package uk.gov.hmcts.reform.hmc.config;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.BaseTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageSenderConfigurationIT extends BaseTest {

    @Value("${hmac.secrets.hmi-to-hmc}")
    private String hmiToHmcSigningSecret;

    @Test
    void shouldSuccessfullyProcessRequestWithAppProperties() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        when(applicationParams.getQueueName()).thenReturn("test-queue");
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );

        configuration.sendMessage("{\"message\":\"ok\"}", MessageType.HEARING_RESPONSE, "123456");

        ArgumentCaptor<ServiceBusMessage> messageCaptor = ArgumentCaptor.forClass(ServiceBusMessage.class);
        verify(senderClient, times(1)).sendMessage(messageCaptor.capture());

        ServiceBusMessage sent = messageCaptor.getValue();
        assertEquals(MessageType.HEARING_RESPONSE.name(), sent.getApplicationProperties().get("message_type"));
        assertEquals("123456", sent.getApplicationProperties().get("hearing_id"));
        assertEquals("HMI-Inbound-Adapter", sent.getApplicationProperties().get("X-Sender-Service"));
        assertNotNull(sent.getApplicationProperties().get("X-Timestamp"));
        assertNotNull(sent.getApplicationProperties().get("X-Message-Signature"));
    }

    @Test
    void shouldNotThrowWhenSendFails() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        when(applicationParams.getQueueName()).thenReturn("test-queue");
        doThrow(new RuntimeException("service bus down")).when(senderClient).sendMessage(any(ServiceBusMessage.class));
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );

        assertDoesNotThrow(
            () -> configuration.sendMessage("{\"message\":\"ok\"}", MessageType.HEARING_RESPONSE, "123456")
        );
        verify(senderClient, times(1)).sendMessage(any(ServiceBusMessage.class));
    }

    @Test
    void shouldGenerateExpectedSignatureFromSentMessage() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        when(applicationParams.getQueueName()).thenReturn("test-queue");
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );
        String body = "{\"test\":\"name\"}";
        String hearingId = "12345";
        String messageType = MessageType.HEARING_RESPONSE.name();

        configuration.sendMessage(body, MessageType.HEARING_RESPONSE, hearingId);

        ArgumentCaptor<ServiceBusMessage> messageCaptor = ArgumentCaptor.forClass(ServiceBusMessage.class);
        verify(senderClient, times(1)).sendMessage(messageCaptor.capture());
        ServiceBusMessage sent = messageCaptor.getValue();

        String timestamp = sent.getApplicationProperties().get("X-Timestamp").toString();
        String actualSignature = sent.getApplicationProperties().get("X-Message-Signature").toString();
        String expectedPayload = String.join("|",
            "v1",
            "HMI-Inbound-Adapter",
            timestamp,
            messageType,
            hearingId,
            body
        );
        String expectedSignature = configuration.hmacSha256Base64(expectedPayload, hmiToHmcSigningSecret);

        assertEquals(expectedSignature, actualSignature);
    }

    @Test
    void shouldFailWhenRequiredDetailsAreMissing() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );

        assertThrows(NullPointerException.class, () -> configuration.sendMessage(null, null, "123456"));
        assertThrows(IllegalArgumentException.class, () -> configuration.sendMessage(null, MessageType.ERROR, null));
    }
}
