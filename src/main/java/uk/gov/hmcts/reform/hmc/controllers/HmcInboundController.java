package uk.gov.hmcts.reform.hmc.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingDetailsRqst;
import uk.gov.hmcts.reform.hmc.service.hmi.HearingManagementService;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

@RestController
public class HmcInboundController {

    private HearingManagementService hearingManagementService;

    @Autowired
    public HmcInboundController(HearingManagementService hearingManagementService) {
        this.hearingManagementService = hearingManagementService;
    }

    @PutMapping(path = "/listings/{id}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Response is valid"),
        @ApiResponse(code = 202, message = "Response is valid"),
        @ApiResponse(code = 400, message = "Invalid case Id"),
        @ApiResponse(code = 404, message = "Case Id could not be found"),
        @ApiResponse(code = 500, message = "HMC service is down.")})
    public ResponseEntity<HttpStatus> getResponseFromHmi(@PathVariable("id") String id,
                                             @RequestBody @Valid HearingDetailsRqst hearingDetailsRqst) {
        return hearingManagementService.execute(id, hearingDetailsRqst);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleExtraFieldsException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Message","Invalid hearing payload");
        return errors;
    }
}
