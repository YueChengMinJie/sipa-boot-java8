package com.sipa.boot.java8.iot.core.enumerate;

import java.util.Optional;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public enum EErrorCode {
    /* 设备消息相关 */
    REQUEST_HANDLING("error.code.request_handling"),

    CLIENT_OFFLINE("error.code.client_offline"),

    CONNECTION_LOST("error.code.connection_lost"),

    NO_REPLY("error.code.no_reply"),

    TIME_OUT("error.code.time_out"),

    SYSTEM_ERROR("error.code.system_error"),

    UNSUPPORTED_MESSAGE("error.code.unsupported_message"),

    PARAMETER_ERROR("error.code.parameter_error"),

    PARAMETER_UNDEFINED("error.code.parameter_undefined"),

    FUNCTION_UNDEFINED("error.code.function_undefined"),

    PROPERTY_UNDEFINED("error.code.property_undefined"),

    UNKNOWN_PARENT_DEVICE("error.code.unknown_parent_device"),

    CYCLIC_DEPENDENCE("error.code.cyclic_dependence"),

    SERVER_NOT_AVAILABLE("error.code.server_not_available"),

    UNKNOWN("error.code.unknown");

    private final String text;

    EErrorCode(String text) {
        this.text = text;
    }

    public static Optional<EErrorCode> of(String code) {
        if (code == null) {
            return Optional.empty();
        }
        for (EErrorCode value : values()) {
            if (value.name().equalsIgnoreCase(code)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public String getText() {
        return text;
    }
}
