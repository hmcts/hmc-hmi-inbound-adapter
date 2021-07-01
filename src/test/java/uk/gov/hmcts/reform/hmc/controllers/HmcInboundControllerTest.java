package uk.gov.hmcts.reform.hmc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.hmc.service.hmi.HearingManagementService;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HmcInboundController.class)
class HmcInboundControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private HearingManagementService hearingManagementService;

    private String url = "/listings/CASE111111";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
     void shouldReturn202_whenRequest_has_only_HearingDetails() throws Exception {
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getHearingRequest())))
               .andExpect(status().is(202))
               .andReturn();

    }

    @Test
    void shouldReturn202_whenRequest_has_only_ErrorDetails() throws Exception {
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getErrorRequest(2000))))
               .andExpect(status().is(202))
               .andReturn();

    }

    @Test
    void shouldReturn404_when_CaseListingId_isNotPresent() throws Exception {
        mockMvc.perform(put("/listings")
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getErrorRequest(2000))))
               .andExpect(status().is(404))
               .andReturn();

    }

    @Test
    void shouldReturn400_when_HearingResponseMandatoryFields_NotPresent1() throws Exception {
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getHearingRequestMandatoryFieldMissing())))
               .andExpect(status().is(400))
               .andReturn();
    }

    @Test
    void shouldReturn400_when_MetaResponseMandatoryFields_NotPresent() throws Exception {
        mockMvc.perform(put(url)
               .contentType(MediaType.APPLICATION_JSON_VALUE)
               .content(objectMapper.writeValueAsString(TestingUtil.getMetaRequestMandatoryFieldMissing())))
               .andExpect(status().is(400))
               .andReturn();
    }

}
