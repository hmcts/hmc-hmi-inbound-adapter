package uk.gov.hmcts.reform.hmc.service.hmi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRqst;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
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
    public void processRequest(String caseId, HearingDetailsRqst hearingDetailsRqst) {
        cftHearingService.isValidCaseId(caseId);
        isValidRequest(hearingDetailsRqst);
        validateHmiHearingRequest(hearingDetailsRqst);
    }

    private void isValidRequest(HearingDetailsRqst hearingDetailsRqst) {
        log.info("Validating hearing response");
        if (hearingDetailsRqst.getHearingResponse() != null && hearingDetailsRqst.getErrorDetails() != null) {
            throw new BadRequestException(INVALID_HEARING_PAYLOAD);
        }
    }

    private void validateHmiHearingRequest(HearingDetailsRqst hearingDetailsRqst) {
        if (null != hearingDetailsRqst.getErrorDetails()) {
            isValidErrorDetails(hearingDetailsRqst);
        }
        if (null != hearingDetailsRqst.getHearingResponse()) {
            sendHearingRspToQueue(hearingDetailsRqst);
        }
    }

    private void isValidErrorDetails(HearingDetailsRqst hearingDetailsRqst) {
        log.info("Validating hearing response error details");
        if (null != hearingDetailsRqst.getErrorDetails().getErrorCode()
            && CASE_LISTING_ERROR_CODE != hearingDetailsRqst.getErrorDetails().getErrorCode()) {
            throw new BadRequestException(INVALID_ERROR_CODE_ERR_MESSAGE);
        } else {
            sendHearingRspToQueue(hearingDetailsRqst);
        }
    }

    private void sendHearingRspToQueue(Object response) {
        var jsonNode  = objectMapperService.convertObjectToJsonNode(response);
        messageSenderConfiguration.sendMessageToQueue(jsonNode.toString());
    }

}
