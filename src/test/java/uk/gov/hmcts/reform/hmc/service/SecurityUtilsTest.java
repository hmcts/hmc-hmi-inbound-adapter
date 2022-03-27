package uk.gov.hmcts.reform.hmc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.hmc.repository.IdamRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

class SecurityUtilsTest {

    private static final String SERVICE_JWT = "SERVICE_JWT";
    private static final String USER_JWT = "USER_JWT";

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private IdamRepository idamRepository;

    @InjectMocks
    private SecurityUtils securityUtils;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(authTokenGenerator.generate()).thenReturn(SERVICE_JWT);
        when(idamRepository.getHmcSystemUserAccessToken()).thenReturn(USER_JWT);
    }

    @Test
    void shouldGenerateAuthorizationHeaders() {
        HttpHeaders headers = securityUtils.authorizationHeaders();

        assertAll(
            () -> assertHeader(headers, "ServiceAuthorization", SERVICE_JWT),
            () -> assertHeader(headers, "Authorization", USER_JWT)
        );
    }

    private void assertHeader(HttpHeaders headers, String name, String value) {
        assertThat(headers.get(name), hasSize(1));
        assertThat(headers.get(name).get(0), equalTo(value));
    }
}
