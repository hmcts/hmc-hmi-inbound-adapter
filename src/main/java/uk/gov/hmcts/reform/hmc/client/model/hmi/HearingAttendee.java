package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;

@Data
@NoArgsConstructor
public class HearingAttendee {

    private String entityIdCaseHQ;
    @Size(max = 40, message = ValidationError.HEARING_ATTENDEE_ENTITY_ID_LENGTH)
    private String entityId;
    private String entityType;
    private String entityClass;
    private JsonNode entityRole;
    private HearingChannel hearingChannel;
}
