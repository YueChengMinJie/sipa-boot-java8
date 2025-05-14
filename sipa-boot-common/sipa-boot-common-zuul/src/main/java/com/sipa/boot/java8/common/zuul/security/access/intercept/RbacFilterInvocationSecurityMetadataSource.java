package com.sipa.boot.java8.common.zuul.security.access.intercept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.JsonUtils;
import com.sipa.boot.java8.common.utils.LogUtils;
import com.sipa.boot.java8.common.zuul.common.ZuulConstants;
import com.sipa.boot.java8.common.zuul.property.ZuulProperties;
import com.sipa.boot.java8.common.zuul.service.IRoleService;

/**
 * @author feizhihao
 * @date 2019-07-12 15:52
 */
@Component
public class RbacFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    public static final Log LOGGER = LogFactory.get(RbacFilterInvocationSecurityMetadataSource.class);

    private final IRoleService roleService;

    private final ObjectMapper objectMapper;

    private final PathMatcher matcher;

    private final String ignoring;

    private final String intercept;

    public RbacFilterInvocationSecurityMetadataSource(@Autowired(required = false) IRoleService roleService,
        @Autowired(required = false) ObjectMapper objectMapper, ZuulProperties zuulProperties) {
        this.roleService = roleService;
        this.objectMapper = objectMapper;

        this.matcher = new AntPathMatcher();

        this.ignoring = zuulProperties.getSecurity().getIgnoring();
        this.intercept = zuulProperties.getSecurity().getIntercept();
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String url = ((FilterInvocation)object).getRequestUrl();
        String method = ((FilterInvocation)object).getHttpRequest().getMethod();
        int firstQuestionMarkIndex = url.indexOf("?");
        if (firstQuestionMarkIndex != -1) {
            url = url.substring(0, firstQuestionMarkIndex);
        }

        List<ConfigAttribute> result = new ArrayList<>();
        try {
            if (StringUtils.isNotBlank(ignoring)) {
                for (String path : ignoring.split(",")) {
                    String tmp = path.replaceAll(" ", "");
                    if (matcher.match(tmp, url)) {
                        LogUtils.debug(LOGGER, "Match uri ignoring, path [{}], url [{}]", tmp, url);
                        ConfigAttribute attribute = new SecurityConfig(ZuulConstants.SECURITY_PASS_ROLE);
                        result.add(attribute);
                        return result;
                    }
                }
            }

            if (!isIntercept(url)) {
                LogUtils.debug(LOGGER, "Match uri not intercept, intercept [{}], url [{}]", intercept, url);
                ConfigAttribute attribute = new SecurityConfig(ZuulConstants.SECURITY_PASS_ROLE);
                result.add(attribute);
                return result;
            }

            ResponseWrapper<List<String>> roles = roleService.roleByUri(StringUtils.lowerCase(method), url);
            if (Objects.nonNull(roles)) {
                List<String> data = roles.getData();
                if (Objects.nonNull(data)) {
                    String dataJsonStr = JsonUtils.writeValueAsString(objectMapper, data);
                    LogUtils.debug(LOGGER, "Uri [{}] matched roles [{}]", url, dataJsonStr);
                    data.forEach(role -> {
                        ConfigAttribute conf = new SecurityConfig(role);
                        result.add(conf);
                    });
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return result;
    }

    private boolean isIntercept(String url) {
        if (StringUtils.isNotBlank(intercept)) {
            for (String path : intercept.split(",")) {
                if (matcher.match(path.replaceAll(" ", ""), url)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
