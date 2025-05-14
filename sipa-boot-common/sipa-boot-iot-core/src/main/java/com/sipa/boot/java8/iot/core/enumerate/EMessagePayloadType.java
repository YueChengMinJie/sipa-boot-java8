package com.sipa.boot.java8.iot.core.enumerate;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public enum EMessagePayloadType {
    JSON, STRING, BINARY, HEX, UNKNOWN;

    public static EMessagePayloadType of(String of) {
        for (EMessagePayloadType value : EMessagePayloadType.values()) {
            if (value.name().equalsIgnoreCase(of)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
