package uk.gov.hmcts.reform.hmc.exceptions;

public final class ValidationError {

    public static final String TRANSACTION_ID_CASE_HQ_EMPTY = "Transaction Id case HQ can not be empty";
    public static final String HEARING_ID_CASE_HQ_EMPTY = "Hearing Id case HQ can not be empty";
    public static final String HEARING_TYPE_EMPTY = "Hearing type can not be empty";
    public static final String HEARING_STARTING_TIME_EMPTY = "Hearing start time can not be empty";
    public static final String HEARING_END_TIME_EMPTY = "Hearing end time can not be empty";
    public static final String HEARING_CASE_ID_HMCTS_EMPTY = "Hearing case id Hmcts can not be empty";
    public static final String HEARING_TRANSLATOR_REQUIRED_EMPTY = "Hearing translator can not be empty";
    public static final String HEARING_TRANSLATOR_LANGUAGE_EMPTY = "Hearing translator language can not be empty ";
    public static final String HEARING_CREATED_DATE_EMPTY = "Hearing created date can not be empty";
    public static final String HEARING_CREATED_BY_EMPTY = "Hearing created by can not be empty";
    public static final String HEARING_VENUE_ID_EMPTY = "Hearing venue id can not be empty";
    public static final String HEARING_JUDGE_ID_EMPTY = "Hearing judge id can not be empty";
    public static final String HEARING_CASE_ID_HMCTS_INVALID_LENGTH =
        "Hearing case id must not be more than 30 characters long";
    public static final String API_VERSION_INVALID_LENGTH = "API Version must not be more than 32 characters long";

    private ValidationError() {
    }
}
