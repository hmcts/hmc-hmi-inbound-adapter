package uk.gov.hmcts.reform.hmc.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.hmc.BaseTest;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubSwagger200FailedToLoadRemoteConfig;

/**
 * Built-in feature which saves service's swagger specs in temporary directory.
 * Each travis run on master should automatically save and upload (if updated) documentation.
 */
@ContextConfiguration(classes = SwaggerConfiguration.class)
class SwaggerPublisherTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

    @DisplayName("Generate swagger documentation - Successful Test")
    @Test
    void generateDocsSuccess() throws Exception {
        MockHttpServletResponse response = mvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        String responseContent = new String(response.getContentAsByteArray());
        assertThat(responseContent).doesNotContain("Failed to load remote configuration");

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("/tmp/swagger-specs.json"))) {
            outputStream.write(response.getContentAsByteArray());
        }
    }

    @DisplayName("Generate swagger documentation - Failing Test")
    @Test
    void generateDocsFailure() throws Exception {
        stubSwagger200FailedToLoadRemoteConfig();
        MockHttpServletResponse response = mvc.perform(get("/v3/api-docs/failedToLoadRemoteConfiguration"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        String responseContent = new String(response.getContentAsByteArray());
        assertThat(responseContent).contains("Failed to load remote configuration");

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("/tmp/swagger-specs.json"))) {
            outputStream.write(response.getContentAsByteArray());
        }
    }
}
