package uk.gov.hmcts.reform.hmc.config;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.BaseTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    @Test
    void shouldCloseInjectedSenderClientWhenPresent() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );

        configuration.close();

        verify(senderClient).close();
    }

    @Test
    void shouldNotThrowWhenCloseCalledWithoutInjectedSenderClient() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, null
        );

        assertDoesNotThrow(configuration::close);
    }

    @Test
    void shouldThrowWhenSigningSecretMissing() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(applicationParams, "", senderClient);

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> configuration.sendMessage("{}", MessageType.ERROR, "12345")
        );

        assertEquals("hmac.secrets.hmi-to-hmc must be configured", ex.getMessage());
    }

    @Test
    void shouldWrapHmacErrorsAsIllegalStateException() {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );

        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> configuration.hmacSha256Base64("payload", "%%%not-base64%%%")
        );

        assertEquals("Unable to calculate HMAC-SHA256", ex.getMessage());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    @Test
    void shouldBuildPayloadUsingEmptyStringsForNullValues() throws Exception {
        ApplicationParams applicationParams = mock(ApplicationParams.class);
        ServiceBusSenderClient senderClient = mock(ServiceBusSenderClient.class);
        MessageSenderConfiguration configuration = new MessageSenderConfiguration(
            applicationParams, hmiToHmcSigningSecret, senderClient
        );

        Method method = MessageSenderConfiguration.class.getDeclaredMethod(
            "buildPayloadToSign", String.class, String.class, String.class, String.class, String.class
        );
        method.setAccessible(true);

        String payload = (String) method.invoke(configuration, null, "2026-02-26T12:00:00Z",
                                                "HMI-Inbound-Adapter", null, null);

        assertEquals("v1|HMI-Inbound-Adapter|2026-02-26T12:00:00Z|||", payload);
    }
}
