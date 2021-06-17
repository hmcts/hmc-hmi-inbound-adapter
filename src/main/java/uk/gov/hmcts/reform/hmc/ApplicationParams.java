package uk.gov.hmcts.reform.hmc;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class ApplicationParams {

    private String cftHearingServiceHost = "http://localhost:4459";
    private String hmiHearingHost = "http://localhost:8080";

    public String hmiHearingPutUrl() {
        return hmiHearingHost + "listings/{id}";
    }

    public String cftHearingValidateHearingIdUrl(Long hearingId) {
        return cftHearingServiceHost + "/hearing/" + hearingId + "?isValid";
    }
}
