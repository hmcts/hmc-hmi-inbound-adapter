package uk.gov.hmcts.reform.hmc.client.model.hmi;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class MetaResponse {

    @Size(max = 32, message = ValidationError.API_VERSION_INVALID_LENGTH)
    private String apiVersion;

    @NotEmpty(message = ValidationError.TRANSACTION_ID_CASE_HQ_EMPTY)
    private String transactionIdCaseHQ;
}
