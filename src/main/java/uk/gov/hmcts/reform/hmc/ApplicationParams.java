package uk.gov.hmcts.reform.hmc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Getter
public class ApplicationParams {

    @Value("${hmc.cft-hearing-service.host}")
    private String cftHearingServiceHost;

    @Value("${jms.servicebus.queue-name}")
    private String queueName;

    @Value("${jms.servicebus.connection-string}")
    private String connectionString;

    @Value("${idam.system-user.id}")
    private String hmcSystemUserId;

    @Value("${idam.system-user.password}")
    private String hmcSystemUserPassword;

    @Value("${hmc.hearing.terminalStates}")
    private List<String> hmcHearingTerminalStates;

    public String cftHearingValidateCaseIdUrl(String caseId) {
        return cftHearingServiceHost + "/hearing/" + encode(caseId) + "?isValid=true";
    }

    public static String encode(final String stringToEncode) {
        return URLEncoder.encode(stringToEncode, StandardCharsets.UTF_8);
    }
}
