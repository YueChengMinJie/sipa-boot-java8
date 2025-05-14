package com.sipa.boot.java8.common.mvc.filter.common;

import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.google.common.base.Throwables;
import com.sipa.boot.java8.common.log.property.CommonLoggingProperties;
import com.sipa.boot.java8.common.log.property.RequestLoggingProperties;
import com.sipa.boot.java8.common.supports.IRequestLoggingSupport;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public class LoggingFilter extends AbstractRequestLoggingFilter implements IRequestLoggingSupport {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    protected RequestLoggingProperties properties;

    private final boolean enableCache;

    private final PathMatcher pathMatcher;

    public LoggingFilter(boolean enableCache, RequestLoggingProperties properties) {
        this.enableCache = enableCache;
        this.properties = properties;
        this.pathMatcher = new AntPathMatcher();

        super.setIncludeQueryString(this.properties.isIncludeQueryString());
        super.setIncludeClientInfo(this.properties.isIncludeClientInfo());
        super.setIncludeHeaders(this.properties.isIncludeHeaders());
        super.setIncludePayload(this.properties.isIncludePayload());
        super.setMaxPayloadLength(this.properties.getMaxPayloadLength());
        super.setBeforeMessagePrefix(this.properties.getBeforeMessagePrefix());
        super.setBeforeMessageSuffix(this.properties.getBeforeMessageSuffix());
        super.setAfterMessagePrefix(this.properties.getAfterMessagePrefix());
        super.setAfterMessageSuffix(this.properties.getAfterMessageSuffix());
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain) {
        boolean isFirstRequest = !isAsyncDispatch(request);

        HttpServletRequest requestToUse = request;
        if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
        }

        boolean shouldLog = shouldLog(requestToUse);
        if (shouldLog && isFirstRequest) {
            beforeRequest(requestToUse, getBeforeMessage(requestToUse));
        }

        HttpServletResponse wrappedResponse = getWrappedResponseFrom(response);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            filterChain.doFilter(requestToUse, wrappedResponse);
        } catch (Exception e) {
            // should not reach here since ex are handled by ExceptionAdviceTrait
            LOGGER.error("Final point to log error before req life cycle finished.", e);
            Throwables.throwIfUnchecked(e);
        } finally {
            stopWatch.stop();

            if (shouldLog && !isAsyncStarted(requestToUse)) {
                afterRequest(requestToUse, getAfterMessage(requestToUse), wrappedResponse,
                    stopWatch.getTotalTimeMillis());
            }
        }
    }

    private String getBeforeMessage(HttpServletRequest request) {
        return createMessage(request, this.properties.getBeforeMessagePrefix(),
            this.properties.getBeforeMessageSuffix());
    }

    private String getAfterMessage(HttpServletRequest request) {
        return createMessage(request, this.properties.getAfterMessagePrefix(), this.properties.getAfterMessageSuffix());
    }

    @Nonnull
    @Override
    protected String createMessage(HttpServletRequest request, @Nonnull String prefix, @Nonnull String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            msg.append(" client=").append(StringUtils.defaultIfBlank(client, CommonLoggingProperties.PLACE_HOLDER));
        } else {
            msg.append(" client=").append(CommonLoggingProperties.PLACE_HOLDER);
        }

        if (isIncludeHeaders()) {
            buildHeader(msg, new ServletServerHttpRequest(request).getHeaders(), this.properties.getHeaderWhiteList(),
                this.properties.getHeaderBlackList());
        } else {
            msg.append(" headers=").append(CommonLoggingProperties.PLACE_HOLDER);
        }

        boolean isPayloadAppend = false;
        if (isIncludePayload()) {
            ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    int length = Math.min(buf.length, getMaxPayloadLength());
                    String payload;
                    try {
                        payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
                        if (StringUtils.isNotBlank(payload)) {
                            payload = filterPayload(payload);
                        }
                    } catch (UnsupportedEncodingException ex) {
                        payload = CommonLoggingProperties.UNKNOWN;
                    }
                    msg.append(" payload=").append(payload);
                    isPayloadAppend = true;
                }
            }
        }
        if (!isPayloadAppend) {
            msg.append(" payload=").append(CommonLoggingProperties.PLACE_HOLDER);
        }

        msg.append(suffix);

        return msg.toString();
    }

    @Override
    protected boolean shouldLog(@Nonnull HttpServletRequest request) {
        return properties.isEnabled() && isReqNotInWhiteList(request);
    }

    protected boolean isReqNotInWhiteList(HttpServletRequest request) {
        return CollectionUtils.isEmpty(properties.getBlackList())
            || properties.getBlackList().stream().noneMatch(pattern -> {
                String url = StringUtils.defaultIfBlank(request.getServletPath(), StringUtils.EMPTY);
                String pathInfo = StringUtils.defaultIfBlank(request.getPathInfo(), StringUtils.EMPTY);
                return getPathMatcher().match(pattern, url + pathInfo);
            });
    }

    protected PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, @Nonnull String message) {
        LOGGER.info(message, request.getMethod());
    }

    @Override
    protected void afterRequest(@Nonnull HttpServletRequest request, @Nonnull String message) {
        // this method is not applicable
    }

    private void afterRequest(HttpServletRequest request, String message, HttpServletResponse response, long timeCost) {
        LOGGER.info(message, request.getMethod(), response.getStatus(), timeCost);
    }

    private HttpServletResponse getWrappedResponseFrom(HttpServletResponse response) {
        return enableCache ? new ContentCachingResponseWrapper(response) : response;
    }
}
