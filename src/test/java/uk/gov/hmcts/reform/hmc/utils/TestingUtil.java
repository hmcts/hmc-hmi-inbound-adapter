package uk.gov.hmcts.reform.hmc.utils;

import uk.gov.hmcts.reform.hmc.client.model.hmi.ErrorDetails;
import uk.gov.hmcts.reform.hmc.client.model.hmi.Hearing;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCaseStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.ListingStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.MetaResponse;

import java.time.LocalDateTime;

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
        hearingCaseStatus.setCode(HearingCode.CLOSED);
        hearingCaseStatus.setDescription("value");
        hearing.setHearingCaseStatus(hearingCaseStatus);
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

    public static HearingDetailsRequest getHearingWithCodesRequest() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        metaResponse.setTimestamp(LocalDateTime.now());
        hearingResponse.setMeta(metaResponse);

        Hearing hearing = new Hearing();
        hearing.setHearingCaseVersionId(123);
        HearingCaseStatus hearingCaseStatus = new HearingCaseStatus();
        hearingCaseStatus.setCode(HearingCode.CLOSED);
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
        hearingCaseStatus.setCode(HearingCode.CLOSED);
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
