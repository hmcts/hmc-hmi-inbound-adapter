package uk.gov.hmcts.reform.hmc.client.model.hmi;

public enum HearingStatusCode {

    DRAFT("DRAFT"),
    FIXED("FIXED"),
    PROV("PROV"),
    CNCL("CNCL");

    public final String label;

    HearingStatusCode(String label) {
        this.label = label;
    }

}
