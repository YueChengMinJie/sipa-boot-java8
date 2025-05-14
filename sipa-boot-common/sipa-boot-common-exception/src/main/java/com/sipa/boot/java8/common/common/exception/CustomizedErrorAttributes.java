package com.sipa.boot.java8.common.common.exception;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.advice.ExceptionAdviceTrait;

/**
 * @author caszhou
 * @date 2021/9/28
 */
@Component
public class CustomizedErrorAttributes extends DefaultErrorAttributes implements ErrorAttributes {
    private static final Logger LOG = LoggerFactory.getLogger(CustomizedErrorAttributes.class);

    private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";

    private DefaultWebExceptionHandling defaultWebExceptionHandling = null;

    private List<ExceptionAdviceTrait> exceptionAdviceTraits = null;

    @Autowired
    public void setDefaultWebExceptionHandling(DefaultWebExceptionHandling defaultWebExceptionHandling) {
        this.defaultWebExceptionHandling = defaultWebExceptionHandling;
    }

    @Autowired(required = false)
    public void setExceptionAdviceTraits(List<ExceptionAdviceTrait> exceptionAdviceTraits) {
        if (exceptionAdviceTraits != null) {
            this.exceptionAdviceTraits = exceptionAdviceTraits;
            this.exceptionAdviceTraits.sort(new Comparator<ExceptionAdviceTrait>() {
                @Override
                public int compare(ExceptionAdviceTrait o1, ExceptionAdviceTrait o2) {
                    return order(o1) - order(o2);
                }

                private int order(ExceptionAdviceTrait trait) {
                    Order order = AnnotationUtils.findAnnotation(trait.getClass(), Order.class);

                    if (order == null) {
                        return Ordered.LOWEST_PRECEDENCE;
                    } else {
                        return order.value();
                    }
                }
            });
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {
        storeErrorAttributes(request, ex);
        return null;
    }

    private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
        request.setAttribute(ERROR_ATTRIBUTE, ex);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, boolean includeStackTrace) {
        Throwable throwable = getError(requestAttributes);
        if (throwable == null || !(requestAttributes instanceof ServletRequestAttributes)
            || exceptionAdviceTraits == null || exceptionAdviceTraits.isEmpty()) {
            Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
            // convert to error entity
            int statusCode = (int)defaultErrorAttributes.get("status");
            String message = (String)defaultErrorAttributes.get("message");
            if (statusCode == 999) {
                // '999' and 'none' was hard code in super.getErrorAttributes
                defaultErrorAttributes.put("error",
                    new ErrorEntity(new DefaultHttpCodeException(999, 999, "none", message, LocalDateTime.now())));
            } else {
                HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
                defaultErrorAttributes.put("error",
                    new ErrorEntity(new DefaultHttpCodeException(httpStatus, message, LocalDateTime.now())));
            }

            return defaultErrorAttributes;
        } else {
            Map<String, Object> errorAttributes = new LinkedHashMap<>();
            Object errorResponse = toErrorResponse(((ServletRequestAttributes)requestAttributes).getRequest(),
                ((ServletRequestAttributes)requestAttributes).getResponse(), throwable);
            if (errorResponse instanceof ResponseEntity) {
                ResponseEntity<ErrorEntity> errorEntityResponse = (ResponseEntity<ErrorEntity>)errorResponse;
                String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
                if (path != null) {
                    errorAttributes.put("path", path);
                }
                errorAttributes.put("status", errorEntityResponse.getStatusCodeValue());
                errorAttributes.put("error", errorEntityResponse.getBody());
                errorAttributes.put("message", Objects.requireNonNull(errorEntityResponse.getBody()).getErrorMessage());
                errorAttributes.put("timestamp", errorEntityResponse.getBody().getTimestamp());
                // current not supported error stack in error page ...
            } else {
                errorAttributes.putAll(((ModelAndView)errorResponse).getModel());
            }

            return errorAttributes;
        }
    }

    private Object toErrorResponse(final HttpServletRequest request, final HttpServletResponse response,
        final Throwable error) {
        for (ExceptionAdviceTrait exceptionAdviceTrait : exceptionAdviceTraits) {
            if (exceptionAdviceTrait.isSupported(error)) {
                Class clazz = error.getClass();
                Method method = null;
                while (method == null && clazz != null && clazz != clazz.getSuperclass()) {
                    method = ReflectionUtils.findMethod(exceptionAdviceTrait.getClass(), "handle",
                        HttpServletRequest.class, HttpServletResponse.class, clazz);

                    clazz = clazz.getSuperclass();
                }

                if (method == null) {
                    LOG.warn("Can't find error handle for concrete error type [{}] in class [{}]",
                        Objects.requireNonNull(error.getClass()).getCanonicalName(),
                        exceptionAdviceTrait.getClass().getCanonicalName());
                } else {
                    return ReflectionUtils.invokeMethod(method, exceptionAdviceTrait, request, response, error);
                }
            }
        }

        return defaultWebExceptionHandling.handle(request, response, error);
    }

    @Override
    public Throwable getError(WebRequest requestAttributes) {
        Throwable exception = getAttribute(requestAttributes, ERROR_ATTRIBUTE);
        if (exception == null) {
            exception = getAttribute(requestAttributes, "javax.servlet.error.exception");
        }
        return exception;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T)requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
}
