package uk.gov.hmcts.reform.hmc.service;

import uk.gov.hmcts.reform.hmc.client.hmi.model.response.CftHearingServiceRsp;
import uk.gov.hmcts.reform.hmc.client.hmi.model.response.HearingManagementInterfaceRsp;

public interface HearingManagementInterfaceService {

    HearingManagementInterfaceRsp execute(String caseId);

    HearingManagementInterfaceRsp getResponseFromHmi(String caseId);

    CftHearingServiceRsp isValidCaseId(String caseId);
}
