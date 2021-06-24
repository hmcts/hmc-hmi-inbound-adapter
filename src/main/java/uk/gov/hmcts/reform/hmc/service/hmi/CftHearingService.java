package uk.gov.hmcts.reform.hmc.service.hmi;

import uk.gov.hmcts.reform.hmc.client.model.hmi.CftHearingServiceRsp;

public interface CftHearingService {

    CftHearingServiceRsp isValidCaseId(String caseId);
}
