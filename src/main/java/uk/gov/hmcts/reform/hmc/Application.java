package uk.gov.hmcts.reform.hmc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.idam.client.IdamApi;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableFeignClients(basePackageClasses = { IdamApi.class, ServiceAuthorisationApi.class })
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, it is not a utility class
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
