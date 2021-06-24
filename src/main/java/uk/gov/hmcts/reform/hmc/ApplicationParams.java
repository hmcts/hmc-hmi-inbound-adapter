package uk.gov.hmcts.reform.hmc;

import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class ApplicationParams {

    @Value("${hmc.cft-hearing-service.host}")
    private String cftHearingServiceHost;

    @Value("${spring.jms.servicebus.queue-name}")
    private String queueName;

    @Value("${spring.jms.servicebus.connection-string}")
    private String connectionString;


    public String cftHearingValidateCaseIdUrl(String caseId) {
        return cftHearingServiceHost + "/hearing/" + encode(caseId) + "?isValid";
    }

    public String getCftHearingServiceHost() {
        return cftHearingServiceHost;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public static String encode(final String stringToEncode) {
        return URLEncoder.encode(stringToEncode, StandardCharsets.UTF_8);
    }
}
