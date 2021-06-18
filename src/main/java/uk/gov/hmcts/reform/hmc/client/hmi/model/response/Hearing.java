package uk.gov.hmcts.reform.hmc.client.hmi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Hearing {

    private String hearingIdCaseHQ;

    private String hearingType;

    private LocalDateTime hearingStartTime;

    private LocalDateTime hearingEndTime;

    @JsonProperty("hearingCaseIdHMCTS")
    private String hearingCaseIdHmcts;

    private String hearingSessionIdCaseHQ;

    private Boolean hearingTranslatorRequired;

    private String hearingTranslatorLanguage;

    private LocalDateTime hearingCreatedDate;

    private String hearingCreatedBy;

    private String hearingVenueId; //LOV

    private String hearingRoomId; //LOV

    private String hearingJudgeId;

    public Hearing(String hearingIdCaseHQ, String hearingType, LocalDateTime hearingStartTime,
                   LocalDateTime hearingEndTime, String hearingCaseIdHmcts, String hearingSessionIdCaseHQ,
                   Boolean hearingTranslatorRequired, String hearingTranslatorLanguage,
                   LocalDateTime hearingCreatedDate, String hearingCreatedBy, String hearingVenueId,
                   String hearingRoomId, String hearingJudgeId) {
        this.hearingIdCaseHQ = hearingIdCaseHQ;
        this.hearingType = hearingType;
        this.hearingStartTime = hearingStartTime;
        this.hearingEndTime = hearingEndTime;
        this.hearingCaseIdHmcts = hearingCaseIdHmcts;
        this.hearingSessionIdCaseHQ = hearingSessionIdCaseHQ;
        this.hearingTranslatorRequired = hearingTranslatorRequired;
        this.hearingTranslatorLanguage = hearingTranslatorLanguage;
        this.hearingCreatedDate = hearingCreatedDate;
        this.hearingCreatedBy = hearingCreatedBy;
        this.hearingVenueId = hearingVenueId;
        this.hearingRoomId = hearingRoomId;
        this.hearingJudgeId = hearingJudgeId;
    }

    public String getHearingIdCaseHQ() {
        return hearingIdCaseHQ;
    }

    public void setHearingIdCaseHQ(String hearingIdCaseHQ) {
        this.hearingIdCaseHQ = hearingIdCaseHQ;
    }

    public String getHearingType() {
        return hearingType;
    }

    public void setHearingType(String hearingType) {
        this.hearingType = hearingType;
    }

    public LocalDateTime getHearingStartTime() {
        return hearingStartTime;
    }

    public void setHearingStartTime(LocalDateTime hearingStartTime) {
        this.hearingStartTime = hearingStartTime;
    }

    public LocalDateTime getHearingEndTime() {
        return hearingEndTime;
    }

    public void setHearingEndTime(LocalDateTime hearingEndTime) {
        this.hearingEndTime = hearingEndTime;
    }

    public String getHearingCaseIdHmcts() {
        return hearingCaseIdHmcts;
    }

    public void setHearingCaseIdHmcts(String hearingCaseIdHmcts) {
        this.hearingCaseIdHmcts = hearingCaseIdHmcts;
    }

    public String getHearingSessionIdCaseHQ() {
        return hearingSessionIdCaseHQ;
    }

    public void setHearingSessionIdCaseHQ(String hearingSessionIdCaseHQ) {
        this.hearingSessionIdCaseHQ = hearingSessionIdCaseHQ;
    }

    public Boolean getHearingTranslatorRequired() {
        return hearingTranslatorRequired;
    }

    public void setHearingTranslatorRequired(Boolean hearingTranslatorRequired) {
        this.hearingTranslatorRequired = hearingTranslatorRequired;
    }

    public String getHearingTranslatorLanguage() {
        return hearingTranslatorLanguage;
    }

    public void setHearingTranslatorLanguage(String hearingTranslatorLanguage) {
        this.hearingTranslatorLanguage = hearingTranslatorLanguage;
    }

    public LocalDateTime getHearingCreatedDate() {
        return hearingCreatedDate;
    }

    public void setHearingCreatedDate(LocalDateTime hearingCreatedDate) {
        this.hearingCreatedDate = hearingCreatedDate;
    }

    public String getHearingCreatedBy() {
        return hearingCreatedBy;
    }

    public void setHearingCreatedBy(String hearingCreatedBy) {
        this.hearingCreatedBy = hearingCreatedBy;
    }

    public String getHearingVenueId() {
        return hearingVenueId;
    }

    public void setHearingVenueId(String hearingVenueId) {
        this.hearingVenueId = hearingVenueId;
    }

    public String getHearingRoomId() {
        return hearingRoomId;
    }

    public void setHearingRoomId(String hearingRoomId) {
        this.hearingRoomId = hearingRoomId;
    }

    public String getHearingJudgeId() {
        return hearingJudgeId;
    }

    public void setHearingJudgeId(String hearingJudgeId) {
        this.hearingJudgeId = hearingJudgeId;
    }
}
