package uk.gov.hmcts.reform.hmc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

public class KeyGenerator {

    private static final int KEY_SIZE = 2048;
    private static final String KEY_ID = "23456789";

    private static RSAKey rsaKey;

    private KeyGenerator() {
    }

    public static RSAKey getRsaKey() throws JOSEException {
        if (rsaKey == null) {
            rsaKey = new RSAKeyGenerator(KEY_SIZE).keyID(KEY_ID).generate();
        }

        return rsaKey;
    }
}
