package uk.gov.hmcts.reform.hmc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.hmc.BaseTest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.constants.Constants;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubSuccessfullyGetResponseFromCft;
import static uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode.LISTED;
import static uk.gov.hmcts.reform.hmc.constants.Constants.ADJOURNED;
import static uk.gov.hmcts.reform.hmc.constants.Constants.CANCELLED;
import static uk.gov.hmcts.reform.hmc.constants.Constants.COMPLETED;

class HearingManagementServiceIT extends BaseTest {

    @Autowired
    private HearingManagementService hearingManagementService;

    @Autowired
    private CftHearingService cftHearingService;

    private final String caseListingId = "test-listing-id";

    @Test
    void testProcessRequest() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170",LISTED.getLabel());
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        hearingManagementService.processRequest(caseListingId, hearingRequest);
    }

    @Test
    void testProcessRequestAwaiting_listing() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170", LISTED.getLabel());
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            .setCode(HearingCode.AWAITING_LISTING.getNumber());
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        hearingManagementService.processRequest(caseListingId, hearingRequest);
    }

    @Test
    void testProcessRequestForHearingStatusAdjourned() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170", ADJOURNED);
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            .setCode(HearingCode.AWAITING_LISTING.getNumber());
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
               () -> hearingManagementService.processRequest(caseListingId, hearingRequest));
        assertEquals(Constants.INVALID_HEARING_STATE, badRequestException.getMessage());
    }

    @Test
    void testProcessRequestForHearingStatusCompleted() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170", COMPLETED);
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            .setCode(HearingCode.AWAITING_LISTING.getNumber());
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                   () -> hearingManagementService.processRequest(caseListingId, hearingRequest));
        assertEquals(Constants.INVALID_HEARING_STATE, badRequestException.getMessage());
    }

    @Test
    void testProcessRequestForHearingStatusCancelled() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170", CANCELLED);
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            .setCode(HearingCode.AWAITING_LISTING.getNumber());
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                   () -> hearingManagementService.processRequest(caseListingId, hearingRequest));
        assertEquals(Constants.INVALID_HEARING_STATE, badRequestException.getMessage());
    }

    @Test
    void testProcessRequestForHearingStatusMandatory() {
        stubSuccessfullyGetResponseFromCft(caseListingId, "170", LISTED.getLabel());
        HearingDetailsRequest hearingRequest = TestingUtil.getHearingRequest();
        hearingRequest.getHearingResponse().getHearing().setHearingStatus(null);
        hearingRequest.getHearingResponse().getHearing().getHearingCaseStatus()
            .setCode(LISTED.getNumber());
        hearingRequest.getHearingResponse().getHearing().setHearingCaseVersionId(170);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                                    () -> hearingManagementService.processRequest(caseListingId, hearingRequest));
        assertEquals(Constants.INVALID_HEARING_STATUS, badRequestException.getMessage());
    }

}