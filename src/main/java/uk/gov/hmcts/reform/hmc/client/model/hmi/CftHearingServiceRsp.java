package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CftHearingServiceRsp {

    @JsonProperty("status code")
    private Integer statusCode;

    private String description;

}

