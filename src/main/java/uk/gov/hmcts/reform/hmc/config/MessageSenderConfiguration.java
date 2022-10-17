package uk.gov.hmcts.reform.hmc.config;


import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.hmc.ApplicationParams;

import static uk.gov.hmcts.reform.hmc.constants.Constants.HMC_HMI_INBOUND_ADAPTER;
import static uk.gov.hmcts.reform.hmc.constants.Constants.TYPE_INBOUND;
import static uk.gov.hmcts.reform.hmc.constants.Constants.WRITE;

@Slf4j
@Component
public class MessageSenderConfiguration {

    private final ApplicationParams applicationParams;

    private static final String MESSAGE_TYPE = "message_type";
    private static final String HEARING_ID = "hearing_id";

    public MessageSenderConfiguration(ApplicationParams applicationParams) {
        this.applicationParams = applicationParams;
    }

    public void sendMessage(String message, MessageType messageType, String hearingId) {
        try {
            ServiceBusMessage serviceBusMessage = new ServiceBusMessage(message);
            serviceBusMessage.getApplicationProperties().put(MESSAGE_TYPE, messageType.name());
            serviceBusMessage.getApplicationProperties().put(HEARING_ID, hearingId);
            ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(applicationParams.getConnectionString())
                .sender()
                .queueName(applicationParams.getQueueName())
                .buildClient();
            log.debug("Connected to Queue {}", applicationParams.getQueueName());
            senderClient.sendMessage(serviceBusMessage);
            log.debug("Message has been sent to the Queue {}", applicationParams.getQueueName());
        } catch (Exception e) {
            log.error("Error while sending the message to queue:{}", e.getMessage());
            log.error(
                "Error occurred during service bus processing. Service:{} . Type: {}. Method: {}. Hearing ID: {}.",
                HMC_HMI_INBOUND_ADAPTER,
                TYPE_INBOUND,
                WRITE,
                hearingId
            );
        }
    }
}
