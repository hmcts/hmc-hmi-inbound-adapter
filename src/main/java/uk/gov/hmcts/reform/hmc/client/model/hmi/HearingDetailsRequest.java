package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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
