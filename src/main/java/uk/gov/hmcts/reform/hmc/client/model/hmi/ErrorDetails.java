package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {

    @JsonProperty("errCode")
    private Integer errorCode;

    @JsonProperty("errDesc")
    private String errorDescription;

    @JsonProperty("errLinkId")
    private String errorLinkId;

}
