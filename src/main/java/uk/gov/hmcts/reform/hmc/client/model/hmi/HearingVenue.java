package uk.gov.hmcts.reform.hmc.client.model.hmi;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class HearingVenue {
    private String locationIdCaseHQ;
    private String locationName;
    private JsonNode locationRegion;
    private JsonNode locationCluster;
    @Valid
    private ArrayList<VenueLocationReference> locationReference;
}
