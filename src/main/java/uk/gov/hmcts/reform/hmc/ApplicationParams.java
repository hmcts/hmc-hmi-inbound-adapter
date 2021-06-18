package uk.gov.hmcts.reform.hmc;

import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class ApplicationParams {

    @Value("${hmc.cft-hearing-service.host}")
    private String cftHearingServiceHost;

    @Value("${hearing-management-interface.host}")
    private String hmiHearingHost;

    public String hmiHearingPutUrl(String caseId) {
        return hmiHearingHost + "/listings/" + caseId;
    }

    public String cftHearingValidatecaseIdUrl(String caseId) {
        return cftHearingServiceHost + "/hearing/" + caseId + "?isValid";
    }
}
