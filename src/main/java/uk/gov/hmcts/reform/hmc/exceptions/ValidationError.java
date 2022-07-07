package uk.gov.hmcts.reform.hmc.exceptions;

public final class ValidationError {
    private static final String CHARACTERS_LONG = "characters long";
    public static final String VENUE_LOCATION_CODE_NULL = "Venue location code can not be null or empty";
    public static final String VENUE_LOCATION_CODE_LENGTH = "Venue location code must not be more than 30 "
        + CHARACTERS_LONG;
    public static final String TRANSACTION_ID_CASE_HQ_NULL = "Transaction Id Case Hq can not be null or empty";
    public static final String TRANSACTION_ID_CASE_HQ_LENGTH = "Transaction Id Case Hq must not be more than 60 "
        + CHARACTERS_LONG;
    public static final String TRANSACTION_ID_CASE_HQ_EMPTY = "Timestamp Id Case Hq can not be null or empty";

    public static final String HEARING_ROOM_NAME_LENGTH = "Hearing room name must not be more than 70 "
        + CHARACTERS_LONG;
    public static final String HEARING_JOH_CODE_LENGTH = "Hearing joh code must not be more than 30 "
        + CHARACTERS_LONG;
    public static final String HEARING_CODE_NULL = "Hearing code can not be null or empty";
    public static final String HEARING_CODE_LENGTH = "Hearing code must not be more than 30 " + CHARACTERS_LONG;
    public static final String HEARING_ATTENDEE_ENTITY_ID_LENGTH =
        "Hearing attendee entity id must not be more than 40 " + CHARACTERS_LONG;
    public static final String HEARING_CASE_VERSION_ID_NULL = "Hearing case version id can not be null or empty";
    public static final String HEARING_CASE_STATUS_NULL = "Hearing case status code not be null or empty";
    public static final String HEARING_CANCELLATION_REASON_LENGTH =
        "Hearing cancellation reason must not be more than 70 " + CHARACTERS_LONG;

    public static final String HEARING_STATUS_NULL = "Hearing status code not be null or empty";

    public static final String META_EMPTY = "Meta can not be null or empty";

    public static final String HEARING_EMPTY = "Hearing can not be null or empty";

    private ValidationError() {
    }
}
