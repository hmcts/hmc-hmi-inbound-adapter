package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;

import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class Hearing {

    @NotEmpty(message = ValidationError.HEARING_ID_CASE_HQ_EMPTY)
    private String hearingIdCaseHQ;

    @NotEmpty(message = ValidationError.HEARING_TYPE_EMPTY)
    private String hearingType;

    @NotNull(message = ValidationError.HEARING_STARTING_TIME_EMPTY)
    private LocalDateTime hearingStartTime;

    @NotNull(message = ValidationError.HEARING_END_TIME_EMPTY)
    private LocalDateTime hearingEndTime;

    @JsonProperty("hearingCaseIdHMCTS")
    @NotEmpty(message = ValidationError.HEARING_CASE_ID_HMCTS_EMPTY)
    @Size(max = 30, message = ValidationError.HEARING_CASE_ID_HMCTS_INVALID_LENGTH)
    private String hearingCaseIdHmcts;

    private Integer hearingSessionIdCaseHQ;

    @NotNull(message = ValidationError.HEARING_TRANSLATOR_REQUIRED_EMPTY)
    private Boolean hearingTranslatorRequired;

    @NotEmpty(message = ValidationError.HEARING_TRANSLATOR_LANGUAGE_EMPTY)
    private String hearingTranslatorLanguage;

    @NotNull(message = ValidationError.HEARING_CREATED_DATE_EMPTY)
    private LocalDateTime hearingCreatedDate;

    @NotEmpty(message = ValidationError.HEARING_CREATED_BY_EMPTY)
    private String hearingCreatedBy;

    @NotEmpty(message = ValidationError.HEARING_VENUE_ID_EMPTY)
    private String hearingVenueId; //LOV

    private String hearingRoomId; //LOV

    @NotEmpty(message = ValidationError.HEARING_JUDGE_ID_EMPTY)
    private String hearingJudgeId;

}
