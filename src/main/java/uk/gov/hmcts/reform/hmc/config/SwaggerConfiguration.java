package uk.gov.hmcts.reform.hmc.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.hmcts.reform.hmc.Application;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("hmc-hmi-inbound-adapter")
                .packagesToScan(Application.class.getPackage().getName() + ".controllers")
                .build();
    }

}
