package uk.gov.hmcts.reform.hmc.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRqst;
import uk.gov.hmcts.reform.hmc.service.hmi.HearingManagementService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class HmcInboundController {

    private HearingManagementService hearingManagementService;

    public HmcInboundController(HearingManagementService hearingManagementService) {
        this.hearingManagementService = hearingManagementService;
    }

    @PutMapping(path = "/listings/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiResponses(value = {
        @ApiResponse(code = 202, message = "Response is valid"),
        @ApiResponse(code = 400, message = "Invalid case Id"),
        @ApiResponse(code = 404, message = "Case Id could not be found"),
        @ApiResponse(code = 500, message = "Error occurred on the server.")})
    public void getResponseFromHmi(@PathVariable("id") String id,
                                             @RequestBody @Valid HearingDetailsRqst hearingDetailsRqst) {
        hearingManagementService.processRequest(id, hearingDetailsRqst);
    }

}
