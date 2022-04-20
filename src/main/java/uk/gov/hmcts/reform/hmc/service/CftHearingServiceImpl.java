package uk.gov.hmcts.reform.hmc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.reform.hmc.exceptions.ServiceException;

import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.reform.hmc.constants.Constants.CFT_SERVICE_DOWN_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND_MSG;

@Service
@Slf4j
public class CftHearingServiceImpl implements CftHearingService {

    @Qualifier("cftServiceRestTemplate")
    private final RestTemplate restTemplate;

    private final ApplicationParams applicationParams;
    private final SecurityUtils securityUtils;
    private static final String LatestRequestVersion = "latestHearingRequestVersion";

    public CftHearingServiceImpl(RestTemplate restTemplate,
                                 ApplicationParams applicationParams,
                                 SecurityUtils securityUtils) {
        this.restTemplate = restTemplate;
        this.applicationParams = applicationParams;
        this.securityUtils = securityUtils;
    }

    @Override
    public Integer getLatestVersion(String caseId) {
        ResponseEntity responseEntity = validateCaseId(caseId);
        Integer latestVersion = 0;
        if (responseEntity.getHeaders().containsKey(LatestRequestVersion)) {
            HttpHeaders headers = responseEntity.getHeaders();
            if (headers.containsKey(LatestRequestVersion)) {
                List<String> values = headers.get(LatestRequestVersion);
                String value = (String) values.get(0);
                latestVersion = Integer.parseInt(value);
            }
        }
        return latestVersion;
    }

    @Override
    public ResponseEntity validateCaseId(String caseId) {
        try {
            var httpHeaders = securityUtils.authorizationHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, httpHeaders);
            return restTemplate.exchange(applicationParams.cftHearingValidateCaseIdUrl(caseId),
                    HttpMethod.GET, requestEntity, HttpStatus.class);
        } catch (HttpClientErrorException e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
        } catch (Exception e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
        }
    }

}
