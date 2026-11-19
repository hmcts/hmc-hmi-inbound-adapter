package uk.gov.hmcts.reform.hmc.config;


import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static uk.gov.hmcts.reform.hmc.constants.Constants.ERROR_PROCESSING_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.HMC_FROM_HMI;
import static uk.gov.hmcts.reform.hmc.constants.Constants.HMC_HMI_INBOUND_ADAPTER;
import static uk.gov.hmcts.reform.hmc.constants.Constants.WRITE;

@Slf4j
@Component
public class MessageSenderConfiguration {

    private final ServiceBusSenderClient senderClient;

    private static final String MESSAGE_TYPE = "message_type";
    private static final String HEARING_ID = "hearing_id";

    public MessageSenderConfiguration(ServiceBusSenderClient senderClient) {
        this.senderClient = senderClient;
    }

    public void sendMessage(String message, MessageType messageType, String hearingId) {
        try {
            ServiceBusMessage serviceBusMessage = new ServiceBusMessage(message);
            serviceBusMessage.getApplicationProperties().put(MESSAGE_TYPE, messageType.name());
            serviceBusMessage.getApplicationProperties().put(HEARING_ID, hearingId);
            log.debug("Sending message to Queue {}", message);
            senderClient.sendMessage(serviceBusMessage);
            log.debug("Message has been sent to the Queue {}", senderClient.getEntityPath());
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
}
