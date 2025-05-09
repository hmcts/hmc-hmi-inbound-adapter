package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Hearing {

    private String listingRequestId;

    @NotNull(message = ValidationError.HEARING_CASE_VERSION_ID_NULL)
    private Integer hearingCaseVersionId;

    @JsonProperty("hearingCaseIdHMCTS")
    private String hearingCaseIdHmcts;

    private JsonNode hearingCaseJurisdiction;

    @NotNull(message = ValidationError.HEARING_CASE_STATUS_NULL)
    @Valid
    private HearingCaseStatus hearingCaseStatus;

    @Valid
    private HearingStatus hearingStatus;

    private String hearingIdCaseHQ;

    private JsonNode hearingType;

    @Size(max = 70, message = ValidationError.HEARING_CANCELLATION_REASON_LENGTH)
    private String hearingCancellationReason;

    private LocalDateTime hearingStartTime;

    private LocalDateTime hearingEndTime;

    private Boolean hearingPrivate;

    private Boolean hearingRisk;

    private Boolean hearingTranslatorRequired;

    private LocalDateTime hearingCreatedDate;

    private String hearingCreatedBy;

    private HearingVenue hearingVenue;

    private HearingRoom hearingRoom;

    private String hearingVhStatus;

    private String hearingVhId;

    private String hearingVhGroupId;

    private List<HearingAttendee> hearingAttendees;

    private List<HearingJoh> hearingJohs;

    private List<HearingSession> hearingSessions;
}
