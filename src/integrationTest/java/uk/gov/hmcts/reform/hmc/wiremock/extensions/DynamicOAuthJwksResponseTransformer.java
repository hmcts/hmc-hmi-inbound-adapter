package uk.gov.hmcts.reform.hmc.wiremock.extensions;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.hmc.KeyGenerator;

@Slf4j
public class DynamicOAuthJwksResponseTransformer extends ResponseTransformer {

    private static final String RESPONSE_TRANSFORMER_NAME = "dynamic-oauth-jwks-response-transformer";

    private static final String JWKS_RESPONSE_JSON = """
        {
            "keys": [%s]
        }""";

    @Override
    public String getName() {
        return RESPONSE_TRANSFORMER_NAME;
    }

    @Override
    public Response transform(Request request, Response response, FileSource fileSource, Parameters parameters) {
        String dynamicJwksResponse = getDynamicJwksResponse();
        log.debug("Dynamic JWKS response: {}", dynamicJwksResponse);

        return Response.Builder.like(response).but().body(dynamicJwksResponse).build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    private String getDynamicJwksResponse() {
        try {
            return JWKS_RESPONSE_JSON.formatted(KeyGenerator.getRsaKey().toPublicJWK().toJSONString());
        } catch (JOSEException e) {
            log.error("KeyGenerator unable to generate RSA Key", e);
        }

        return null;
    }
}
