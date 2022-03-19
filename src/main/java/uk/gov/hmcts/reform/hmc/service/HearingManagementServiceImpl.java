package uk.gov.hmcts.reform.hmc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
import uk.gov.hmcts.reform.hmc.config.MessageType;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.service.common.ObjectMapperService;

import static uk.gov.hmcts.reform.hmc.constants.Constants.CASE_LISTING_ERROR_CODE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_ERROR_CODE_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_PAYLOAD;

@Service
@Slf4j
public class HearingManagementServiceImpl implements HearingManagementService {

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
        cftHearingService.isValidCaseId(caseId);
        isValidRequest(hearingDetailsRequest);
        validateHmiHearingRequest(hearingDetailsRequest, caseId);
    }

    private void isValidRequest(HearingDetailsRequest hearingDetailsRequest) {
        log.info("Validating hearing response");
        if (hearingDetailsRequest.getHearingResponse() != null && hearingDetailsRequest.getErrorDetails() != null) {
            throw new BadRequestException(INVALID_HEARING_PAYLOAD);
        }
    }

    private void validateHmiHearingRequest(HearingDetailsRequest hearingDetailsRequest, String caseId) {
        if (null != hearingDetailsRequest.getErrorDetails()) {
            isValidErrorDetails(hearingDetailsRequest, caseId);
        }
        if (null != hearingDetailsRequest.getHearingResponse()) {
            sendHearingRspToQueue(hearingDetailsRequest, MessageType.HEARING_RESPONSE, caseId);
        }
    }

    private void isValidErrorDetails(HearingDetailsRequest hearingDetailsRequest, String caseId) {
        log.info("Validating hearing response error details");
        if (null != hearingDetailsRequest.getErrorDetails().getErrorCode()
            && CASE_LISTING_ERROR_CODE != hearingDetailsRequest.getErrorDetails().getErrorCode()) {
            throw new BadRequestException(INVALID_ERROR_CODE_ERR_MESSAGE);
        } else {
            sendHearingRspToQueue(hearingDetailsRequest, MessageType.ERROR, caseId);
        }
    }

    private void sendHearingRspToQueue(Object response, MessageType messageType, String caseId) {
        var jsonNode  = objectMapperService.convertObjectToJsonNode(response);
        messageSenderConfiguration.sendMessage(jsonNode.toString(), messageType, caseId);
    }

}
