package uk.gov.hmcts.reform.hmc.validator;

import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
