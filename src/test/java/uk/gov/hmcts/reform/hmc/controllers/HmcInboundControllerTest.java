package uk.gov.hmcts.reform.hmc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.hmc.service.HearingManagementService;
import uk.gov.hmcts.reform.hmc.utils.TestingUtil;

import static org.mockito.Mockito.doNothing;

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
     void shouldReturn202_whenRequest_has_only_HearingDetails()  {
        doNothing().when(hearingManagementService).processRequest(Mockito.anyString(),Mockito.any());
        HmcInboundController controller = new HmcInboundController(hearingManagementService);
        controller.getResponseFromHmi("123", TestingUtil.getHearingRequest());

    }

    @Test
    void shouldReturn202_whenRequest_has_only_ErrorDetails() {
        doNothing().when(hearingManagementService).processRequest(Mockito.anyString(),Mockito.any());
        HmcInboundController controller = new HmcInboundController(hearingManagementService);
        controller.getResponseFromHmi("123", TestingUtil.getErrorRequest(2000));
    }

    @Test
    void shouldReturn404_when_CaseListingId_isNotPresent() {
        doNothing().when(hearingManagementService).processRequest(Mockito.anyString(),Mockito.any());
        HmcInboundController controller = new HmcInboundController(hearingManagementService);
        controller.getResponseFromHmi(null, TestingUtil.getErrorRequest(2000));
    }

    @Test
    void shouldReturn400_when_HearingResponseMandatoryFields_NotPresent() {
        doNothing().when(hearingManagementService).processRequest(Mockito.anyString(),Mockito.any());
        HmcInboundController controller = new HmcInboundController(hearingManagementService);
        controller.getResponseFromHmi("123", TestingUtil.getHearingRequestMandatoryFieldMissing());
    }

    @Test
    void shouldReturn400_when_MetaResponseMandatoryFields_NotPresent() {
        doNothing().when(hearingManagementService).processRequest(Mockito.anyString(),Mockito.any());
        HmcInboundController controller = new HmcInboundController(hearingManagementService);
        controller.getResponseFromHmi("123", TestingUtil.getMetaRequestMandatoryFieldMissing());
    }

}
