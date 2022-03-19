package uk.gov.hmcts.reform.hmc.config;


import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.hmc.ApplicationParams;

@Slf4j
@Component
public class MessageSenderConfiguration {

    private final ApplicationParams applicationParams;

    private static final String MESSAGE_TYPE = "message_type";
    private static final String HEARING_ID = "hearing_id";

    public MessageSenderConfiguration(ApplicationParams applicationParams) {
        this.applicationParams = applicationParams;
    }

    public void sendMessage(String message, MessageType messageType, String caseId) {
        try {
            ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(applicationParams.getConnectionString())
                .sender()
                .queueName(applicationParams.getQueueName())
                .buildClient();
            log.debug("Connected to Queue {}", applicationParams.getQueueName());
            senderClient.sendMessage(new ServiceBusMessage(message)
                                         .addContext(MESSAGE_TYPE, messageType)
                                         .addContext(HEARING_ID, caseId));
            log.debug("Message has been sent to the Queue {}", applicationParams.getQueueName());
        } catch (Exception e) {
            log.error("Error while sending the message to queue:{}", e.getMessage());
        }
    }
}
