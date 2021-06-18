package uk.gov.hmcts.reform.hmc.client.hmi.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HearingManagementInterfaceRsp {

    private HearingResponse hearingResponse;

    public HearingManagementInterfaceRsp(HearingResponse hearingResponse) {
        this.hearingResponse = hearingResponse;
    }

    public HearingResponse getHearingResponse() {
        return hearingResponse;
    }

    public void setHearingResponse(HearingResponse hearingResponse) {
        this.hearingResponse = hearingResponse;
    }
}
