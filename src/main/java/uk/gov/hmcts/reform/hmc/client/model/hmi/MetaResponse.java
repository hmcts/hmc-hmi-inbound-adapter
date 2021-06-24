package uk.gov.hmcts.reform.hmc.client.model.hmi;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.validators.HearingPayloadConstraint;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class MetaResponse {

    @Size(max = 32, message = "api Version must not be greater than 32 characters")
    private String apiVersion;

    @HearingPayloadConstraint
    private String transactionIdCaseHQ;
}
