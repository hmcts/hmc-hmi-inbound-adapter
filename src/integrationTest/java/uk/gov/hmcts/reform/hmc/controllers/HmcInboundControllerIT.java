package uk.gov.hmcts.reform.hmc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.hmc.BaseTest;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubReturn404FromCft;
import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubSuccessfullyGetResponseFromCft;
import static uk.gov.hmcts.reform.hmc.WiremockFixtures.stubSuccessfullyGetResponseFromHmi;

class HmcInboundControllerIT extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private static final String listingId = "test1";

    private String url = "/listings/" + listingId;

    @Test
    void shouldReturn202_WhenHearingResponseIsValid() {
        stubSuccessfullyGetResponseFromHmi(listingId);
    }

    @Test
    void shouldReturn400_when_HearingResponseMandatoryFields_NotPresent() throws Exception {
        stubSuccessfullyGetResponseFromCft(listingId);
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getHearingRequestMandatoryFieldMissing())))
            .andExpect(status().is(400))
            .andReturn();
    }

    @Test
    void shouldReturn202_whenRequest_has_only_ErrorDetails() throws Exception {
        stubSuccessfullyGetResponseFromCft(listingId);
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getErrorRequest(2000))))
               .andExpect(status().is(202))
               .andReturn();
    }

    @Test
    void shouldReturn400_whenRequest_has_only_InvalidErrorCode() throws Exception {
        stubSuccessfullyGetResponseFromCft(listingId);
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .accept(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(TestingUtil.getErrorRequest(null))))
               .andExpect(status().is(400))
               .andReturn();
    }

    @Test
    void shouldReturn400_when_MetaResponseMandatoryFields_NotPresent() throws Exception {
        stubSuccessfullyGetResponseFromCft(listingId);
        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(TestingUtil.getMetaRequestMandatoryFieldMissing())))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void shouldReturn400_when_HearingVenueLocationReferencesKeyEqualsEpims_NotPresent() throws Exception {
        stubSuccessfullyGetResponseFromCft(listingId);
        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                        TestingUtil.getHearingVenueLocationReferencesKeyDoesNotEqualsEpims())))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    void shouldReturn404_when_CaseListingId_NotFound() throws Exception {
        stubReturn404FromCft(listingId);
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getHearingWithCodesRequest())))
               .andExpect(status().is(404))
               .andReturn();

    }
}
