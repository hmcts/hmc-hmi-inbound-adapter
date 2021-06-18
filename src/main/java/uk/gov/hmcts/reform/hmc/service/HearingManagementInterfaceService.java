package uk.gov.hmcts.reform.hmc.service;

import uk.gov.hmcts.reform.hmc.client.hmi.model.response.CftHearingServiceRsp;
import uk.gov.hmcts.reform.hmc.client.hmi.model.response.HearingManagementInterfaceRsp;

public interface HearingManagementInterfaceService {

    HearingManagementInterfaceRsp getResponseFromHmi(Long hearingId);

    CftHearingServiceRsp isValidHearingId(Long hearingId);
}
