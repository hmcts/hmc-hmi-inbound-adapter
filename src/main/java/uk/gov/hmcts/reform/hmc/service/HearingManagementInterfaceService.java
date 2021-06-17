package uk.gov.hmcts.reform.hmc.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.hmc.client.hmi.model.HearingManagementInterfaceResponse;

public interface HearingManagementInterfaceService {

    HearingManagementInterfaceResponse getResponseFromHmi(Long hearingId);

    ResponseEntity<HttpEntity> isValidHearingId(Long hearingId);
}
