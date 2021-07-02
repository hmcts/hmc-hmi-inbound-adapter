package uk.gov.hmcts.reform.hmc.service;

import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;

public interface HearingManagementService {

    void processRequest(String caseId, HearingDetailsRequest hearingDetailsRequest);

}
