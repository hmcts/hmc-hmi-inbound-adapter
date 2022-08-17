package uk.gov.hmcts.reform.hmc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.hmc.BaseTest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubSuccessfullyGetResponseFromCft;

class HearingManagementServiceIT extends BaseTest {

    @Autowired
    private HearingManagementService hearingManagementService;

    @Autowired
    private CftHearingService cftHearingService;

    private final String caseListingId = "test-listing-id";

    @Test
    void testProcessRequest() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170");
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        hearingManagementService.processRequest(caseListingId, hearingRequest);
    }


    @Test
    void testProcessRequestAwaiting_listing() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170");
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().getHearingCaseStatus().setCode(HearingCode.AWAITING_LISTING.getNumber());
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        hearingManagementService.processRequest(caseListingId, hearingRequest);
    }

}
