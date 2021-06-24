package uk.gov.hmcts.reform.hmc.service.hmi;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRqst;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.service.common.ObjectMapperService;

import static uk.gov.hmcts.reform.hmc.constants.Constants.CASE_LISTING_ERROR_CODE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_ERROR_CODE_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_PAYLOAD;

@Service
public class HearingManagementServiceImpl implements HearingManagementService {

    private static final Logger logger = LoggerFactory.getLogger(HearingManagementServiceImpl.class);

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
    public ResponseEntity<HttpStatus> execute(String caseId, HearingDetailsRqst hearingDetailsRqst) {
        cftHearingService.isValidCaseId(caseId);
        isValidRequest(hearingDetailsRqst);
        return validateHmiHearingRequest(hearingDetailsRqst);
    }

    private void isValidRequest(HearingDetailsRqst hearingDetailsRqst) {
        logger.info("Validating hearing response");
        if (hearingDetailsRqst.getHearingResponse() != null && hearingDetailsRqst.getErrorDetails() != null) {
            throw new BadRequestException(INVALID_HEARING_PAYLOAD);
        }
    }

    private ResponseEntity<HttpStatus> validateHmiHearingRequest(HearingDetailsRqst hearingDetailsRqst) {
        if (null != hearingDetailsRqst.getErrorDetails()) {
            return isValidErrorDetails(hearingDetailsRqst);
        }
        if (null != hearingDetailsRqst.getHearingResponse()) {
            logger.info("Hearing details are valid.");
            sendHearingRspToQueue(hearingDetailsRqst.getHearingResponse());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private ResponseEntity<HttpStatus> isValidErrorDetails(HearingDetailsRqst hearingDetailsRqst) {
        logger.info("Validating hearing response error details");
        if (null != hearingDetailsRqst.getErrorDetails().getErrorCode()
            && CASE_LISTING_ERROR_CODE != hearingDetailsRqst.getErrorDetails().getErrorCode()) {
            throw new BadRequestException(INVALID_ERROR_CODE_ERR_MESSAGE);
        } else {
            sendHearingRspToQueue(hearingDetailsRqst.getErrorDetails());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
    }

    private void sendHearingRspToQueue(Object response) {
        JsonNode responseNode = objectMapperService.convertObjectToJsonNode(response);
        messageSenderConfiguration.sendMessageToQueue(responseNode.toString());
    }

}
