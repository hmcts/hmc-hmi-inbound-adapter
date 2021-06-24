package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@NoArgsConstructor
public class HearingDetailsRqst  {

    @Valid
    private HearingResponse hearingResponse;

    @JsonProperty("err")
    @Valid
    private ErrorDetails errorDetails;

}
