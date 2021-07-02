package uk.gov.hmcts.reform.hmc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.hmc.BaseTest;
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
        stubSuccessfullyGetResponseFromCft(caseListingId);
        hearingManagementService.processRequest(caseListingId, TestingUtil.getHearingRequest());
    }

}
