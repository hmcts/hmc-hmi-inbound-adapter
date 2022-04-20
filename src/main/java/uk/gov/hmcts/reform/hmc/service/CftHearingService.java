package uk.gov.hmcts.reform.hmc.service;

import org.springframework.http.ResponseEntity;

public interface CftHearingService {

    Integer getLatestVersion(String caseId);

    ResponseEntity validateCaseId(String caseId);

}
