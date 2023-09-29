package uk.gov.hmcts.reform.hmc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.reform.hmc.exceptions.ServiceException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_STATE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.LATEST_HEARING_REQUEST_VERSION;
import static uk.gov.hmcts.reform.hmc.constants.Constants.VERSION_NOT_SUPPLIED;

class CftHearingServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApplicationParams applicationParams;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private CftHearingServiceImpl cftHearingService;

    private String cftBaseUrl = "cftBaseURL";

    private String validCaseId = "Case1234";
    private String inValidCaseId = "Case1111";

    HttpHeaders responseHeaders = new HttpHeaders();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        given(applicationParams.cftHearingValidateCaseIdUrl(Mockito.anyString())).willReturn(cftBaseUrl);
        given(applicationParams.getHmcHearingTerminalStates()).willReturn(List.of("COMPLETED","ADJOURNED","CANCELLED"));
        given(securityUtils.authorizationHeaders()).willReturn(new HttpHeaders());
        responseHeaders.set("Latest-Hearing-Request-Version", "1");
        responseHeaders.set("Latest-Hearing-Status", "LISTED");
    }

    @Test
    void shouldFailToGetLatestVersionFromHeader() {
        HttpHeaders responseHeaders = new HttpHeaders();
        final ResourceNotFoundException expectedException =
                assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getLatestVersion(responseHeaders,
                    validCaseId));
        assertEquals(VERSION_NOT_SUPPLIED.replace("%s", validCaseId), expectedException.getMessage());
    }

    @Test
    void shouldSucceedToGetLatestVersionFromHeader() {
        responseHeaders.set("Latest-Hearing-Request-Version", "170");
        final String versionValue = "170";
        assertEquals(170, cftHearingService.getLatestVersion(responseHeaders, validCaseId));
    }

    @Test
    void shouldSuccessfullyValidateCaseId() {
        final String versionValue = "170";
        ResponseEntity responseEntity = ResponseEntity.status(204)
                .header(LATEST_HEARING_REQUEST_VERSION, versionValue).build();

        doReturn(responseEntity).when(restTemplate).exchange(anyString(),
                                                                                 eq(HttpMethod.GET),
                                                                                 any(HttpEntity.class),
                                                                                 eq(HttpStatus.class));
        assertTrue(responseEntity.getHeaders().containsKey(LATEST_HEARING_REQUEST_VERSION));
        assertEquals(responseEntity.getHeaders().get(LATEST_HEARING_REQUEST_VERSION)
                        .get(0), versionValue);
    }

    @Test
    void shouldFailToValidateCaseIdNotFound() {
        responseHeaders.set("Latest-Hearing-Request-Version", inValidCaseId);
        Exception exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ResourceNotFoundException expectedException =
            assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getHearingVersionHeaders(
                                                                                                   inValidCaseId));
        assertEquals("Hearing Case Id:'Case1111' not found", expectedException.getMessage());

    }

    @Test
    void shouldFailToValidateCaseIdBadRequest() {
        Exception exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ResourceNotFoundException expectedException =
            assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getHearingVersionHeaders(
                inValidCaseId));
        assertEquals("Hearing Case Id:'Case1111' not found", expectedException.getMessage());

    }

    @Test
    void shouldFailToValidateCaseIdWhenUnAuthorised() {
        HttpHeaders responseHeaders = new HttpHeaders();
        Exception exception = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ResourceNotFoundException expectedException =
            assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getHearingVersionHeaders(
                                                                                                   inValidCaseId));
        assertEquals("Hearing Case Id:'Case1111' not found", expectedException.getMessage());

    }

    @Test
    void shouldFailToValidateCaseWhenConnectivityIssue() {
        Exception exception = new RestClientException("connectivity issue");
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ServiceException expectedException =
            assertThrows(ServiceException.class, () -> cftHearingService.getHearingVersionHeaders(validCaseId));
        assertEquals("The CFT service is currently down, please refresh "
                         +  "your browser or try again later", expectedException.getMessage());
    }

    @Test
    void shouldFailAsHearingInTerminalState_Completed() {
        responseHeaders.set("Latest-Hearing-Status","COMPLETED");
        final BadRequestException expectedException =
            assertThrows(BadRequestException.class, () -> cftHearingService.checkHearingInTerminalState(responseHeaders,
                                                                                                        validCaseId));
        assertEquals(INVALID_HEARING_STATE, expectedException.getMessage());
    }

    @Test
    void shouldFailAsHearingInTerminalState_Adjourned() {
        responseHeaders.set("Latest-Hearing-Status","ADJOURNED");
        final BadRequestException expectedException =
            assertThrows(BadRequestException.class, () -> cftHearingService.checkHearingInTerminalState(responseHeaders,
                                                                                                        validCaseId));
        assertEquals(INVALID_HEARING_STATE, expectedException.getMessage());
    }

    @Test
    void shouldFailAsHearingInTerminalState_Cancelled() {
        responseHeaders.set("Latest-Hearing-Status","CANCELLED");
        final BadRequestException expectedException =
            assertThrows(BadRequestException.class, () -> cftHearingService.checkHearingInTerminalState(responseHeaders,
                                                                                                        validCaseId));
        assertEquals(INVALID_HEARING_STATE, expectedException.getMessage());
    }

    @Test
    void shouldPassAsHearingNotInTerminalState() {
        cftHearingService.checkHearingInTerminalState(responseHeaders, validCaseId);
    }
}
