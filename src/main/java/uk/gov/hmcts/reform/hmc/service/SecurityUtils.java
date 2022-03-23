package uk.gov.hmcts.reform.hmc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

@Service
public class SecurityUtils {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public SecurityUtils(final AuthTokenGenerator authTokenGenerator) {
        this.authTokenGenerator = authTokenGenerator;
    }

    public HttpHeaders serviceAuthorizationHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTHORIZATION, getServiceAuthorization());
        return headers;
    }

    private String getServiceAuthorization() {
        return authTokenGenerator.generate();
    }
}
