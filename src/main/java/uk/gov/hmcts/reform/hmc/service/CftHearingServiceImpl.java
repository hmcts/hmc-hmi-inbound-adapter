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

import static uk.gov.hmcts.reform.hmc.constants.Constants.CFT_SERVICE_DOWN_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND_MSG;

@Service
@Slf4j
public class CftHearingServiceImpl implements CftHearingService {

    @Qualifier("cftServiceRestTemplate")
    private final RestTemplate restTemplate;

    private final ApplicationParams applicationParams;
    private final SecurityUtils securityUtils;

    public CftHearingServiceImpl(RestTemplate restTemplate,
                                 ApplicationParams applicationParams,
                                 SecurityUtils securityUtils) {
        this.restTemplate = restTemplate;
        this.applicationParams = applicationParams;
        this.securityUtils = securityUtils;
    }

    @Override
    public boolean isValidCaseId(String caseId) {
        try {
            var httpHeaders = securityUtils.serviceAuthorizationHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, httpHeaders);
            restTemplate.exchange(applicationParams.cftHearingValidateCaseIdUrl(caseId),
                                  HttpMethod.GET, requestEntity, HttpStatus.class);
            return true;
        } catch (HttpClientErrorException e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
        } catch (Exception e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
        }
    }

}
