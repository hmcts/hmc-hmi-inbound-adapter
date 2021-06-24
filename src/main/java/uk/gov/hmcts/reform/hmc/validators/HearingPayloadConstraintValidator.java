package uk.gov.hmcts.reform.hmc.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HearingPayloadConstraintValidator implements
    ConstraintValidator<HearingPayloadConstraint, String> {

    @Override
    public boolean isValid(String field, ConstraintValidatorContext cxt) {
        if (field != null) {
            return !field.isEmpty() && field.trim().length() != 0;
        }
        return false;
    }
}
