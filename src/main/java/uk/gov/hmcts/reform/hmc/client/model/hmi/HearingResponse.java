package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@NoArgsConstructor
public class HearingResponse {

    @JsonProperty("meta")
    @Valid
    private MetaResponse metaResponse;

    @Valid
    private Hearing hearing;

}
