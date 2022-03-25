package uk.gov.hmcts.reform.hmc;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationParamsTest {

    private final ApplicationParams applicationParams = new ApplicationParams();
    private static final String VALUE = "test-value";
    private static final String CASE_ID = "Case1234";

    @Test
    void shouldGetCftHearingServiceHost() {
        ReflectionTestUtils.setField(applicationParams, "cftHearingServiceHost", VALUE);
        assertEquals(VALUE,
                     applicationParams.getCftHearingServiceHost());
    }

    @Test
    void shouldGetQueueName() {
        ReflectionTestUtils.setField(applicationParams, "queueName", VALUE);
        assertEquals(VALUE,
                     applicationParams.getQueueName());
    }

    @Test
    void shouldGetConnectionString() {
        ReflectionTestUtils.setField(applicationParams, "connectionString", VALUE);
        assertEquals(VALUE,
                     applicationParams.getConnectionString());
    }

    @Test
    void shouldTestGetCftHearingValidateCaseIdUrl() {
        ReflectionTestUtils.setField(applicationParams, "cftHearingServiceHost", VALUE);
        assertEquals(applicationParams.cftHearingValidateCaseIdUrl(CASE_ID),
                     applicationParams.getCftHearingServiceHost() + "/hearing/" + CASE_ID + "?isValid=true");
    }

    @Test
    void shouldTestEncode() {
        assertEquals(CASE_ID, ApplicationParams.encode(CASE_ID));
    }
}
