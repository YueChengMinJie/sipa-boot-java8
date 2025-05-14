package com.sipa.boot.java8.common.zuul.rest;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.utils.CodecUtils;
import com.sipa.boot.java8.common.zuul.property.ZuulProperties;

/**
 * @author feizhihao
 * @date 2019-10-18 17:36
 */
@Component
@ConditionalOnProperty(prefix = "sipa.boot.zuul.local", name = "enable", havingValue = "true")
public class PrivateValidationRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrivateValidationRestService.class);

    private final RestTemplate restTemplate;

    private final ZuulProperties properties;

    public PrivateValidationRestService(RestTemplate restTemplate, ZuulProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * 远程认证.
     */
    public Boolean privateValidation(String appKey) {
        LOGGER.info("PrivateValidation rest api appKey [{}]", appKey);
        Map<String, Object> params = Maps.newHashMap();
        params.put("appKey", appKey);
        ResponseEntity<ResponseWrapper<String>> response =
            restTemplate.postForEntity(properties.getLocal().getCheckUrl(), null,
                ResponseWrapper.<String>successWithType().genericClass(), params);
        if (response.getStatusCode() == HttpStatus.OK && Objects.nonNull(response.getBody())) {
            String result = response.getBody().getData();
            return Boolean.parseBoolean(CodecUtils.TripleDes.decrypt(result));
        }
        return false;
    }

    /**
     * 本地认证.
     */
    public Boolean localValidation(String appKey) {
        LOGGER.info("LocalValidation rest api appKey [{}]", appKey);
        ResponseEntity<ResponseWrapper<String>> response = restTemplate.exchange(properties.getLocal().getCheckUrl(),
            HttpMethod.GET, null, new ParameterizedTypeReference<ResponseWrapper<String>>() {});
        if (response.getStatusCode() == HttpStatus.OK && Objects.nonNull(response.getBody())) {
            String dockerHostMac = response.getBody().getData();
            return appKey.equals(dockerHostMac);
        }
        return false;
    }
}
