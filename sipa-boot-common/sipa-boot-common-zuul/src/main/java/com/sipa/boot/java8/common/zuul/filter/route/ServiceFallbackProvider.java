package com.sipa.boot.java8.common.zuul.filter.route;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.enums.EResCode;

/**
 * @author zhouxiajie
 * @date 2019-01-18
 */
@Component
public class ServiceFallbackProvider implements FallbackProvider {
    private final ObjectMapper objectMapper;

    public ServiceFallbackProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getRoute() {
        return "*-*-service";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return response(SipaBootCommonConstants.GLOBAL_MSG, EResCode.FALLBACK_GATEWAY_TIMEOUT);
        } else {
            return response(cause.getMessage(), EResCode.FALLBACK_INTERNAL_SERVER_ERROR);
        }
    }

    private ClientHttpResponse response(String msg, EResCode eResCode) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {}

            @Override
            public InputStream getBody() throws IOException {
                ResponseWrapper<Object> res = ResponseWrapper.errorOf(msg, eResCode);

                return new ByteArrayInputStream(objectMapper.writeValueAsBytes(res));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return headers;
            }
        };
    }
}
