package com.sipa.boot.java8.common.archs.resttemplate;

import java.net.URI;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import com.sipa.boot.java8.common.exceptions.BaseRestServiceErrorException;

/**
 * @author xiajiezhou
 * @since 2020/2/9 18:59
 */
public abstract class AbstractRestService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestService.class);

    protected static final String HTTP_HOST_WITH_PORT_PATH = "http://{0}{1}";

    /**
     * Get rest template
     *
     * @return RestTemplate Rest template
     */
    public abstract RestTemplate getRestTemplate(

    );

    // ********************************************************
    // ************************* get **************************
    // ********************************************************
    protected <T> T get(URI uri, Class<T> responseType) {
        return responseHandler(getRestTemplate().getForEntity(uri, responseType));
    }

    protected <T> T get(String hostWithPort, String path, Class<T> responseType) {
        return responseHandler(
            getRestTemplate().getForEntity(getHttpHostWithPortPath(hostWithPort, path), responseType));
    }

    // ********************************************************
    // ************************* post *************************
    // ********************************************************
    protected <T> T post(URI uri, Object request, Class<T> clazz) {
        return responseHandler(getRestTemplate().postForEntity(uri, request, clazz));
    }

    protected <T> T post(String hostWithPort, String path, Object request, Class<T> clazz) {
        return responseHandler(
            getRestTemplate().postForEntity(getHttpHostWithPortPath(hostWithPort, path), request, clazz));
    }

    // ********************************************************
    // ************************ patch *************************
    // ********************************************************
    protected <T> T patch(URI uri, Object request, Class<T> responseType) {
        return responseHandler(
            getRestTemplate().exchange(uri, HttpMethod.PATCH, new HttpEntity<>(request, null), responseType));
    }

    // ********************************************************
    // ************************ delete ************************
    // ********************************************************
    protected <T> T delete(URI uri, Class<T> responseType) {
        return delete(uri, null, responseType);
    }

    protected <T> T delete(URI uri, @Nullable HttpEntity<?> requestEntity, Class<T> responseType) {
        return responseHandler(getRestTemplate().exchange(uri, HttpMethod.DELETE, requestEntity, responseType));
    }

    // ********************************************************
    // ********************* interceptor **********************
    // ********************************************************
    protected <T> T responseHandler(ResponseEntity<T> responseEntity) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("Request failed caused by connection error, http status code is [{}]",
                responseEntity.getStatusCodeValue());
            throw new BaseRestServiceErrorException();
        }
        return responseEntity.getBody();
    }

    // ********************************************************
    // *********************** common *************************
    // ********************************************************
    protected String getHttpHostWithPortPath(String hostWithPort, String path) {
        return MessageFormat.format(HTTP_HOST_WITH_PORT_PATH, hostWithPort, path);
    }
}
