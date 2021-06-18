package uk.gov.hmcts.reform.hmc.client.hmi.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class MetaResponse {

    @Size(max = 6, message = "api Version must be greater than 32 characters")
    private String apiVersion;

    @Size(max = 20, message = "api Version must be greater than 20 characters")
    private String transactionIdCaseHQ;

    public MetaResponse(String apiVersion, String transactionIdCaseHQ) {
        this.apiVersion = apiVersion;
        this.transactionIdCaseHQ = transactionIdCaseHQ;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getTransactionIdCaseHQ() {
        return transactionIdCaseHQ;
    }

    public void setTransactionIdCaseHQ(String transactionIdCaseHQ) {
        this.transactionIdCaseHQ = transactionIdCaseHQ;
    }
}
