package uk.gov.hmcts.reform.hmc.config;


import com.azure.core.util.ConfigurationBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.hmc.ApplicationParams;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static uk.gov.hmcts.reform.hmc.constants.Constants.AMQP_CACHE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.AMQP_CACHE_VALUE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.ERROR_PROCESSING_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.HMC_FROM_HMI;
import static uk.gov.hmcts.reform.hmc.constants.Constants.HMC_HMI_INBOUND_ADAPTER;
import static uk.gov.hmcts.reform.hmc.constants.Constants.WRITE;

@Slf4j
@Component
public class MessageSenderConfiguration {

    private final ApplicationParams applicationParams;
    private final ServiceBusSenderClient senderClient;
    private final String hmiToHmcSigningSecret;

    private static final String MESSAGE_TYPE = "message_type";
    private static final String HEARING_ID = "hearing_id";
    private static final String HEADER_SIGNATURE = "X-Message-Signature";
    private static final String HEADER_SENDER = "X-Sender-Service";
    private static final String HEADER_TIMESTAMP = "X-Timestamp";
    private static final String SENDER_SERVICE = "HMI-Inbound-Adapter";

    @Autowired
    public MessageSenderConfiguration(ApplicationParams applicationParams,
                                      @org.springframework.beans.factory.annotation.Value(
                                          "${hmac.secrets.hmi-to-hmc}") String hmiToHmcSigningSecret) {
        this(applicationParams, hmiToHmcSigningSecret, null);
    }

    MessageSenderConfiguration(ApplicationParams applicationParams,
                               String hmiToHmcSigningSecret,
                               ServiceBusSenderClient senderClient) {
        this.applicationParams = applicationParams;
        this.hmiToHmcSigningSecret = hmiToHmcSigningSecret;
        this.senderClient = senderClient;
    }

    public void sendMessage(String message, MessageType messageType, String hearingId) {
        validateInput(messageType, hearingId);
        try {
            log.debug("setting up the connection details for hearingId {}", hearingId);

            String timestamp = Instant.now().toString();
            String messageTypeName = messageType.name();

            ServiceBusMessage serviceBusMessage = new ServiceBusMessage(message);
            serviceBusMessage.getApplicationProperties().put(MESSAGE_TYPE, messageTypeName);
            serviceBusMessage.getApplicationProperties().put(HEARING_ID, hearingId);
            serviceBusMessage.getApplicationProperties().put(HEADER_SENDER, SENDER_SERVICE);
            serviceBusMessage.getApplicationProperties().put(HEADER_TIMESTAMP, timestamp);

            String payloadToSign = buildPayloadToSign(message, timestamp, SENDER_SERVICE, hearingId, messageTypeName);
            String signature = hmacSha256Base64(payloadToSign, hmiToHmcSigningSecret);
            serviceBusMessage.getApplicationProperties().put(HEADER_SIGNATURE, signature);

            ServiceBusSenderClient sender = senderClient;
            if (sender == null) {
                sender = new ServiceBusClientBuilder()
                    .connectionString(applicationParams.getConnectionString())
                    .configuration(new ConfigurationBuilder()
                        .putProperty(AMQP_CACHE, AMQP_CACHE_VALUE)
                        .build())
                    .sender()
                    .queueName(applicationParams.getQueueName())
                    .buildClient();
            }

            log.debug("Connected to Queue {}", applicationParams.getQueueName());
            log.debug("Sending message {} for hearingId {} with messageType {} to queue {}",
                message, hearingId, messageTypeName, applicationParams.getQueueName());
            sender.sendMessage(serviceBusMessage);
            log.debug("Message has been sent to the Queue {}", applicationParams.getQueueName());
        } catch (Exception e) {
            log.error("Error while sending the message to queue:{}", e.getMessage());
            log.error(
                ERROR_PROCESSING_MESSAGE,
                HMC_HMI_INBOUND_ADAPTER,
                HMC_FROM_HMI,
                WRITE,
                hearingId
            );
        }
    }

    @PreDestroy
    public void close() {
        if (senderClient != null) {
            senderClient.close();
        }
    }

    private void validateInput(MessageType messageType, String hearingId) {
        Objects.requireNonNull(messageType, "messageType must not be null");
        if (!StringUtils.hasText(hearingId)) {
            throw new IllegalArgumentException("hearingId must not be blank");
        }
        if (!StringUtils.hasText(hmiToHmcSigningSecret)) {
            throw new IllegalStateException("hmac.secrets.hmi-to-hmc must be configured");
        }
    }

    private String buildPayloadToSign(String body,
                                      String timestamp,
                                      String sender,
                                      String hearingId,
                                      String messageType) {
        return String.join("|",
            "v1",
            sender,
            timestamp,
            messageType == null ? "" : messageType,
            hearingId == null ? "" : hearingId,
            body == null ? "" : body
        );
    }

    String hmacSha256Base64(String payload, String base64Secret) {
        try {
            byte[] secretBytes = Base64.getDecoder().decode(base64Secret);
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to calculate HMAC-SHA256", e);
        }
    }
}
