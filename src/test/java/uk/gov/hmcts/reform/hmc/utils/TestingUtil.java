package uk.gov.hmcts.reform.hmc.utils;

import uk.gov.hmcts.reform.hmc.client.model.hmi.ErrorDetails;
import uk.gov.hmcts.reform.hmc.client.model.hmi.Hearing;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.MetaResponse;

import java.time.LocalDateTime;

public  class TestingUtil {

    private TestingUtil() {
    }

    public static HearingDetailsRequest getErrorRequest(int errorCode) {
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
        hearingResponse.setMetaResponse(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingIdCaseHQ("47743382");
        hearing.setHearingType("type");
        hearing.setHearingStartTime(LocalDateTime.now());
        hearing.setHearingEndTime(LocalDateTime.now());
        hearing.setHearingCaseIdHmcts("SW710014");
        hearing.setHearingTranslatorRequired(false);
        hearing.setHearingTranslatorLanguage("test");
        hearing.setHearingCreatedDate(LocalDateTime.now());
        hearing.setHearingCreatedBy("sysadm");
        hearing.setHearingVenueId("300");
        hearing.setHearingJudgeId("test");
        hearingResponse.setHearing(hearing);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingDetailsRequest getHearingOptionalFields() {
        HearingDetailsRequest request = getHearingRequest();
        request.getHearingResponse().getMetaResponse().setApiVersion("version1");
        request.getHearingResponse().getHearing().setHearingSessionIdCaseHQ(123);
        return request;
    }

    public static HearingDetailsRequest getHearingRequestMandatoryFieldMissing() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        hearingResponse.setMetaResponse(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingType("type");
        hearing.setHearingCaseIdHmcts("SW710014");
        hearing.setHearingTranslatorRequired(false);
        hearing.setHearingTranslatorLanguage("test");
        hearing.setHearingCreatedDate(LocalDateTime.now());
        hearing.setHearingCreatedBy("sysadm");
        hearing.setHearingVenueId("300");
        hearingResponse.setHearing(hearing);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);
        return request;
    }

    public static HearingDetailsRequest getMetaRequestMandatoryFieldMissing() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setApiVersion("version");
        hearingResponse.setMetaResponse(metaResponse);

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
