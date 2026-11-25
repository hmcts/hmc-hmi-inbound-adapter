package uk.gov.hmcts.reform.hmc.config;

import com.azure.messaging.servicebus.ServiceBusMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.hmc.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class MessageSenderConfigurationIT extends BaseTest {

    @Autowired
    private MessageSenderConfiguration messageSenderConfiguration;

    @Test
    void sendsTheHearingResponse() {
        messageSenderConfiguration.sendMessage("Test Message", MessageType.HEARING_RESPONSE, "123456");

        var message = ArgumentCaptor.forClass(ServiceBusMessage.class);
        verify(queueSenderClient).sendMessage(message.capture());
        assertThat(message.getValue().getBody().toString()).isEqualTo("Test Message");
        assertThat(message.getValue().getApplicationProperties())
            .containsEntry("message_type", "HEARING_RESPONSE")
            .containsEntry("hearing_id", "123456");
    }
}
