package uk.gov.hmcts.reform.hmc.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.config.MessageSenderConfiguration;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.service.common.ObjectMapperService;
import uk.gov.hmcts.reform.hmc.service.hmi.CftHearingServiceImpl;
import uk.gov.hmcts.reform.hmc.service.hmi.HearingManagementServiceImpl;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_ERROR_CODE_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_PAYLOAD;

class HearingManagementServiceImplTest {

    @Mock
    private CftHearingServiceImpl cftHearingService;

    @Mock
    private ApplicationParams applicationParams;

    @Mock
    private ObjectMapperService objectMapperService;

    @InjectMocks
    private HearingManagementServiceImpl hearingManagementService;

    @Mock
    private MessageSenderConfiguration messageSenderConfiguration;

    private String validCaseId = "Case1234";
    private String inValidCaseId = "Case1111";

    JsonNode jsonNode = mock(JsonNode.class);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
     void shouldFailAsRequestHasHearingAndErrorDetails() {
        given(cftHearingService.isValidCaseId(validCaseId)).willReturn(true);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            hearingManagementService.processRequest(validCaseId, TestingUtil.getHearingAndErrorRequest());
        });
        assertEquals(INVALID_HEARING_PAYLOAD, exception.getMessage());
        assertThat(exception).isInstanceOf(BadRequestException.class);
    }

    @Test
     void shouldFailAsErrorCodeIsInValid() {
        given(cftHearingService.isValidCaseId(validCaseId)).willReturn(true);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            hearingManagementService.processRequest(validCaseId, TestingUtil.getErrorRequest(100));
        });
        assertEquals(INVALID_ERROR_CODE_ERR_MESSAGE, exception.getMessage());
        assertThat(exception).isInstanceOf(BadRequestException.class);
    }

    @Test
     void shouldPassWithOptionalErrorDetails() {
        HearingDetailsRequest request = TestingUtil.getErrorRequestWithOptionalFields();
        given(cftHearingService.isValidCaseId(validCaseId)).willReturn(true);
        when(objectMapperService.convertObjectToJsonNode(request)).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
    }


    @Test
     void shouldPassWithOptionalHearingDetails() {
        HearingDetailsRequest request = TestingUtil.getHearingOptionalFields();
        given(cftHearingService.isValidCaseId(validCaseId)).willReturn(true);
        when(objectMapperService.convertObjectToJsonNode(request)).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
    }

    @Test
     void shouldFailAsHearingMandatoryFieldsMissing() {
        HearingDetailsRequest request = TestingUtil.getHearingRequestMandatoryFieldMissing();
        given(cftHearingService.isValidCaseId(validCaseId)).willReturn(true);
        when(objectMapperService.convertObjectToJsonNode(request)).thenReturn(jsonNode);
        hearingManagementService.processRequest(validCaseId, request);
    }

}
