package uk.gov.hmcts.reform.hmc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.idam.client.IdamClient;

@Component
public class IdamRepository {

    private final IdamClient idamClient;
    private final ApplicationParams appParams;

    @Autowired
    public IdamRepository(IdamClient idamClient, ApplicationParams applicationParams) {
        this.idamClient = idamClient;
        this.appParams = applicationParams;
    }

    @Cacheable("hmcAccessTokenCache")
    public String getHmcSystemUserAccessToken() {
        return idamClient.getAccessToken(appParams.getHmcSystemUserId(), appParams.getHmcSystemUserPassword());
    }
}

