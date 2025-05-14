package com.sipa.boot.java8.common.archs.resttemplate;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import com.sipa.boot.java8.common.exceptions.BaseRestServiceErrorException;
import com.sipa.boot.java8.common.services.IMessageService;

/**
 * @author sunyukun
 * @since 2019/5/8 19:59
 */
@Deprecated
public abstract class BaseRestService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseRestService.class);

    /**
     * Get rest template
     *
     * @return RestTemplate Rest template
     */
    public abstract RestTemplate getRestTemplate();

    /**
     * Get Sipa Boot MessageService
     *
     * @return Sipa Boot MessageService
     */
    public abstract IMessageService getSipaBootMessageService();

    protected <T> T post(URI uri, Object request, Class<T> clazz) {
        return responseHandler(getRestTemplate().postForEntity(uri, request, clazz));
    }

    public <T> T get(URI uri, Class<T> responseType) {
        return responseHandler(getRestTemplate().getForEntity(uri, responseType));
    }

    public <T> T patch(URI uri, Object request, Class<T> responseType) {
        return responseHandler(
            getRestTemplate().exchange(uri, HttpMethod.PATCH, new HttpEntity<>(request, null), responseType));
    }

    public <T> T delete(URI uri, Class<T> responseType) {
        return delete(uri, null, responseType);
    }

    public <T> T delete(URI uri, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) {
        return responseHandler(getRestTemplate().exchange(uri, HttpMethod.DELETE, requestEntity, responseType));
    }

    private <T> T responseHandler(ResponseEntity<T> responseEntity) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("Request failed caused by connection error, http status code is [{}]",
                responseEntity.getStatusCodeValue());
            throw new BaseRestServiceErrorException();
        }
        return responseEntity.getBody();
    }
}
