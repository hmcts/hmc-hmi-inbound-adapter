package uk.gov.hmcts.reform.hmc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private static final String LATEST_HEARING_REQUEST_VERSION = "latestHearingRequestVersion";

    public CftHearingServiceImpl(RestTemplate restTemplate,
                                 ApplicationParams applicationParams,
                                 SecurityUtils securityUtils) {
        this.restTemplate = restTemplate;
        this.applicationParams = applicationParams;
        this.securityUtils = securityUtils;
    }

    @Override
    public Integer getLatestVersion(String caseId) {
        HttpHeaders headers = validateCaseId(caseId);
        Integer latestVersion = 0;
        if (headers.containsKey(LATEST_HEARING_REQUEST_VERSION)) {
            List<String> values = headers.get(LATEST_HEARING_REQUEST_VERSION);
            if (null != values && !values.isEmpty()) {
                latestVersion = Integer.parseInt(values.get(0));
            }
        }
        return latestVersion;
    }

    @Override
    public HttpHeaders validateCaseId(String caseId) {
        try {
            var httpHeaders = securityUtils.authorizationHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, httpHeaders);
            return restTemplate.exchange(applicationParams.cftHearingValidateCaseIdUrl(caseId),
                    HttpMethod.GET, requestEntity, HttpStatus.class).getHeaders();
        } catch (HttpClientErrorException e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
        } catch (Exception e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
        }
    }

}
