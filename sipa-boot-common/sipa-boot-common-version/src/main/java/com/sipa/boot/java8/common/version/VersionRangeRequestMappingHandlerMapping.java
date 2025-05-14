package com.sipa.boot.java8.common.version;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.google.common.collect.Lists;

/**
 * @author songjianming
 * @date 2021/11/10
 */
public class VersionRangeRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private final String prefix;

    public VersionRangeRequestMappingHandlerMapping(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) {
            return null;
        }
        VersionRange methodAnnotation = AnnotationUtils.findAnnotation(method, VersionRange.class);
        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);
            info = createApiVersionInfo(methodAnnotation, methodCondition).combine(info);
        } else {
            VersionRange typeAnnotation = AnnotationUtils.findAnnotation(handlerType, VersionRange.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
                info = createApiVersionInfo(typeAnnotation, typeCondition).combine(info);
            }
        }

        return info;
    }

    private RequestMappingInfo createApiVersionInfo(VersionRange annotation, RequestCondition<?> customCondition) {
        String[] values = annotation.value();
        String[] patterns;
        List<String> list = Lists.newArrayList();

        if (values.length == 1) {
            patterns = new String[2];
            patterns[0] = prefix + new BigDecimal(values[0]).floatValue();
            patterns[1] = prefix + new BigDecimal(values[0]).intValue();
        } else if (values.length == 2) {
            BigDecimal bigDecimal = new BigDecimal(values[0]);
            BigDecimal bigDecimal1 = new BigDecimal(values[1]);
            int minVersionNum = bigDecimal.multiply(BigDecimal.valueOf(10)).intValue();
            int maxVersionNum = bigDecimal1.multiply(BigDecimal.valueOf(10)).intValue();
            patterns = new String[maxVersionNum - minVersionNum + 1];
            for (int i = 0; i < maxVersionNum - minVersionNum + 1; i++) {
                patterns[i] = prefix + (bigDecimal.add(new BigDecimal("0.1").multiply(new BigDecimal(i))).floatValue());
            }
        } else {
            Arrays.stream(values).forEach(v -> list.add(prefix + new BigDecimal(v).floatValue()));
            patterns = list.toArray(new String[0]);
        }
        return new RequestMappingInfo(
            new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), useSuffixPatternMatch(),
                useTrailingSlashMatch(), getFileExtensions()),
            new RequestMethodsRequestCondition(), new ParamsRequestCondition(), new HeadersRequestCondition(),
            new ConsumesRequestCondition(), new ProducesRequestCondition(), customCondition);
    }
}
