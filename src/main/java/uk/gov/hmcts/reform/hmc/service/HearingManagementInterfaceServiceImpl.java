package uk.gov.hmcts.reform.hmc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.hmc.ApplicationParams;
import uk.gov.hmcts.reform.hmc.client.hmi.model.response.CftHearingServiceRsp;
import uk.gov.hmcts.reform.hmc.client.hmi.model.response.HearingManagementInterfaceRsp;
import uk.gov.hmcts.reform.hmc.exceptions.ResourceNotFoundException;
import uk.gov.hmcts.reform.hmc.exceptions.ServiceException;

import java.util.Collections;

import static uk.gov.hmcts.reform.hmc.constants.Constants.CFT_SERVICE_DOWN_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND_MSG;

@Service
public class HearingManagementInterfaceServiceImpl implements HearingManagementInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(HearingManagementInterfaceServiceImpl.class);

    @Qualifier("cftServiceRestTemplate")
    @Autowired
    private final RestTemplate restTemplate;

    private final ApplicationParams applicationParams;


    public HearingManagementInterfaceServiceImpl(final RestTemplate restTemplate,
                                                 final ApplicationParams applicationParams) {
        this.restTemplate = restTemplate;
        this.applicationParams = applicationParams;
    }

    @Override
    public HearingManagementInterfaceRsp execute(String caseId) {
        isValidCaseId(caseId);
        return getResponseFromHmi(caseId);
    }

    @Override
    public HearingManagementInterfaceRsp getResponseFromHmi(String caseId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, headers);
            return restTemplate.exchange(applicationParams.hmiHearingPutUrl(caseId), HttpMethod.PUT, requestEntity,
                                         HearingManagementInterfaceRsp.class).getBody();
        } catch (Exception e) {
            logger.warn("Error while retrieving response from HMI for hearing Id:{}", caseId, e);
            if (e instanceof HttpClientErrorException
                && ((HttpClientErrorException) e).getRawStatusCode() == RESOURCE_NOT_FOUND) {
                throw new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
            } else {
                logger.warn("Error while retrieving response from HMI for hearing Id:{}", caseId, e);
                throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
            }
        }
    }

    @Override
    public CftHearingServiceRsp isValidCaseId(String caseId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, headers);
            return restTemplate.exchange(applicationParams.cftHearingValidatecaseIdUrl(caseId),
                                         HttpMethod.GET, requestEntity, CftHearingServiceRsp.class).getBody();
        } catch (Exception e) {
            logger.warn("Error while validating hearing Id:{}", caseId, e);
            if (e instanceof HttpClientErrorException
                && ((HttpClientErrorException) e).getRawStatusCode() == RESOURCE_NOT_FOUND) {
                throw new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
            } else {
                logger.warn("Error while validating hearing Id:{}", caseId, e);
                throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
            }
        }
    }
}
