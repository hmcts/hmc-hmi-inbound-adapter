package uk.gov.hmcts.reform.hmc.service.hmi;

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
import uk.gov.hmcts.reform.hmc.client.model.hmi.CftHearingServiceRsp;
import uk.gov.hmcts.reform.hmc.exceptions.BadRequestException;
import uk.gov.hmcts.reform.hmc.exceptions.ServiceException;

import java.util.Collections;

import static uk.gov.hmcts.reform.hmc.constants.Constants.CFT_SERVICE_DOWN_ERR_MESSAGE;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND;
import static uk.gov.hmcts.reform.hmc.constants.Constants.RESOURCE_NOT_FOUND_MSG;

@Service
public class CftHearingServiceImpl implements CftHearingService {

    private static final Logger logger = LoggerFactory.getLogger(CftHearingServiceImpl.class);

    @Qualifier("cftServiceRestTemplate")
    @Autowired
    private final RestTemplate restTemplate;

    private final ApplicationParams applicationParams;

    public CftHearingServiceImpl(RestTemplate restTemplate, ApplicationParams applicationParams) {
        this.restTemplate = restTemplate;
        this.applicationParams = applicationParams;
    }

    @Override
    public CftHearingServiceRsp isValidCaseId(String caseId) {
        try {
            var httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> requestEntity = new HttpEntity<>(caseId, httpHeaders);
            return restTemplate.exchange(applicationParams.cftHearingValidateCaseIdUrl(caseId),
                                         HttpMethod.GET, requestEntity, CftHearingServiceRsp.class).getBody();
        } catch (Exception e) {
            logger.warn("Error while validating case Id:{}", caseId, e);
            if (e instanceof HttpClientErrorException
                && ((HttpClientErrorException) e).getRawStatusCode() == RESOURCE_NOT_FOUND) {
                throw new BadRequestException(String.format(RESOURCE_NOT_FOUND_MSG, caseId));
            } else {
                logger.warn("Error while validating case Id:{}", caseId, e);
                throw new ServiceException(CFT_SERVICE_DOWN_ERR_MESSAGE, e);
            }
        }
    }
}
