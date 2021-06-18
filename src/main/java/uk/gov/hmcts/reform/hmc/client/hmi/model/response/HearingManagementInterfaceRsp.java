package uk.gov.hmcts.reform.hmc.client.hmi.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HearingManagementInterfaceRsp {

    private String apiVersion;

    private String[] transactionIdCaseHQ;

    private String[] hearingIdCaseHQ;

    private String[] hearingType;

    private LocalDateTime hearingStartTime;

    @JsonProperty("hearingCaseIdHMCTS")
    private LocalDateTime hearingEndTime;

    private String hearingCaseIdHmcts;

    private Integer hearingSessionIdCaseHQ;

    private Boolean hearingTranslatorRequired;

    private String[] hearingTranslatorLanguage;

    private LocalDateTime hearingCreatedDate;

    private LocalDateTime hearingCreatedBy;

    private String hearingVenueId;

    private String hearingRoomId;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String[] getTransactionIdCaseHQ() {
        return transactionIdCaseHQ;
    }

    public void setTransactionIdCaseHQ(String[] transactionIdCaseHQ) {
        this.transactionIdCaseHQ = transactionIdCaseHQ;
    }

    public String[] getHearingIdCaseHQ() {
        return hearingIdCaseHQ;
    }

    public void setHearingIdCaseHQ(String[] hearingIdCaseHQ) {
        this.hearingIdCaseHQ = hearingIdCaseHQ;
    }

    public String[] getHearingType() {
        return hearingType;
    }

    public void setHearingType(String[] hearingType) {
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

    public Integer getHearingSessionIdCaseHQ() {
        return hearingSessionIdCaseHQ;
    }

    public void setHearingSessionIdCaseHQ(Integer hearingSessionIdCaseHQ) {
        this.hearingSessionIdCaseHQ = hearingSessionIdCaseHQ;
    }

    public Boolean getHearingTranslatorRequired() {
        return hearingTranslatorRequired;
    }

    public void setHearingTranslatorRequired(Boolean hearingTranslatorRequired) {
        this.hearingTranslatorRequired = hearingTranslatorRequired;
    }

    public String[] getHearingTranslatorLanguage() {
        return hearingTranslatorLanguage;
    }

    public void setHearingTranslatorLanguage(String[] hearingTranslatorLanguage) {
        this.hearingTranslatorLanguage = hearingTranslatorLanguage;
    }

    public LocalDateTime getHearingCreatedDate() {
        return hearingCreatedDate;
    }

    public void setHearingCreatedDate(LocalDateTime hearingCreatedDate) {
        this.hearingCreatedDate = hearingCreatedDate;
    }

    public LocalDateTime getHearingCreatedBy() {
        return hearingCreatedBy;
    }

    public void setHearingCreatedBy(LocalDateTime hearingCreatedBy) {
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

    public String[] getHearingJudgeId() {
        return hearingJudgeId;
    }

    public void setHearingJudgeId(String[] hearingJudgeId) {
        this.hearingJudgeId = hearingJudgeId;
    }

    private String[] hearingJudgeId;

}
