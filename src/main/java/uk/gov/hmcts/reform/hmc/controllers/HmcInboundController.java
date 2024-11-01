package uk.gov.hmcts.reform.hmc.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRequest;
import uk.gov.hmcts.reform.hmc.service.HearingManagementService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Validated
public class HmcInboundController {


    private final HearingManagementService hearingManagementService;

    public HmcInboundController(HearingManagementService hearingManagementService) {
        this.hearingManagementService = hearingManagementService;
    }

    @PutMapping(path = "/listings/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiResponse(responseCode = "202", description = "Response is valid")
    @ApiResponse(responseCode = "400", description = "Invalid case listing Id")
    @ApiResponse(responseCode = "404", description = "Case listing Id could not be found")
    @ApiResponse(responseCode = "500", description = "Error occurred on the server.")
    public void getResponseFromHmi(@PathVariable("id") String id,
                                   @Valid @RequestBody HearingDetailsRequest hearingDetailsRequest) {
        hearingManagementService.processRequest(id, hearingDetailsRequest);
    }

}
