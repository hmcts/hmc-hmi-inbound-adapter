package uk.gov.hmcts.reform.hmc.validator;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingStatus;
import uk.gov.hmcts.reform.hmc.client.model.hmi.HearingStatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HearingStatusCodeEnumPatternValidatorTest {

    static Validator validator;

    private static final Logger logger = LoggerFactory.getLogger(
        HearingStatusCodeEnumPatternValidatorTest.class);

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenHearingStatusCodeIsInvalid() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode("CANCEL");
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        List<String> validationErrors = new ArrayList<>();
        violations.forEach(e -> validationErrors.add(e.getMessage()));
        assertEquals("Unsupported type or value for hearing status code", validationErrors.get(0));
    }

    @Test
    void whenHearingStatusCodeIsNull() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode(null);
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        List<String> validationErrors = new ArrayList<>();
        violations.forEach(e -> validationErrors.add(e.getMessage()));
        assertTrue(validationErrors.contains("Hearing code can not be null or empty"));
        assertTrue(validationErrors.contains("Unsupported type or value for hearing status code"));
    }

    @Test
    void whenHearingStatusCodeMaxLength() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode("a".repeat(31));
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        List<String> validationErrors = new ArrayList<>();
        violations.forEach(e -> validationErrors.add(e.getMessage()));
        assertTrue(validationErrors.contains("Hearing code must not be more than 30 characters long"));
        assertTrue(validationErrors.contains("Unsupported type or value for hearing status code"));
    }

    @Test
    void whenValidHearingStatusCodeString_Draft() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode(HearingStatusCode.DRAFT.label);
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidHearingStatusCodeString_Cncl() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode(HearingStatusCode.CNCL.label);
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"DRAFT", "FIXED", "PROV", "CNCL"})
    void test_not_null(String code) {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode(code);
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidHearingCodeWithDescription() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode(HearingStatusCode.FIXED.label);
        hearingStatus.setDescription("fixed hearing");
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidHearingCode_Cncl_CaseSensitive() {
        HearingStatus hearingStatus = new HearingStatus();
        hearingStatus.setCode("cncl");
        Set<ConstraintViolation<HearingStatus>> violations = validator.validate(hearingStatus);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        List<String> validationErrors = new ArrayList<>();
        violations.forEach(e -> validationErrors.add(e.getMessage()));
        assertEquals("Unsupported type or value for hearing status code", validationErrors.get(0));
    }
}
