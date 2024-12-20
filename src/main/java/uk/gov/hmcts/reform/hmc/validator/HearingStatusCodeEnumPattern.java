package uk.gov.hmcts.reform.hmc.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingStatusCode;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = HearingStatusCodeEnumPatternValidator.class)
public @interface HearingStatusCodeEnumPattern {
    Class<? extends Enum<HearingStatusCode>> enumClass();
    String fieldName();
    String message() default "Unsupported type or value for {fieldName}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

