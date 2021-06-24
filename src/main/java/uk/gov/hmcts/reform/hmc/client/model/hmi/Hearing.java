package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.validators.HearingPayloadConstraint;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class Hearing {

    @HearingPayloadConstraint
    private String hearingIdCaseHQ;

    @HearingPayloadConstraint
    private String hearingType;

    @NotNull
    private LocalDateTime hearingStartTime;

    @NotNull
    private LocalDateTime hearingEndTime;

    @JsonProperty("hearingCaseIdHMCTS")
    @HearingPayloadConstraint
    @Size(max = 30, message = "hearing case Id must not be greater than 30 characters")
    private String hearingCaseIdHmcts;

    private Integer hearingSessionIdCaseHQ;

    @NotNull(message = "HearingTranslatorRequired must not be null")
    private Boolean hearingTranslatorRequired;

    @HearingPayloadConstraint
    private String hearingTranslatorLanguage;

    @NotNull(message = "HearingCreatedDate must not be null")
    private LocalDateTime hearingCreatedDate;

    @NotNull(message = "HearingCreatedBy must not be null")
    private String hearingCreatedBy;

    @HearingPayloadConstraint
    private String hearingVenueId; //LOV

    private String hearingRoomId; //LOV

    @HearingPayloadConstraint
    private String hearingJudgeId;

}
