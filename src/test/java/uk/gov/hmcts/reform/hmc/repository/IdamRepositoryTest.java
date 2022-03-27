package uk.gov.hmcts.reform.hmc.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.idam.client.IdamClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class IdamRepositoryTest {

    private static final String TEST_BEARER_TOKEN = "TestBearerToken";
    private static final String USER_ID = "232-SFWE-4543-CVDSF";

    @Mock
    private IdamClient idamClient;

    @Mock
    private ApplicationParams applicationParams;

    @InjectMocks
    private IdamRepository idamRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldGetHmcSystemUserAccessToken() {
        String userId = "User";
        String password = "Password";
        given(applicationParams.getHmcSystemUserId()).willReturn(userId);
        given(applicationParams.getHmcSystemUserPassword()).willReturn(password);
        given(idamClient.getAccessToken(userId, password)).willReturn(TEST_BEARER_TOKEN);

        String token = idamRepository.getHmcSystemUserAccessToken();

        assertThat(token).isEqualTo(TEST_BEARER_TOKEN);
    }
}
