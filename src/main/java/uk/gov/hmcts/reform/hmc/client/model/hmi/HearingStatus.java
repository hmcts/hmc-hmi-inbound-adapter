package uk.gov.hmcts.reform.hmc.client.model.hmi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;
import uk.gov.hmcts.reform.hmc.validator.HearingStatusCodeEnumPattern;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class HearingStatus {

    @NotEmpty(message = ValidationError.HEARING_CODE_NULL)
    @Size(max = 30, message = ValidationError.HEARING_CODE_LENGTH)
    @HearingStatusCodeEnumPattern(enumClass = HearingStatusCode.class, fieldName = "hearing status code")
    @Schema(allowableValues = "DRAFT, FIXED, PROV, CNCL")
    private String code;

    private String description;
}
