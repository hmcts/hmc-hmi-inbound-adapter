package uk.gov.hmcts.reform.hmc.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingResponse;
import uk.gov.hmcts.reform.hmc.client.model.hmi.MetaResponse;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
import uk.gov.hmcts.reform.hmc.constants.Constants;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.service.common.ObjectMapperService;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import java.time.LocalDateTime;

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
    private ApplicationParams applicationParams;

    @Mock
    private ObjectMapperService objectMapperService;

    private HearingManagementServiceImpl hearingManagementService;

    @Mock
    private MessageSenderConfiguration messageSenderConfiguration;

    private final String validCaseId = "Case1234";

    JsonNode jsonNode = mock(JsonNode.class);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hearingManagementService = new HearingManagementServiceImpl(messageSenderConfiguration,
                                                                    objectMapperService,
                                                                    cftHearingService);
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
        given(cftHearingService.getLatestVersion(validCaseId)).willReturn(123);
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
        given(cftHearingService.getLatestVersion(validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getErrorDetails())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getLatestVersion(any());
    }

    @Test
    void shouldPassWithOptionalErrorDetails() {
        HearingDetailsRequest request = TestingUtil.getErrorRequestWithOptionalFields();
        when(objectMapperService.convertObjectToJsonNode(request.getErrorDetails())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getLatestVersion(any());
    }


    @Test
     void shouldPassWithOptionalHearingDetails() {
        HearingDetailsRequest request = TestingUtil.getHearingOptionalFields();
        given(cftHearingService.getLatestVersion(validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getLatestVersion(any());
    }

    @Test
     void shouldFailAsHearingMandatoryFieldsMissing() {
        HearingDetailsRequest request = TestingUtil.getHearingRequestMandatoryFieldMissing();
        given(cftHearingService.getLatestVersion(validCaseId)).willReturn(123);
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
        verify(cftHearingService, times(1)).getLatestVersion(any());
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
    void shouldFailAsHearingVenueLocationReferencesKeyEqualsEpimsMissing() {
        HearingDetailsRequest request = TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims();
        when(objectMapperService.convertObjectToJsonNode(request.getHearingResponse())).thenReturn(jsonNode);
        final BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> hearingManagementService.processRequest(validCaseId, request));
        assertEquals(Constants.INVALID_LOCATION_REFERENCES, badRequestException.getMessage());
        verify(cftHearingService, times(1)).getLatestVersion(any());
    }
}
