package uk.gov.hmcts.reform.hmc.client.model.hmi;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.reform.hmc.exceptions.ValidationError;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
