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
import uk.gov.hmcts.reform.hmc.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.reform.hmc.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        given(applicationParams.cftHearingValidateCaseIdUrl(Mockito.anyString())).willReturn(cftBaseUrl);
        given(securityUtils.authorizationHeaders()).willReturn(new HttpHeaders());
    }

    @Test
    void shouldSuccessfullyValidateCaseId() {
        final String latestHearingRequestVersion = "latestHearingRequestVersion";
        final String versionValue = "170";
        ResponseEntity responseEntity = ResponseEntity.status(204)
                .header(latestHearingRequestVersion, versionValue).build();

        doReturn(responseEntity).when(restTemplate).exchange(anyString(),
                                                                                 eq(HttpMethod.GET),
                                                                                 any(HttpEntity.class),
                                                                                 eq(HttpStatus.class));
        assertTrue(responseEntity.getHeaders().containsKey(latestHearingRequestVersion));
        assertEquals(responseEntity.getHeaders().get(latestHearingRequestVersion).get(0), versionValue);
    }

    @Test
    void shouldFailToValidateCaseIdNotFound() {
        Exception exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ResourceNotFoundException expectedException =
            assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getLatestVersion(inValidCaseId));
        assertEquals("Hearing Case Id:'Case1111' not found", expectedException.getMessage());

    }

    @Test
    void shouldFailToValidateCaseIdBadRequest() {
        Exception exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ResourceNotFoundException expectedException =
            assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getLatestVersion(inValidCaseId));
        assertEquals("Hearing Case Id:'Case1111' not found", expectedException.getMessage());

    }

    @Test
    void shouldFailToValidateCaseIdWhenUnAuthorised() {
        Exception exception = new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ResourceNotFoundException expectedException =
            assertThrows(ResourceNotFoundException.class, () -> cftHearingService.getLatestVersion(inValidCaseId));
        assertEquals("Hearing Case Id:'Case1111' not found", expectedException.getMessage());

    }

    @Test
    void shouldFailToValidateCaseWhenConnectivityIssue() {
        Exception exception = new RestClientException("connectivity issue");
        doThrow(exception).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
                                                       eq(HttpStatus.class));
        final ServiceException expectedException =
            assertThrows(ServiceException.class, () -> cftHearingService.getLatestVersion(validCaseId));
        assertEquals("The CFT service is currently down, please refresh "
                         +  "your browser or try again later", expectedException.getMessage());
    }
}
