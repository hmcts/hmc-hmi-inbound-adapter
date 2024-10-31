package uk.gov.hmcts.reform.hmc.client.model.hmi;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;
import uk.gov.hmcts.reform.hmc.validator.HearingStatusCodeEnumPattern;

@Data
@NoArgsConstructor
public class HearingStatus {

    @NotEmpty(message = ValidationError.HEARING_CODE_NULL)
    @Size(max = 30, message = ValidationError.HEARING_CODE_LENGTH)
    @HearingStatusCodeEnumPattern(enumClass = HearingStatusCode.class, fieldName = "hearing status code")
    @ApiModelProperty(allowableValues = "DRAFT, FIXED, PROV, CNCL")
    private String code;

    private String description;
}
