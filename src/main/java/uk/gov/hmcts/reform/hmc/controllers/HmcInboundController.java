package uk.gov.hmcts.reform.hmc.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.hmc.client.hmi.model.response.HearingManagementInterfaceRsp;
import uk.gov.hmcts.reform.hmc.service.HearingManagementInterfaceService;

@RestController
public class HmcInboundController {

    private HearingManagementInterfaceService hearingManagementService;

    @Autowired
    public HmcInboundController(HearingManagementInterfaceService hearingManagementService) {
        this.hearingManagementService = hearingManagementService;
    }

    @PutMapping(path = "/listings/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Response is valid"),
        @ApiResponse(code = 202, message = "Response is valid"),
        @ApiResponse(code = 400, message = "Invalid hearing Id"),
        @ApiResponse(code = 404, message = "Case id could not be found"),
        @ApiResponse(code = 500, message = "HMC service is down.")})
    public HearingManagementInterfaceRsp getResponseFromHmi(@PathVariable("id") String id) {
        return hearingManagementService.execute(id);

    }
}
