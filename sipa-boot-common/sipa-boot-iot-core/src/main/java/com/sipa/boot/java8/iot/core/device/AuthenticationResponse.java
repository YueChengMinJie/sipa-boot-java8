package com.sipa.boot.java8.iot.core.device;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class AuthenticationResponse {
    private boolean success;

    private int code;

    private String message;

    private String deviceId;

    public static AuthenticationResponse success() {
        return success(null);
    }

    public static AuthenticationResponse success(String deviceId) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.success = true;
        response.code = 200;
        response.message = "授权通过";
        response.deviceId = deviceId;
        return response;
    }

    public static AuthenticationResponse error(int code, String message) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.success = false;
        response.code = code;
        response.message = message;
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("success", success)
            .append("code", code)
            .append("message", message)
            .append("deviceId", deviceId)
            .toString();
    }
}
