package com.sipa.boot.java8.common.common.exception.advice;

import static com.sipa.boot.java8.common.log.property.CommonLoggingProperties.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import com.sipa.boot.java8.common.archs.error.ErrorEntity;
import com.sipa.boot.java8.common.common.exception.property.ExceptionHandlingProperties;
import com.sipa.boot.java8.common.exceptions.ApplicationException;
import com.sipa.boot.java8.common.utils.AppUtils;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public interface ExceptionAdviceTrait {
    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * check is Supported type
     *
     * @param error
     *            error
     * @return isSupported
     */
    boolean isSupported(Throwable error);

    /**
     * handle ex
     *
     * @param request
     *            request
     * @param response
     *            response
     * @param exception
     *            exception
     * @return error entity
     */
    Object handle(final HttpServletRequest request, final HttpServletResponse response, final Throwable exception);

    /**
     * Handler exception with default implementation
     *
     * @param request
     *            http servlet request
     * @param response
     *            http servlet response
     * @param entity
     *            response with error entity
     * @return response
     */
    default Object handle(final HttpServletRequest request, final HttpServletResponse response,
        final ResponseEntity<ErrorEntity> entity) {
        String acceptValue = request.getHeader(HttpHeaders.ACCEPT);

        // try to resolve error message
        setLocalMessage(entity);

        if (StringUtils.isNotBlank(acceptValue) && acceptValue.contains(MediaType.TEXT_HTML_VALUE)) {
            // set response http error status code
            response.setStatus(entity.getStatusCodeValue());

            // return error page response
            ModelAndView model = new ModelAndView("error");

            String path = (String)request.getAttribute("javax.servlet.error.request_uri");
            if (path != null) {
                model.addObject("path", path);
            }

            model.addObject("status", entity.getStatusCodeValue());
            model.addObject("error", entity.getBody());
            model.addObject("message", Objects.requireNonNull(entity.getBody()).getErrorMessage());
            model.addObject("timestamp", entity.getBody().getTimestamp());
            // current not supported error stack in error page ...
            return model;
        } else {
            // return error entity response
            return entity;
        }
    }

    /**
     * log for ex
     *
     * @param level
     *            log level for exception
     * @param exception
     *            exception to be handle
     */
    default void log(final Level level, final Exception exception) {
        Optional.ofNullable(exception).map(e -> {
            if (Level.SEVERE == level) {
                LOGGER.error("Met error exception", e);
            } else if (Level.WARNING == level || Level.FINEST == level) {
                // treat FINEST as warn for better exp tracing
                LOGGER.warn("Met warning exception [{}]", getExceptionHandlingProperties().isPrintWarnExceptionStack()
                    ? ExceptionUtils.getStackTrace(e) : ExceptionUtils.getRootCauseMessage(e));
            } else if (Level.INFO == level) {
                LOGGER.info("Met info exception", e);
            } else {
                LOGGER.debug("Met debug exception", e);
            }
            return e;
        });
    }

    /**
     * log self-defined ex with format
     *
     * @param level
     *            log level for exception
     * @param exception
     *            exception to be handle
     */
    default void log(final Level level, final ApplicationException exception) {
        Optional.ofNullable(exception).map(e -> {
            List<String> l = resolve(e);
            if (Level.SEVERE == level) {
                LOGGER.error(APPLICATION_LOG_FORMAT, l.get(0), l.get(1), l.get(2), l.get(3), e);
            } else if (Level.WARNING == level || Level.FINEST == level) {
                // treat FINEST as warn for better exp tracing
                LOGGER.warn(APPLICATION_LOG_FORMAT_WITH_STACK, l.get(0), l.get(1), l.get(2), l.get(3),
                    getExceptionHandlingProperties().isPrintWarnExceptionStack() ? ExceptionUtils.getStackTrace(e)
                        : ExceptionUtils.getRootCauseMessage(e));
            } else if (Level.INFO == level) {
                LOGGER.info(APPLICATION_LOG_FORMAT, l.get(0), l.get(1), l.get(2), l.get(3), e);
            } else {
                LOGGER.debug(APPLICATION_LOG_FORMAT, l.get(0), l.get(1), l.get(2), l.get(3), e);
            }
            return e;
        });
    }

    /**
     * resolve ApplicationException
     *
     * @param e
     *            ApplicationException
     * @return infoList
     */
    default List<String> resolve(ApplicationException e) {
        List<String> infoList = new ArrayList<>(4);

        infoList.add(StringUtils.defaultIfBlank(String.valueOf(e.getNumericErrorCode()), PLACE_HOLDER));
        if (ArrayUtils.isNotEmpty(e.getStackTrace())) {
            StackTraceElement ste = e.getStackTrace()[0];
            String[] nameArray = ste.getClassName().split("\\.");
            infoList.add(nameArray[nameArray.length - 1]);
            infoList.add(ste.getMethodName());
        } else {
            infoList.add(PLACE_HOLDER);
            infoList.add(PLACE_HOLDER);
        }
        infoList.add(StringUtils.defaultIfBlank(e.getMessage(), PLACE_HOLDER));

        return infoList;
    }

    /**
     * util method to get ExceptionHandlingProperties
     *
     * @return ExceptionHandlingProperties
     */
    default ExceptionHandlingProperties getExceptionHandlingProperties() {
        return AppUtils.getBean(ExceptionHandlingProperties.class);
    }

    /**
     * resolve error message by error code.
     *
     * @param entity
     *            error entity
     */
    default void setLocalMessage(ResponseEntity<ErrorEntity> entity) {
        try {
            MessageSource messageSource = AppUtils.getBean("messageSource", MessageSource.class);
            String localMessage = messageSource.getMessage(Objects.requireNonNull(entity.getBody()).getErrorCode(),
                null, LocaleContextHolder.getLocale());
            entity.getBody().setErrorMessage(localMessage);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cannot find messageSource or i18n message.", e);
            }
        }
    }
}
