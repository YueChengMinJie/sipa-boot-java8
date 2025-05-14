package com.sipa.boot.java8.common.zuul.filter.pre;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.CodecUtils;
import com.sipa.boot.java8.common.zuul.property.ZuulProperties;
import com.sipa.boot.java8.common.zuul.rest.PrivateValidationRestService;
import com.sipa.boot.java8.common.zuul.util.HttpServletUtils;

/**
 * @author zhouxiajie
 * @date 2019-01-31
 */
@Component
@ConditionalOnProperty(prefix = "sipa.boot.zuul.local", name = "enable", havingValue = "true")
public class PrivateValidationFilter extends ZuulFilter {
    private static final Log LOGGER = LogFactory.get(PrivateValidationFilter.class);

    public static final String PRIVATE_TYPE_LOCAL = "local";

    public static final String PRIVATE_TYPE_PRIVATE = "private";

    private final ZuulProperties properties;

    private final PrivateValidationRestService privateValidationRestService;

    public PrivateValidationFilter(ZuulProperties properties,
        PrivateValidationRestService privateValidationRestService) {
        this.properties = properties;
        this.privateValidationRestService = privateValidationRestService;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SERVLET_DETECTION_FILTER_ORDER - 97;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = HttpServletUtils.getRequest();

        LOGGER.info("{} request {} {}", StringUtils.repeat("*", 10), request.getRequestURI(),
            StringUtils.repeat("*", 10));

        return "/uaa/oauth/token".equals(request.getRequestURI());
    }

    @Override
    public Object run() throws ZuulException {
        File file = FileUtils.getFile(properties.getLocal().getKeyPath(), "appKey");
        String appKey = "";
        try {
            appKey = StringUtils.deleteWhitespace(StringUtils.chomp(FileUtils.readFileToString(file, "utf-8")));
        } catch (IOException e) {
            LOGGER.error("Get app key error.", e);
        }

        String privateType = properties.getLocal().getType();
        if (PRIVATE_TYPE_LOCAL.equals(privateType)) {
            Boolean isPass = privateValidationRestService.localValidation(appKey);
            if (!isPass) {
                throw new ZuulException("Access denied by remote", HttpStatus.SC_FORBIDDEN, "Access Denied By Remote");
            }
        } else if (PRIVATE_TYPE_PRIVATE.equals(privateType)) {
            appKey = CodecUtils.TripleDes.encryptReturnHex(appKey.getBytes());
            Boolean isPass = privateValidationRestService.privateValidation(appKey);
            if (!isPass) {
                throw new ZuulException("Access denied by remote", HttpStatus.SC_FORBIDDEN, "Access Denied By Remote");
            }
        }
        return null;
    }
}
