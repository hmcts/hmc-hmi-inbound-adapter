package uk.gov.hmcts.reform.hmc.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import uk.gov.hmcts.reform.hmc.BaseTestSecurity;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.hmc.KeyGenerator.getRsaKey;
import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubReturn404FromCft;

class JwtDecoderIT extends BaseTestSecurity {

    private static final String HEARING_ID = "2000000000";
    private static final String HMI_HEARING_RESPONSE = """
        {
            "hearingResponse": {
                "meta": {
                    "timestamp": "2026-04-09T12:00:00Z",
                    "transactionIdCaseHQ": "123"
                },
                "hearing": {
                    "hearingCaseVersionId": 1,
                    "hearingCaseStatus": {
                        "code": "100"
                    }
                }
            }
        }""";

    private static final String VALID_ISSUER_FORGEROCK = "http://fr-am:8080/openam/oauth2/hmcts";
    private static final String INVALID_ISSUER = "http://invalidIssuer";

    private static final String REQUEST_HEADER_SERVICE_AUTHORISATION = "ServiceAuthorization";
    private static final String REQUEST_HEADER_AUTHORISATION = "Authorization";
    private static final String RESPONSE_HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

    private final MockMvc mockMvc;

    @Autowired
    public JwtDecoderIT(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void jwtTimestampValidator_shouldFailWhenExpiresAtInPast() throws Exception {
        Instant currentInstant = Instant.now();
        Instant issuedAt = currentInstant.minusSeconds(3600);
        Instant expiresAt = currentInstant.minusSeconds(1800);
        Instant notBefore = currentInstant.minusSeconds(3600);

        String userToken = createUserToken(issuedAt, expiresAt, notBefore);

        mockMvc.perform(putRequest(userToken))
            .andExpect(status().isUnauthorized())
            .andExpect(header().exists(RESPONSE_HEADER_WWW_AUTHENTICATE))
            .andExpect(header().string(RESPONSE_HEADER_WWW_AUTHENTICATE, containsString("Jwt expired at")));
    }

    @Test
    void jwtTimestampValidator_shouldFailWhenNotBeforeInFuture() throws Exception {
        Instant currentInstant = Instant.now();
        Instant issuedAt = currentInstant.minusSeconds(3600);
        Instant expiresAt = currentInstant.plusSeconds(3600);
        Instant notBefore = currentInstant.plusSeconds(1800);

        String userToken = createUserToken(issuedAt, expiresAt, notBefore);

        mockMvc.perform(putRequest(userToken))
            .andExpect(status().isUnauthorized())
            .andExpect(header().exists(RESPONSE_HEADER_WWW_AUTHENTICATE))
            .andExpect(header().string(RESPONSE_HEADER_WWW_AUTHENTICATE, containsString("Jwt used before")));
    }

    @Test
    void multiIssuerValidator_shouldFailWithInvalidIssuer() throws Exception {
        String userToken = createUserToken(INVALID_ISSUER);

        mockMvc.perform(putRequest(userToken))
            .andExpect(status().isUnauthorized())
            .andExpect(header().exists(RESPONSE_HEADER_WWW_AUTHENTICATE))
            .andExpect(header().string(RESPONSE_HEADER_WWW_AUTHENTICATE,
                                       containsString("The issuer is missing or invalid")));
    }

    @Test
    void multiIssuerValidator_shouldSucceedWithValidIssuer() throws Exception {
        String userToken = createUserToken(VALID_ISSUER_FORGEROCK);

        stubReturn404FromCft(HEARING_ID);

        mockMvc.perform(putRequest(userToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value("NOT_FOUND"))
            .andExpect(jsonPath("$.errors").value("Hearing Case Id:'2000000000' not found"));
    }

    private RequestBuilder putRequest(String userToken) {
        return put("/listings/" + HEARING_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header(REQUEST_HEADER_SERVICE_AUTHORISATION, "1234")
            .header(REQUEST_HEADER_AUTHORISATION, userToken)
            .content(HMI_HEARING_RESPONSE);
    }

    private String createUserToken(Instant issuedAt, Instant expiresAt, Instant notBefore) throws JOSEException {
        return createUserToken(VALID_ISSUER_FORGEROCK, issuedAt, expiresAt, notBefore);
    }

    private String createUserToken(String issuer) throws JOSEException {
        Instant currentInstant = Instant.now();
        Instant issuedAt = currentInstant.minusSeconds(3600);
        Instant expiresAt = currentInstant.plusSeconds(3600);
        Instant notBefore = currentInstant.minusSeconds(3600);

        return createUserToken(issuer, issuedAt, expiresAt, notBefore);
    }

    private String createUserToken(String issuer, Instant issuedAt, Instant expiresAt, Instant notBefore)
        throws JOSEException {
        String userToken =
            JWT.create()
                .withHeader(Map.of("typ", "JWT",
                                   "alg", "RS256",
                                   "kid", "23456789"))
                .withSubject("hmi")
                .withIssuer(issuer)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withNotBefore(notBefore)
                .sign(Algorithm.RSA256(getRsaKey().toRSAPublicKey(), getRsaKey().toRSAPrivateKey()));

        return "Bearer " + userToken;
    }
}
