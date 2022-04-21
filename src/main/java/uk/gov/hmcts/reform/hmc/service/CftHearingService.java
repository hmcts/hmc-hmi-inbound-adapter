package uk.gov.hmcts.reform.hmc.service;

import org.springframework.http.HttpHeaders;

public interface CftHearingService {

    Integer getLatestVersion(String caseId);

    HttpHeaders validateCaseId(String caseId);

}
