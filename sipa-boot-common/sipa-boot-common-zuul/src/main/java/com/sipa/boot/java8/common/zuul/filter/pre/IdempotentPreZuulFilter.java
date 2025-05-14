package com.sipa.boot.java8.common.zuul.filter.pre;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.sipa.boot.java8.common.archs.cost.TraceWatch;
import com.sipa.boot.java8.common.archs.cost.TraceWatchFactory;
import com.sipa.boot.java8.common.archs.cost.TraceWatchUtils;
import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.JsonUtils;
import com.sipa.boot.java8.common.zuul.common.ZuulConstants;
import com.sipa.boot.java8.data.redis.utils.RedisUtils;

/**
 * @author zhouxiajie
 * @date 2020/12/22
 */
@Component
@ConditionalOnProperty(prefix = "sipa.boot.zuul", name = "enableIdempotent", havingValue = "true")
public class IdempotentPreZuulFilter extends ZuulFilter {
    private static final Log LOGGER = LogFactory.get(IdempotentPreZuulFilter.class);

    private static final long LIMIT_TIME = 3L;

    private static final List<String> METHOD_WHITE_LIST =
        Lists.newArrayList(HttpMethod.GET.name(), HttpMethod.OPTIONS.name());

    private static final List<String> URI_WHITE_LIST =
        Lists.newArrayList(ZuulConstants.TOKEN_BASE2, ZuulConstants.TOKEN_BASE3);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        // 非GET & OPTION 进行拦截判断
        String method = request.getMethod().toUpperCase();
        String uri = request.getRequestURI();
        String contentType = request.getContentType();
        if (!METHOD_WHITE_LIST.contains(method) && !URI_WHITE_LIST.contains(uri) && (Objects.isNull(contentType)
            || ZuulConstants.CONTENT_TYPE_WHITE_LIST.stream().noneMatch(contentType::contains))) {
            TraceWatch traceWatch = TraceWatchFactory.newTraceWatch();

            traceWatch.start();
            byte[] requestParamsBytes = null;
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (MapUtils.isNotEmpty(parameterMap)) {
                String parameterMapJsonStr = JsonUtils.writeValueAsString(parameterMap);
                requestParamsBytes = parameterMapJsonStr.getBytes(StandardCharsets.UTF_8);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("parameterMapJsonStr [{}]", parameterMapJsonStr);
                }
            }

            String body = getBody(request);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("body [{}]", body);
            }
            byte[] bodyBytes = null;
            if (StringUtils.isNotBlank(body)) {
                bodyBytes = body.getBytes(StandardCharsets.UTF_8);
            }

            context.setRequest(modifyRequest(request, body));

            byte[] resultBytes = uri.getBytes(StandardCharsets.UTF_8);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("uri [{}]", uri);
            }
            if (ArrayUtils.isNotEmpty(requestParamsBytes)) {
                resultBytes = Bytes.concat(resultBytes, requestParamsBytes);
            }
            if (ArrayUtils.isNotEmpty(bodyBytes)) {
                resultBytes = Bytes.concat(resultBytes, bodyBytes);
            }

            String requestHex = DigestUtils.md5Hex(resultBytes);
            traceWatch.stop();

            String key = MessageFormat.format("{0}:{1}", ZuulConstants.IDEMPOTENT, requestHex);
            String value = RedisUtils.get(key);
            boolean exists = false;
            if (StringUtils.isNotBlank(value)) {
                exists = true;
            } else {
                RedisUtils.set(key, requestHex, LIMIT_TIME + TraceWatchUtils.getCost(traceWatch));
            }

            if (exists) {
                context.setSendZuulResponse(false);

                HttpServletResponse response = context.getResponse();
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(400);

                try (PrintWriter writer = response.getWriter()) {
                    writer.write(JsonUtils.writeValueAsString(new ErrorEntity("请求已提交，请稍后提交")));
                } catch (IOException e) {
                    LOGGER.error("write response fail", e);
                }
            }
        }

        return null;
    }

    private String getBody(final HttpServletRequest request) {
        try {
            InputStream in = request.getInputStream();
            if (Objects.nonNull(in)) {
                return StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            LOGGER.error("read body fail", e);
        }
        return StringUtils.EMPTY;
    }

    private HttpServletRequestWrapper modifyRequest(HttpServletRequest request, String body) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public byte[] getContentData() {
                return body.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public int getContentLength() {
                return body.getBytes(StandardCharsets.UTF_8).length;
            }

            @Override
            public long getContentLengthLong() {
                return body.getBytes(StandardCharsets.UTF_8).length;
            }

            @Override
            public BufferedReader getReader() {
                return new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))));
            }

            @Override
            public ServletInputStream getInputStream() {
                return new ServletInputStreamWrapper(body.getBytes(StandardCharsets.UTF_8));
            }
        };
    }
}
