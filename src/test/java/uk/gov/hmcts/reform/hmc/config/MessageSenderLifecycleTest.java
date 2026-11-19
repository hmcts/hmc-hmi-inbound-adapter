package uk.gov.hmcts.reform.hmc.config;

import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.hmc.ApplicationParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageSenderLifecycleTest {

    private static final String CONNECTION_STRING =
        "Endpoint=sb://localhost;SharedAccessKeyName=test;SharedAccessKey=test";

    @Test
    @Timeout(10)
    void createsAndClosesTheConfiguredQueueSender() {
        var params = new ApplicationParams();
        ReflectionTestUtils.setField(params, "connectionString", CONNECTION_STRING);
        ReflectionTestUtils.setField(params, "queueName", "test-queue");

        ServiceBusSenderClient queue;
        try (var application = new AnnotationConfigApplicationContext()) {
            application.getBeanFactory().registerSingleton("applicationParams", params);
            application.register(ServiceBusSenderConfiguration.class, MessageSenderConfiguration.class);
            application.refresh();

            queue = application.getBean("queueSenderClient", ServiceBusSenderClient.class);
            assertThat(queue.getEntityPath()).isEqualTo("test-queue");
        }

        assertThatThrownBy(queue::createMessageBatch).isInstanceOf(IllegalStateException.class);
    }
}
