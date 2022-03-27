package uk.gov.hmcts.reform.hmc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.hmc.repository.IdamRepository;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class SecurityUtils {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    private final AuthTokenGenerator authTokenGenerator;
    private final IdamRepository idamRepository;

    @Autowired
    public SecurityUtils(AuthTokenGenerator authTokenGenerator, IdamRepository idamRepository) {
        this.authTokenGenerator = authTokenGenerator;
        this.idamRepository = idamRepository;
    }

    public HttpHeaders authorizationHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTHORIZATION, getServiceAuthorization());
        headers.add(AUTHORIZATION, getSystemUserAuthorization());
        return headers;
    }

    private String getServiceAuthorization() {
        return authTokenGenerator.generate();
    }

    private String getSystemUserAuthorization() {
        return idamRepository.getHmcSystemUserAccessToken();
    }
}
