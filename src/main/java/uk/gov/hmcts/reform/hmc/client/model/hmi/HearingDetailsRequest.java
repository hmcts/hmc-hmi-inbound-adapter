package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Data
@NoArgsConstructor
@Validated
public class HearingDetailsRequest {

    @Valid
    private HearingResponse hearingResponse;

    @JsonProperty("err")
    @Valid
    private ErrorDetails errorDetails;

}
