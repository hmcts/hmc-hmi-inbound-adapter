package uk.gov.hmcts.reform.hmc.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.MetaResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.VenueLocationReference;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
import uk.gov.hmcts.reform.hmc.constants.Constants;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.service.common.ObjectMapperService;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_ERROR_CODE_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_PAYLOAD;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_VERSION;

class HearingManagementServiceImplTest {

    @Mock
    private CftHearingServiceImpl cftHearingService;

    @Mock
    private ObjectMapperService objectMapperService;

    private HearingManagementServiceImpl hearingManagementService;

    @Mock
    private MessageSenderConfiguration messageSenderConfiguration;

    private static final String validCaseId = "Case1234";

    JsonNode jsonNode = mock(JsonNode.class);

    HttpHeaders responseHeaders = new HttpHeaders();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hearingManagementService = new HearingManagementServiceImpl(messageSenderConfiguration,
                                                                    objectMapperService,
                                                                    cftHearingService);
        responseHeaders.set("Latest-Hearing-Request-Version", validCaseId);
        responseHeaders.set("Latest-Hearing-Status", "ADJOURNED");
    }

    @Test
     void shouldFailAsRequestHasHearingAndErrorDetails() {
        try {
            hearingManagementService.processRequest(validCaseId, TestingUtil.getHearingAndErrorRequest());
            Assertions.fail("Expected an BadRequestException to be thrown");
        } catch (Exception exception) {
            assertEquals(INVALID_HEARING_PAYLOAD, exception.getMessage());
            assertThat(exception).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    void shouldFailAsErrorCodeIsInValid() {
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        try {
            hearingManagementService.processRequest(validCaseId, TestingUtil.getErrorRequest(null));
            Assertions.fail("Expected an BadRequestException to be thrown");
        } catch (Exception exception) {
            assertEquals(INVALID_ERROR_CODE_ERR_MESSAGE, exception.getMessage());
            assertThat(exception).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    void shouldPassAsErrorCodeIsValid() {
        HearingDetailsRequest request = TestingUtil.getErrorRequest(2000);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getErrorDetails())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldPassWithOptionalErrorDetails() {
        HearingDetailsRequest request = TestingUtil.getErrorRequestWithOptionalFields();
        when(objectMapperService.convertObjectToJsonNode(request.getErrorDetails())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }


    @Test
     void shouldPassWithOptionalHearingDetails() {
        HearingDetailsRequest request = TestingUtil.getHearingOptionalFields();
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldSkipVersionCheckWhenHearingCaseStatusIsClosed() {
        HearingDetailsRequest request = TestingUtil.getHearingRequest();
        request.getHearingResponse().getHearing().getHearingCaseStatus().setCode(HearingCode.CLOSED.getNumber());
        request.getHearingResponse().getHearing().setHearingCaseVersionId(999);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
     void shouldFailAsHearingMandatoryFieldsMissing() {
        HearingDetailsRequest request = TestingUtil.getHearingRequestMandatoryFieldMissing();
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldFailAsHearingRequestVersionDiffersFromLatestVersion() {
        HearingDetailsRequest request = TestingUtil.getHearingRequest();
        try {
            hearingManagementService.validateRequestVersion(request, 29);
            Assertions.fail("Expected an BadRequestException to be thrown");
        } catch (Exception exception) {
            assertEquals(INVALID_VERSION, exception.getMessage());
            assertThat(exception).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
        void shouldFailAsMissingHearingResponse() {
        HearingDetailsRequest request = new HearingDetailsRequest();

        try {
            hearingManagementService.validateRequestVersion(request, 29);
            Assertions.fail("Expected an BadRequestException to be thrown");
        } catch (Exception exception) {
            assertEquals(INVALID_VERSION, exception.getMessage());
            assertThat(exception).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    void shouldFailAsMissingHearing() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        metaResponse.setTimestamp(LocalDateTime.now());
        hearingResponse.setMeta(metaResponse);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);

        try {
            hearingManagementService.validateRequestVersion(request, 29);
            Assertions.fail("Expected an BadRequestException to be thrown");
        } catch (Exception exception) {
            assertEquals(INVALID_VERSION, exception.getMessage());
            assertThat(exception).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    void shouldFailAsMissingHearingCaseVersionId() {
        HearingResponse hearingResponse = new HearingResponse();

        MetaResponse metaResponse = new MetaResponse();
        metaResponse.setTransactionIdCaseHQ("123");
        metaResponse.setTimestamp(LocalDateTime.now());
        hearingResponse.setMeta(metaResponse);

        HearingDetailsRequest request = new HearingDetailsRequest();
        request.setHearingResponse(hearingResponse);

        try {
            hearingManagementService.validateRequestVersion(request, 29);
            Assertions.fail("Expected an BadRequestException to be thrown");
        } catch (Exception exception) {
            assertEquals(INVALID_VERSION, exception.getMessage());
            assertThat(exception).isInstanceOf(BadRequestException.class);
        }
    }

    @Test
    void shouldPassAsHearingVenueLocationReferencesContainsOneEpimsKey() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        List<VenueLocationReference> locationReferences = new ArrayList<>();
        VenueLocationReference reference1 = createVenueLocationReference("EPIMS", "Charlestown");
        locationReferences.add(reference1);
        request.getHearingResponse().getHearing().getHearingVenue().setLocationReferences(locationReferences);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldPassAsSessionHearingVenueLocationReferencesContainsOneEpimsKey() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        request.getHearingResponse().getHearing().setHearingVenue(null);
        List<VenueLocationReference> locationReferences = new ArrayList<>();
        VenueLocationReference reference1 = createVenueLocationReference("EPIMS", "Charlestown");
        VenueLocationReference reference2 = createVenueLocationReference("notEPIMS", "Jamestown");
        locationReferences.addAll(List.of(reference1,reference2));
        request.getHearingResponse().getHearing().getHearingSessions().get(0)
                .getHearingVenue().setLocationReferences(locationReferences);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldPassAsMultipleHearingVenuesEachHaveAtMostLocationReferencesOfOneEpimsKey() {
        List<VenueLocationReference> locationReferences = new ArrayList<>();
        VenueLocationReference reference1 = createVenueLocationReference("EPIMS", "Charlestown");
        VenueLocationReference reference2 = createVenueLocationReference("XXXXX", "Edwardtown");
        VenueLocationReference reference3 = createVenueLocationReference("VVVVV", "Richardtown");
        locationReferences.addAll(List.of(reference1, reference2, reference3));
        List<VenueLocationReference> locationReferences2 = new ArrayList<>();
        VenueLocationReference referenceA = createVenueLocationReference("EPIMS", "Smithtown");
        VenueLocationReference referenceB = createVenueLocationReference("XXXXX", "Jonestown");
        VenueLocationReference referenceC = createVenueLocationReference("VVVVV", "Murphytown");
        locationReferences2.addAll(List.of(referenceA, referenceB, referenceC));
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        request.getHearingResponse().getHearing().getHearingVenue().setLocationReferences(locationReferences);
        request.getHearingResponse().getHearing().getHearingSessions().get(0)
                .getHearingVenue().setLocationReferences(locationReferences2);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldFailAsHearingVenueLocationReferencesKeyEqualsEpimsMissing() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> hearingManagementService.processRequest(validCaseId, request));
        assertEquals(Constants.INVALID_LOCATION_REFERENCES, badRequestException.getMessage());
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldPassAsHearingVenueLocationReferencesIsEmpty() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        List<VenueLocationReference> locationReferences = new ArrayList<>();
        request.getHearingResponse().getHearing().getHearingVenue().setLocationReferences(locationReferences);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldFailAsHearingVenueLocationReferencesKeyEqualsMultipleEpims() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        List<VenueLocationReference> locationReferences = new ArrayList<>();
        VenueLocationReference reference1 = createVenueLocationReference("EPIMS", "Charlestown");
        VenueLocationReference reference2 = createVenueLocationReference("EPIMS", "Jamestown");
        locationReferences.addAll(List.of(reference1, reference2));
        request.getHearingResponse().getHearing().getHearingVenue().setLocationReferences(locationReferences);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> hearingManagementService.processRequest(validCaseId, request));
        assertEquals(Constants.INVALID_LOCATION_REFERENCES, badRequestException.getMessage());
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }

    @Test
    void shouldFailAsHearingVenueLocationReferencesKeyMoreThanMultipleEpims() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        List<VenueLocationReference> locationReferences = new ArrayList<>();
        VenueLocationReference reference1 = createVenueLocationReference("EPIMS", "Charlestown");
        VenueLocationReference reference2 = createVenueLocationReference("EPIMS", "Jamestown");
        VenueLocationReference reference3 = createVenueLocationReference("XXXXX", "Edwardtown");
        VenueLocationReference reference4 = createVenueLocationReference("VVVVV", "Richardtown");
        locationReferences.addAll(List.of(reference1, reference2, reference3, reference4));
        request.getHearingResponse().getHearing().getHearingVenue().setLocationReferences(locationReferences);
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> hearingManagementService.processRequest(validCaseId, request));
        assertEquals(Constants.INVALID_LOCATION_REFERENCES, badRequestException.getMessage());
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
    }


    @Test
    void shouldPassAsHearingForAwaitingListing() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        request.getHearingResponse().getHearing().getHearingCaseStatus()
            .setCode(HearingCode.AWAITING_LISTING.getNumber());
        given(cftHearingService.getLatestVersion(responseHeaders, validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getHearingVersionHeaders(any());
        //VERY THAT NO MESSAGE WAS SENT.
        verify(objectMapperService, times(0)).convertObjectToJsonNode(any());
    }


    private VenueLocationReference createVenueLocationReference(String key, String value) {
        VenueLocationReference reference = new VenueLocationReference();
        reference.setKey(key);
        reference.setValue(value);
        return reference;
    }
}
