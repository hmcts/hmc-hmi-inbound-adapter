package uk.gov.hmcts.reform.hmc.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingVenue;
import uk.gov.hmcts.reform.hmc.client.model.hmi.VenueLocationReference;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
import uk.gov.hmcts.reform.hmc.config.MessageType;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.service.common.ObjectMapperService;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_ERROR_CODE_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_PAYLOAD;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_LOCATION_REFERENCES;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_VERSION;

@Service
@Slf4j
public class HearingManagementServiceImpl implements HearingManagementService {

    private static final String EPIMS = "EPIMS";

    private final MessageSenderConfiguration messageSenderConfiguration;

    private final ObjectMapperService objectMapperService;

    private final CftHearingService cftHearingService;

    @Autowired
    public HearingManagementServiceImpl(final MessageSenderConfiguration messageSenderConfiguration,
                                        ObjectMapperService objectMapperService,
                                        CftHearingService cftHearingService) {
        this.messageSenderConfiguration = messageSenderConfiguration;
        this.objectMapperService = objectMapperService;
        this.cftHearingService = cftHearingService;
    }

    @Override
    public void processRequest(String caseId, HearingDetailsRequest hearingDetailsRequest) {
        log.debug("Hearing response received for hearing ID {}, ", caseId);
        HttpHeaders headers = cftHearingService.getHearingVersionHeaders(caseId);
        val latestVersion = cftHearingService.getLatestVersion(headers, caseId);
        cftHearingService.checkHearingInTerminalState(headers, caseId);
        if (isAwaitingListingStatus(hearingDetailsRequest)) {
            log.info(
                "Hearing response received for hearing ID {}, version {} with hearingCaseStatus {} (Awaiting Listing)",
                caseId,
                latestVersion,
                HearingCode.AWAITING_LISTING.getNumber()
            );
            return;
        }
        isValidRequest(hearingDetailsRequest);
        validateHmiHearingRequest(hearingDetailsRequest, caseId, latestVersion);
    }

    private boolean isAwaitingListingStatus(HearingDetailsRequest hearingDetailsRequest) {

        return hearingDetailsRequest.getHearingResponse() != null
            && hearingDetailsRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            .getCode().equals(HearingCode.AWAITING_LISTING.getNumber());
    }

    public void validateRequestVersion(HearingDetailsRequest hearingDetailsRequest,
                                       Integer latestHearingRequestVersion) {
        Integer hearingCaseVersionId = 0;
        if (null != hearingDetailsRequest.getHearingResponse()
            && null != hearingDetailsRequest.getHearingResponse().getHearing()
            && null != hearingDetailsRequest.getHearingResponse().getHearing().getHearingCaseVersionId()) {
            hearingCaseVersionId = hearingDetailsRequest.getHearingResponse().getHearing().getHearingCaseVersionId();
        }
        if (latestHearingRequestVersion.intValue() != hearingCaseVersionId) {
            log.warn("Error while validating case version against latest request version: {}, {}",
                    hearingCaseVersionId, latestHearingRequestVersion);
            throw new BadRequestException(INVALID_VERSION);
        }
    }

    private void isValidRequest(HearingDetailsRequest hearingDetailsRequest) {
        log.debug("Validating hearing response");
        if (hearingDetailsRequest.getHearingResponse() != null && hearingDetailsRequest.getErrorDetails() != null) {
            throw new BadRequestException(INVALID_HEARING_PAYLOAD);
        }

        if (hearingDetailsRequest.getHearingResponse() != null) {

            if (null != hearingDetailsRequest.getHearingResponse().getHearing().getHearingVenue()) {
                final HearingVenue hearingVenue = hearingDetailsRequest.getHearingResponse()
                        .getHearing().getHearingVenue();
                if (!CollectionUtils.isEmpty(hearingVenue.getLocationReferences())) {
                    getLocationReference(hearingVenue.getLocationReferences());
                }
            }

            if (null != hearingDetailsRequest.getHearingResponse().getHearing().getHearingSessions()) {
                hearingDetailsRequest.getHearingResponse().getHearing().getHearingSessions().forEach(session -> {
                    if (null != session && null != session.getHearingVenue()
                            && !CollectionUtils.isEmpty(session.getHearingVenue().getLocationReferences())) {
                        getLocationReference(session.getHearingVenue().getLocationReferences());
                    }
                });
            }

        }
    }

    private String getLocationReference(List<VenueLocationReference> locationReferences) {
        List<String> references = locationReferences.stream()
                .map(VenueLocationReference::getKey)
                .filter(key -> key.equalsIgnoreCase(EPIMS))
                .collect(Collectors.toUnmodifiableList());
        if (references.size() == 1) {
            return references.get(0);
        } else {
            throw new BadRequestException(INVALID_LOCATION_REFERENCES);
        }
    }

    private void validateHmiHearingRequest(HearingDetailsRequest hearingDetailsRequest, String caseId,
                                           Integer latestVersion) {
        if (null != hearingDetailsRequest.getErrorDetails()) {
            isValidErrorDetails(hearingDetailsRequest, caseId);
        } else if (!hearingIsClosed(hearingDetailsRequest)) {
            validateRequestVersion(hearingDetailsRequest, latestVersion);
        }
        if (null != hearingDetailsRequest.getHearingResponse()) {
            log.debug("caseId {} has hearingResponse {} ", caseId,
                      hearingDetailsRequest.getHearingResponse().getHearing().getHearingCaseStatus().getDescription());
            sendHearingRspToQueue(hearingDetailsRequest.getHearingResponse(), MessageType.HEARING_RESPONSE, caseId);
        }
    }

    private boolean hearingIsClosed(HearingDetailsRequest hearingDetailsRequest) {
        return null != hearingDetailsRequest
            && null != hearingDetailsRequest.getHearingResponse()
            && null != hearingDetailsRequest.getHearingResponse().getHearing()
            && null != hearingDetailsRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            && HearingCode.CLOSED.getNumber()
                .equals(hearingDetailsRequest.getHearingResponse().getHearing().getHearingCaseStatus().getCode());
    }

    private void isValidErrorDetails(HearingDetailsRequest hearingDetailsRequest, String caseId) {
        log.debug("Validating hearing response error details");
        if (null == hearingDetailsRequest.getErrorDetails().getErrorCode()) {
            throw new BadRequestException(INVALID_ERROR_CODE_ERR_MESSAGE);
        } else {
            log.debug("hearing response has error code for caseId {}", caseId);
            sendHearingRspToQueue(hearingDetailsRequest.getErrorDetails(), MessageType.ERROR, caseId);
        }
    }

    private void sendHearingRspToQueue(Object response, MessageType messageType, String caseId) {
        log.debug("Sending hearing response to Queue for caseId {}", caseId);
        var jsonNode  = objectMapperService.convertObjectToJsonNode(response);
        messageSenderConfiguration.sendMessage(jsonNode.toString(), messageType, caseId);
    }

}
