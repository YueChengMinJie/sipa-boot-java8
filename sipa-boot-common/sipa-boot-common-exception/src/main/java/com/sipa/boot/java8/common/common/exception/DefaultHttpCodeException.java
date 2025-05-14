package com.sipa.boot.java8.common.common.exception;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import com.sipa.boot.java8.common.exceptions.ApplicationException;

/**
 * @author caszhou
 * @date 2021/10/29
 */
public class DefaultHttpCodeException extends ApplicationException {
    private static final Map<HttpStatus, Integer> DEFAULT_ERROR_MAPPING = new EnumMap<>(HttpStatus.class);

    static {
        DEFAULT_ERROR_MAPPING.put(HttpStatus.BAD_REQUEST, 10001);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.NOT_FOUND, 10002);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.CONFLICT, 10003);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.FORBIDDEN, 10004);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.SERVICE_UNAVAILABLE, 10005);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.UNAUTHORIZED, 10006);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.BAD_GATEWAY, 10009);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.INTERNAL_SERVER_ERROR, 10010);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.TOO_MANY_REQUESTS, 10013);

        DEFAULT_ERROR_MAPPING.put(HttpStatus.UNSUPPORTED_MEDIA_TYPE, 20004);
        DEFAULT_ERROR_MAPPING.put(HttpStatus.METHOD_NOT_ALLOWED, 20003);
    }

    public DefaultHttpCodeException(HttpStatus httpStatus, String message, LocalDateTime timestamp) {
        this(httpStatus.value(), resolveNumericErrorCode(httpStatus), resolveErrorCode(httpStatus), message, timestamp);
    }

    public DefaultHttpCodeException(int status, int numericErrCode, String errCode, String message,
        LocalDateTime timestamp) {
        super(status, numericErrCode, errCode, message);
        setTimestamp(timestamp);
    }

    private static String resolveErrorCode(HttpStatus httpStatus) {
        return "errors.com.sipa.boot." + StringUtils.lowerCase(httpStatus.name());
    }

    private static int resolveNumericErrorCode(HttpStatus httpStatus) {
        if (DEFAULT_ERROR_MAPPING.containsKey(httpStatus)) {
            return DEFAULT_ERROR_MAPPING.get(httpStatus);
        } else {
            return httpStatus.value();
        }
    }
}
