package uk.gov.hmcts.reform.hmc.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExceptionResponse {

    private String errorMessage;

    public ExceptionResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
