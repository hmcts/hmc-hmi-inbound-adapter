package uk.gov.hmcts.reform.hmc.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;

public class HearingCodeEnumPatternValidator implements ConstraintValidator<HearingCodeEnumPattern, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Integer number = null;
        try {
            number = Integer.valueOf(value);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return HearingCode.isValidNumber(number);
    }

}
