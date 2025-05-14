package com.sipa.boot.java8.common.resttemplate.interceptor;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.sipa.boot.java8.common.resttemplate.property.RestTemplateProperties;

/**
 * @author sunyukun
 * @since 2019/5/9 14:21
 */
public class LoggingClientHttpRequestInterceptor extends AbstractLoggingHttpRequestInterceptor
    implements ClientHttpRequestInterceptor {
    public LoggingClientHttpRequestInterceptor(RestTemplateProperties.LoggingProperties properties) {
        super(properties);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        long startTime = System.currentTimeMillis();
        logBeforeMessage(request, body);

        ClientHttpResponse response = execution.execute(request, body);
        logAfterMessage(request, response, body, startTime);

        return response;
    }
}
