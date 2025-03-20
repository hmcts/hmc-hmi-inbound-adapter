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

/**
 * Built-in feature which saves service's swagger specs in temporary directory.
 * Each travis run on master should automatically save and upload (if updated) documentation.
 */
@ContextConfiguration(classes = SwaggerConfiguration.class)
class SwaggerPublisherTest extends BaseTest {

    @Autowired
    private MockMvc mvc;

    private static final String FAILED_TO_LOAD_REMOTE_CONFIG = "Failed to load remote configuration";
    private static final String UNCAUGHT_SYNTAX_ERROR = "Uncaught SyntaxError";
    private static final String UNCAUGHT_REFERENCE_ERROR = "Uncaught ReferenceError";
    private static final String UNCAUGHT_TYPE_ERROR = "Uncaught TypeError";

    @DisplayName("Generate swagger documentation - Successful Test")
    @Test
    void generateDocsSuccess() throws Exception {
        MockHttpServletResponse response = mvc.perform(get("/v3/api-docs/?test=ggg"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        String responseContent = new String(response.getContentAsByteArray());

        assertThat(responseContent).doesNotContain(FAILED_TO_LOAD_REMOTE_CONFIG)
            .doesNotContain(UNCAUGHT_SYNTAX_ERROR)
            .doesNotContain(UNCAUGHT_REFERENCE_ERROR)
            .doesNotContain(UNCAUGHT_TYPE_ERROR);

        try (OutputStream outputStream = Files.newOutputStream(Paths.get("/tmp/swagger-specs.json"))) {
            outputStream.write(response.getContentAsByteArray());
        }
    }

    @DisplayName("Generate swagger documentation - Failing Test")
    @Test
    void generateDocsFailure() throws Exception {
        MockHttpServletResponse response = mvc.perform(get("/v3/api-docs/?test=ggg"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        String responseContent = new String(response.getContentAsByteArray())
            + FAILED_TO_LOAD_REMOTE_CONFIG + UNCAUGHT_REFERENCE_ERROR + UNCAUGHT_TYPE_ERROR + UNCAUGHT_SYNTAX_ERROR;

        assertThat(responseContent).contains("Failed to load remote configuration")
            .contains(UNCAUGHT_SYNTAX_ERROR)
            .contains(UNCAUGHT_REFERENCE_ERROR)
            .contains(UNCAUGHT_TYPE_ERROR);
    }
}
