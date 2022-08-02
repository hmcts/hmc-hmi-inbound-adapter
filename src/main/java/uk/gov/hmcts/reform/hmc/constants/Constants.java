package uk.gov.hmcts.reform.hmc.constants;

public final class Constants {

    private Constants() {
    }

    public static final String RESOURCE_NOT_FOUND_MSG = "Hearing Case Id:'%s' not found";
    public static final String CFT_SERVICE_DOWN_ERR_MESSAGE = "The CFT service is currently down, please refresh "
        + "your browser or try again later";
    public static final String INVALID_ERROR_CODE_ERR_MESSAGE = "Error code is invalid";
    public static final String INVALID_HEARING_PAYLOAD = "Invalid json request";
    public static final String INVALID_VERSION = "Invalid version";
    public static final String VERSION_NOT_SUPPLIED = "Version not supplied for hearing case id: '%s'";
    public static final String INVALID_LOCATION_REFERENCES = "Only one EPIMS location reference must be supplied";
}
