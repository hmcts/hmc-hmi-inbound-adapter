package uk.gov.hmcts.reform.hmc.client.hmi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HearingResponse {

    @JsonProperty("meta")
    private MetaResponse metaResponse;

    private Hearing hearing;

    public HearingResponse(MetaResponse metaResponse, Hearing hearing) {
        this.metaResponse = metaResponse;
        this.hearing = hearing;
    }

    public MetaResponse getMetaResponse() {
        return metaResponse;
    }

    public void setMetaResponse(MetaResponse metaResponse) {
        this.metaResponse = metaResponse;
    }

    public Hearing getHearing() {
        return hearing;
    }

    public void setHearing(Hearing hearing) {
        this.hearing = hearing;
    }
}
