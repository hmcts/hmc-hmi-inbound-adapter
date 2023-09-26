package uk.gov.hmcts.reform.hmc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.reform.hmc.exceptions.ServiceException;

import java.util.Collections;
import java.util.List;

import static uk.gov.hmcts.reform.hmc.constants.Constants.CFT_SERVICE_DOWN_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.INVALID_HEARING_STATE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.LATEST_HEARING_REQUEST_VERSION;
import static uk.gov.hmcts.reform.hmc.constants.Constants.LATEST_HEARING_STATUS;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND_MSG;
import static uk.gov.hmcts.reform.hmc.constants.Constants.VERSION_NOT_SUPPLIED;

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
    public Integer getLatestVersion(HttpHeaders headers, String caseId) {
        if (headers.containsKey(LATEST_HEARING_REQUEST_VERSION)) {
            List<String> values = headers.get(LATEST_HEARING_REQUEST_VERSION);
            if (!CollectionUtils.isEmpty(values)) {
                return Integer.parseInt(values.get(0));
            }
        }
        log.warn("Error while get latest version for case Id:{}", caseId);
        throw new ResourceNotFoundException(String.format(VERSION_NOT_SUPPLIED, caseId));
    }

    @Override
    public HttpHeaders getHearingVersionHeaders(String caseId) {
        try {
            var httpHeaders = securityUtils.authorizationHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, httpHeaders);
            log.debug("url: {}", applicationParams.cftHearingValidateCaseIdUrl(caseId));
            return restTemplate.exchange(applicationParams.cftHearingValidateCaseIdUrl(caseId),
                    HttpMethod.GET, requestEntity, HttpStatus.class).getHeaders();
        } catch (HttpClientErrorException e) {
            log.warn("Error while get hearing version headers for case Id:{}", caseId, e);
            throw new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
        } catch (Exception e) {
            log.warn("Error while validating case Id:{}", caseId, e);
            throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
        }
    }

    @Override
    public void isHearingInTerminalState(HttpHeaders headers, String caseId) {
        if (headers.containsKey(LATEST_HEARING_STATUS)) {
            List<String> values = headers.get(LATEST_HEARING_STATUS);
            if (!CollectionUtils.isEmpty(values) && applicationParams.getHmcHearingTerminalStates().contains(
                values.get(0))) {
                log.warn("case Id:{} has hearing status: {}", caseId, values.get(0));
                throw new BadRequestException(String.format(INVALID_HEARING_STATE));
            }
        }

    }
}
