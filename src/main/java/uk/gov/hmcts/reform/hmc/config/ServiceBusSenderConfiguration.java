package uk.gov.hmcts.reform.hmc.config;

import com.azure.core.util.ConfigurationBuilder;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.hmc.ApplicationParams;

import static uk.gov.hmcts.reform.hmc.constants.Constants.AMQP_CACHE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.AMQP_CACHE_VALUE;

@Configuration(proxyBeanMethods = false)
class ServiceBusSenderConfiguration {

    @Bean(destroyMethod = "close")
    ServiceBusSenderClient queueSenderClient(ApplicationParams params) {
        return new ServiceBusClientBuilder()
            .connectionString(params.getConnectionString())
            .configuration(new ConfigurationBuilder()
                .putProperty(AMQP_CACHE, AMQP_CACHE_VALUE)
                .build())
            .sender()
            .queueName(params.getQueueName())
            .buildClient();
    }
}
