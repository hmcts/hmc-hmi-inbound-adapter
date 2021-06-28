package uk.gov.hmcts.reform.hmc.service.hmi;

import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRqst;

public interface HearingManagementService {

    void processRequest(String caseId, HearingDetailsRqst hearingDetailsRqst);

}
