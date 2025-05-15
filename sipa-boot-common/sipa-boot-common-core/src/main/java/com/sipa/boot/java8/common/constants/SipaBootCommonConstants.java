package com.sipa.boot.java8.common.constants;

import java.time.format.DateTimeFormatter;

/**
 * @author zhouxiajie
 * @date 2019-02-01
 */
public class SipaBootCommonConstants {
    public static final String GLOBAL_MSG = "网络错误";

    public static final String COMMA = ",";

    public static final String WELL = "#";

    public static final String COLON = ":";

    public static final String ACROSS = "-";

    public static final String UNDERLINE = "_";

    public static final String UPRIGHT = "|";

    public static final String POINT = ".";

    public static final String ASTERISK = "*";

    public static final String LEFT_BRACKET = "(";

    public static final String RIGHT_BRACKET = ")";

    public static final String LEFT_CURLY_BRACES = "{";

    public static final String RIGHT_CURLY_BRACES = "}";

    public static final String EMPTY = " ";

    public static final String SLASH = "/";

    public static final String BLANK = "";

    public static final String DOUBLE_ASTERISK = "**";

    public static final String SIPA_BOOT_USER_ID_HEADER = "X-User-Id";

    public static final String SIPA_BOOT_TENANT_ID_HEADER = "X-Tenant-Id";

    public static final String SIPA_BOOT_SCOPE_HEADER = "X-Scope";

    public static final String SIPA_BOOT_AUTHORITIES_HEADER = "X-Authorities";

    public static final String SIPA_BOOT_API_KEY = "X-Api-Key";

    public static final String SIPA_BOOT_USER_ID_KEY = "user_id";

    public static final String SIPA_BOOT_TENANT_ID_KEY = "tenant_id";

    public static final String SIPA_BOOT_SEQUENCE_KEY = "sequence";

    public static final String SIPA_BOOT_SCOPE_KEY = "scope";

    public static final String SIPA_BOOT_CLIENT_ID_KEY = "jti";

    /**
     * 默认超级管理员角色名
     */
    public static final String SIPA_BOOT_SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    /**
     * 默认租户id
     */
    public static final String DEFAULT_TENANT_ID = "1";

    public static final String GATEWAY = "Gateway";

    private SipaBootCommonConstants() {
        // noop
    }

    public interface Symbol {
        String PERCENTAGE = "%";

        String UNDERLINE = "_";

        String LINE = "-";
    }

    public interface StringValue {
        String STRING_N_VALUE_1 = "-1";

        String STRING_VALUE_0 = "0";

        String STRING_VALUE_1 = "1";

        String STRING_VALUE_2 = "2";

        String STRING_VALUE_3 = "3";

        String STRING_VALUE_4 = "4";

        String STRING_VALUE_5 = "5";

        String STRING_VALUE_6 = "6";

        String STRING_VALUE_7 = "7";

        String STRING_VALUE_8 = "8";
    }

    public interface StringDoubleValue {
        String STRING_VALUE_1 = "01";

        String STRING_VALUE_2 = "02";

        String STRING_VALUE_3 = "03";

        String STRING_VALUE_4 = "04";

        String STRING_VALUE_5 = "05";

        String STRING_VALUE_6 = "06";

        String STRING_VALUE_7 = "07";

        String STRING_VALUE_8 = "08";
    }

    public interface Number {
        int N_INT_1 = -1;

        int INT_0 = 0;

        int INT_1 = 1;

        int INT_2 = 2;

        int INT_3 = 3;

        int INT_4 = 4;

        int INT_5 = 5;

        int INT_6 = 6;

        int INT_7 = 7;

        int INT_8 = 8;

        int INT_9 = 9;

        int INT_10 = 10;

        int INT_13 = 13;

        int INT_14 = 14;

        int INT_15 = 15;

        int INT_16 = 16;

        int INT_20 = 20;

        int INT_30 = 30;

        int INT_64 = 64;
    }

    public interface HBase {
        String TABLE_NAME = "message";

        String COLUMN_SCOPE = "scope";

        String FAMILY_F = "f";

        String MIN_COMMAND = "00";

        String MAX_COMMAND = "FF";
    }

    public interface DataParser {
        String RIGHT_HEADER = "##";
    }

    public interface LongValue {
        long LONG_0 = 0L;

        Long SEVEN_DAYS_SEC = 60 * 60 * 24 * 7L;
    }

    public interface RedisKey {
        String LOGIN_STATE = "loginState";

        String LATITUDE = "latitude";

        String LONGITUDE = "longitude";
    }

    public interface TimeFormatKey {
        String DEFAULT = "yyyy-MM-dd HH:mm:ss";

        String DEFAULT_WITH_POINT_MILS = "yyyy-MM-dd HH:mm:ss.SSS";

        String DEFAULT_WITH_MILS = "yyyy-MM-dd HH:mm:ss:SSS";

        String DEFAULT_WITH_UL = "yyyy-MM-dd_HH:mm:ss";

        String DEFAULT_WITH_NONE = "yyyyMMddHHmmss";

        String DATE = "yyyy-MM-dd";

        String HOUR = "yyyy-MM-dd HH";

        String MINUTE = "yyyy-MM-dd HH:mm";

        String SECOND = "yyyy-MM-dd HH:mm";
    }

    public interface LocalDateTimeFormatter {
        DateTimeFormatter DEFAULT = DateTimeFormatter.ofPattern(TimeFormatKey.DEFAULT);
    }

    /**
     * Http status code constants
     */
    public interface Http {
        int OK = 200;

        int CREATED = 201;

        int ACCEPTED = 202;

        int NO_CONTENT = 204;

        int RESET_CONTENT = 205;

        int PARTIAL_CONTENT = 206;

        int MULTI_STATUS = 207;

        int MOVED_PERMANENTLY = 301;

        int FOUND = 302;

        int SEE_OTHER = 303;

        int NOT_MODIFIED = 304;

        int USE_PROXY = 305;

        int TEMPORARY_REDIRECT = 307;

        int BAD_REQUEST = 400;

        int UNAUTHORIZED = 401;

        int PAYMENT_REQUIRED = 402;

        int FORBIDDEN = 403;

        int NOT_FOUND = 404;

        int METHOD_NOT_ALLOWED = 405;

        int NOT_ACCEPTABLE = 406;

        int PROXY_AUTHENTICATION_REQUIRED = 407;

        int REQUEST_TIMEOUT = 408;

        int CONFLICT = 409;

        int GONE = 410;

        int LENGTH_REQUIRED = 411;

        int PRECONDITION_FAILED = 412;

        int REQUEST_ENTITY_TOO_LARGE = 413;

        int REQUEST_URI_TOO_LONG = 414;

        int UNSUPPORTED_MEDIA_TYPE = 415;

        int REQUESTED_RANGE_NOT_SATISFIABLE = 416;

        int EXPECTATION_FAILED = 417;

        int INTERNAL_SERVER_ERROR = 500;

        int NOT_IMPLEMENTED = 501;

        int BAD_GATEWAY = 502;

        int SERVICE_UNAVAILABLE = 503;

        int GATEWAY_TIMEOUT = 504;

        int VERSION_NOT_SUPPORTED = 505;
    }

    public abstract static class MediaType {
        String APPLICATION_JSON_DEFAULT = "application/json";
    }

    public interface Canary {
        String COOKIE = "Sipa-Boot-Canary";

        String ALWAYS = "always";

        String METADATA = "canary";
    }

    public interface TagName {
        String COLLECTION_ID = "collection_id";
    }
}
