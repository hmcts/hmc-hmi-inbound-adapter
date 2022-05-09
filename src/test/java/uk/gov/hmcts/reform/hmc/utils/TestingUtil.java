package uk.gov.hmcts.reform.hmc.utils;

import uk.gov.hmcts.reform.hmc.client.model.hmi.ErrorDetails;
import uk.gov.hmcts.reform.hmc.client.model.hmi.Hearing;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingAttendee;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCaseStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingJoh;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingRoom;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingSession;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingVenue;
import uk.gov.hmcts.reform.hmc.client.model.hmi.ListingStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.MetaResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.VenueLocationReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public  class TestingUtil {

    private TestingUtil() {
    }

    public static HearingDetailsRequest getErrorRequest(Integer errorCode) {
        final HearingDetailsRequest request = new HearingDetailsRequest();

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(errorCode);
        errorDetails.setErrorDescription("optional");
        errorDetails.setErrorLinkId("link");
        request.setErrorDetails(errorDetails);
        return request;
    }

    public static HearingDetailsRequest getErrorRequestWithOptionalFields() {
        HearingDetailsRequest request = new HearingDetailsRequest();

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(2000);
        errorDetails.setErrorDescription("optional");

        request.setErrorDetails(errorDetails);
        return request;
    }

    public static HearingDetailsRequest getHearingRequest() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        metaResponse.setTimestamp(LocalDateTime.now());
        hearingResponse.setMeta(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingCaseVersionId(123);
        HearingCaseStatus hearingCaseStatus = new HearingCaseStatus();
        hearingCaseStatus.setCode(String.valueOf(HearingCode.getNumber(HearingCode.LISTED)));
        hearingCaseStatus.setDescription("value");
        hearing.setHearingCaseStatus(hearingCaseStatus);
        hearing.setHearingIdCaseHQ("47743382");
        hearing.setHearingStartTime(LocalDateTime.now());
        hearing.setHearingEndTime(LocalDateTime.now());
        hearing.setHearingCaseIdHmcts("SW710014");
        hearing.setHearingTranslatorRequired(false);
        hearing.setHearingCreatedDate(LocalDateTime.now());
        hearing.setHearingCreatedBy("sysadm");
        hearing.setHearingSessions(List.of(getHearingSession()));
        hearingResponse.setHearing(hearing);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingSession getHearingSession() {
        HearingSession hearingSession = new HearingSession();
        hearingSession.setHearingCaseHQ("21333");
        hearingSession.setHearingStartTime(LocalDateTime.now());
        hearingSession.setHearingEndTime(LocalDateTime.now().plusHours(4));
        hearingSession.setHearingSequence(1);
        hearingSession.setHearingPrivate(true);
        hearingSession.setHearingRisk(true);
        hearingSession.setHearingTranslatorRequired(false);
        hearingSession.setHearingVenue(getHearingVenue());
        hearingSession.setHearingRoom(getHearingRoom());
        hearingSession.setHearingVhStatus("vh status");
        hearingSession.setHearingAttendee(getHearingAttendee());
        hearingSession.setHearingJoh(getHearingJohs());
        return hearingSession;
    }

    public static HearingStatus getHearingStatus(ListingStatus listingStatus) {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setDescription("status desc");
        hearingStatus.setCode(listingStatus);
        return hearingStatus;
    }

    public static HearingVenue getHearingVenue() {
        HearingVenue hearingVenue = new HearingVenue();
        hearingVenue.setLocationName("London");
        return hearingVenue;
    }

    public static HearingRoom getHearingRoom() {
        HearingRoom hearingRoom = new HearingRoom();
        hearingRoom.setLocationName("room1");
        return hearingRoom;
    }

    public static ArrayList<HearingAttendee> getHearingAttendee() {
        HearingAttendee hearingAttendee = new HearingAttendee();
        hearingAttendee.setEntityId("attendeeId");
        ArrayList<HearingAttendee> hearingAttendees = new ArrayList<>();
        hearingAttendees.add(hearingAttendee);
        return hearingAttendees;
    }

    public static ArrayList<HearingJoh> getHearingJohs() {
        HearingJoh hearingJoh = new HearingJoh();
        hearingJoh.setJohId("johId");
        ArrayList<HearingJoh> hearingJohs = new ArrayList<>();
        hearingJohs.add(hearingJoh);
        return hearingJohs;
    }

    public static HearingDetailsRequest getHearingWithCodesRequest() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        metaResponse.setTimestamp(LocalDateTime.now());
        hearingResponse.setMeta(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingCaseVersionId(123);
        HearingCaseStatus hearingCaseStatus = new HearingCaseStatus();
        hearingCaseStatus.setCode(String.valueOf(HearingCode.getNumber(HearingCode.LISTED)));
        hearingCaseStatus.setDescription("value");
        hearing.setHearingCaseStatus(hearingCaseStatus);
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setDescription("vale");
        hearingStatus.setCode(ListingStatus.DRAFT);
        hearing.setHearingStatus(hearingStatus);
        hearing.setHearingIdCaseHQ("47743382");
        hearing.setHearingStartTime(LocalDateTime.now());
        hearing.setHearingEndTime(LocalDateTime.now());
        hearing.setHearingCaseIdHmcts("SW710014");
        hearing.setHearingTranslatorRequired(false);
        hearing.setHearingCreatedDate(LocalDateTime.now());
        hearing.setHearingCreatedBy("sysadm");
        hearingResponse.setHearing(hearing);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingDetailsRequest getHearingWithInvalidHearingStatusCode() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        metaResponse.setTimestamp(LocalDateTime.now());
        hearingResponse.setMeta(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingCaseVersionId(123);
        HearingCaseStatus hearingCaseStatus = new HearingCaseStatus();
        hearingCaseStatus.setCode("1");
        hearingCaseStatus.setDescription("value");
        hearing.setHearingCaseStatus(hearingCaseStatus);
        hearing.setHearingStatus(getHearingStatus(ListingStatus.DRAFT));
        hearing.setHearingIdCaseHQ("47743382");
        hearing.setHearingStartTime(LocalDateTime.now());
        hearing.setHearingEndTime(LocalDateTime.now());
        hearing.setHearingCaseIdHmcts("SW710014");
        hearing.setHearingTranslatorRequired(false);
        hearing.setHearingCreatedDate(LocalDateTime.now());
        hearing.setHearingCreatedBy("sysadm");
        hearing.setHearingVenue(getHearingVenue("EPIMS"));
        hearingResponse.setHearing(hearing);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingDetailsRequest getHearingOptionalFields() {
        HearingDetailsRequest request = getHearingRequest();
        request.getHearingResponse().getMeta().setTransactionIdCaseHQ("version1");
        request.getHearingResponse().getHearing().setHearingCaseIdHmcts("123");
        return request;
    }

    public static HearingDetailsRequest getHearingRequestMandatoryFieldMissing() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        hearingResponse.setMeta(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingCaseVersionId(123);
        HearingCaseStatus hearingCaseStatus = new HearingCaseStatus();
        hearingCaseStatus.setCode(String.valueOf(HearingCode.getNumber(HearingCode.LISTED)));
        hearingCaseStatus.setDescription("value");
        hearing.setHearingCaseStatus(hearingCaseStatus);
        hearing.setHearingCaseIdHmcts("SW710014");
        hearing.setHearingTranslatorRequired(false);
        hearing.setHearingCreatedDate(LocalDateTime.now());
        hearing.setHearingCreatedBy("sysadm");
        hearingResponse.setHearing(hearing);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingDetailsRequest getHearingVenueLocationReferencesKeyDoesNotEqualsEpims() {
        final HearingDetailsRequest hearingDetailsRequest = getHearingRequest();
        final HearingResponse hearingResponse = hearingDetailsRequest.getHearingResponse();
        final Hearing hearing = hearingResponse.getHearing();
        hearing.setHearingVenue(getHearingVenue("notEPIMSKey"));

        hearingResponse.setHearing(hearing);
        hearingDetailsRequest.setHearingResponse(hearingResponse);

        return hearingDetailsRequest;

    }

    private static HearingVenue getHearingVenue(String key) {
        VenueLocationReference venueLocationReference = new VenueLocationReference();
        venueLocationReference.setKey(key);

        HearingVenue hearingVenue = new HearingVenue();
        hearingVenue.setLocationReferences(List.of(venueLocationReference));
        return hearingVenue;
    }

    public static HearingDetailsRequest getMetaRequestMandatoryFieldMissing() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("version");
        hearingResponse.setMeta(metaResponse);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingDetailsRequest getHearingAndErrorRequest() {
        HearingDetailsRequest request = getHearingRequest();

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorCode(2000);
        request.setErrorDetails(errorDetails);

        return request;
    }
}
