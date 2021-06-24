package uk.gov.hmcts.reform.hmc.service.hmi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRqst;

public interface HearingManagementService {

    ResponseEntity<HttpStatus> execute(String caseId, HearingDetailsRqst hearingDetailsRqst);

}
