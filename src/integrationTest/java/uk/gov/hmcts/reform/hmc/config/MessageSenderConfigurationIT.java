package uk.gov.hmcts.reform.hmc.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.hmc.BaseTest;

import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubSuccessfullyGetResponseFromHmi;

class MessageSenderConfigurationIT extends BaseTest {

    @MockBean
    private MessageSenderConfiguration messageSenderConfiguration;

    @Test
    void shouldSuccessfullyProcessRequest() {
        stubSuccessfullyGetResponseFromHmi();
        messageSenderConfiguration.sendMessage("Test");
    }
}
