package uk.gov.hmcts.reform.hmc.client.model.hmi;

import java.util.Arrays;

public enum HearingCode {
    LISTED(100, "LISTED"),
    PENDING_RELISTING(6, "PENDING_RELISTING"),
    CLOSED(8, "CLOSED"),
    EXCEPTION(0, "EXCEPTION"),
    ;

    private int number;
    private String label;

    HearingCode(int number, String label) {
        this.number = number;
        this.label = label;
    }

    public static boolean isValidNumber(int number) {
        return Arrays.stream(values()).anyMatch(eachType -> (eachType.number == number));
    }

    public static int getNumber(HearingCode code) {
        return code.number;
    }
}
