package uk.gov.hmcts.reform.hmc.service;

import org.springframework.http.HttpHeaders;

public interface CftHearingService {

    Integer getLatestVersion(HttpHeaders headers, String caseId);

    HttpHeaders getHearingVersionHeaders(String caseId);

    void isHearingInTerminalState(HttpHeaders headers, String caseId);
}
