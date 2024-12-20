package uk.gov.hmcts.reform.hmc.client.model.hmi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;

@Data
@NoArgsConstructor
@Validated
public class HearingResponse {

    @NotNull(message = ValidationError.META_EMPTY)
    @Valid
    private MetaResponse meta;

    @NotNull(message = ValidationError.HEARING_EMPTY)
    @Valid
    private Hearing hearing;

}
