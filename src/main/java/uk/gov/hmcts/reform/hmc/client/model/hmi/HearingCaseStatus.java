package uk.gov.hmcts.reform.hmc.client.model.hmi;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;
import uk.gov.hmcts.reform.hmc.validator.HearingCodeEnumPattern;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class HearingCaseStatus {

    @NotNull(message = ValidationError.HEARING_STATUS_NULL)
    @HearingCodeEnumPattern(enumClass = HearingCode.class, fieldName = "hearing case status code")
    private String code;
    private String description;
}
