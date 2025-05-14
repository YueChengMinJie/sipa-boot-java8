package com.sipa.boot.java8.common.resttemplate.interceptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import com.sipa.boot.java8.common.log.property.CommonLoggingProperties;
import com.sipa.boot.java8.common.resttemplate.property.RestTemplateProperties;
import com.sipa.boot.java8.common.supports.IRequestLoggingSupport;

/**
 * @author sunyukun
 * @since 2019/5/9 14:21
 */
public abstract class AbstractLoggingHttpRequestInterceptor implements IRequestLoggingSupport {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractLoggingHttpRequestInterceptor.class);

    private final RestTemplateProperties.LoggingProperties properties;

    public AbstractLoggingHttpRequestInterceptor(RestTemplateProperties.LoggingProperties properties) {
        this.properties = properties;
    }

    protected void logBeforeMessage(HttpRequest request, byte[] body) {
        LOGGER.info(createMessage(request, body, getProperties().getBeforeMessagePrefix(),
            getProperties().getBeforeMessageSuffix(), true), request.getMethod());
    }

    protected void logAfterMessage(HttpRequest request, ClientHttpResponse response, byte[] body, long startTime) {
        try {
            LOGGER.info(
                createMessage(request, body, getProperties().getAfterMessagePrefix(),
                    getProperties().getAfterMessageSuffix(), false),
                request.getMethod(), response.getStatusCode().value(), System.currentTimeMillis() - startTime);
        } catch (IOException e) {
            LOGGER.error("GetStatusCode error.", e);
        }
    }

    protected String createMessage(HttpRequest request, byte[] body, String prefix, String suffix,
        boolean includePayload) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getURI());

        // add this PLACE_HOLDER for log statistic (same format as CommonLoggingFilter.java)
        msg.append(" client=").append(CommonLoggingProperties.PLACE_HOLDER);

        if (this.properties.isIncludeHeaders()) {
            buildHeader(msg, request.getHeaders(), this.properties.getHeaderWhiteList(),
                this.properties.getHeaderBlackList());
        } else {
            msg.append(" headers=").append(CommonLoggingProperties.PLACE_HOLDER);
        }

        if (this.properties.isIncludePayload() && includePayload) {
            int length = Math.min(body.length, this.getProperties().getMaxPayloadLength());
            String payload;
            try {
                payload = ArrayUtils.isEmpty(body) ? CommonLoggingProperties.PLACE_HOLDER
                    : new String(body, 0, length, StandardCharsets.UTF_8.name());
                if (StringUtils.isNotBlank(payload)) {
                    payload = filterPayload(payload);
                }
            } catch (UnsupportedEncodingException ex) {
                payload = CommonLoggingProperties.UNKNOWN;
            }

            msg.append(" payload=").append(payload);
        } else {
            msg.append(" payload=").append(CommonLoggingProperties.PLACE_HOLDER);
        }

        msg.append(suffix);

        return msg.toString();
    }

    protected RestTemplateProperties.LoggingProperties getProperties() {
        return properties;
    }
}
