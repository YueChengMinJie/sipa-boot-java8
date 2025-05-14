package com.sipa.boot.java8.common.dtos;

import java.io.Serializable;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.enums.EResCode;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author zhouxiajie
 * @date 2018/3/1
 */
@Schema(description = "响应统一返回格式")
public class ResponseWrapperV3<T> implements Serializable {
    private static final long serialVersionUID = 4031700508559143217L;

    @Schema(description = "响应对象")
    private T data;

    @Schema(description = "是否成功", example = "true")
    private boolean success;

    @Schema(description = "错误信息", example = "网络错误", hidden = true)
    private String message;

    @Schema(description = "响应状态", example = "200")
    private int status;

    @Schema(description = "响应编码", example = "GLOBAL", hidden = true)
    private EResCode resCode;

    @Schema(description = "响应时间戳", example = "1618062176442")
    private Long timestamp;

    private ResponseWrapperV3() {
        this.timestamp = System.currentTimeMillis();
    }

    public static ResponseWrapperV3<?> success() {
        ResponseWrapperV3<?> res = new ResponseWrapperV3<>();
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    public static <T> ResponseWrapperV3<T> successWithType() {
        ResponseWrapperV3<T> res = new ResponseWrapperV3<>();
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    public static <T> ResponseWrapperV3<T> successOf(T data) {
        return successOf(data, null);
    }

    public static <T> ResponseWrapperV3<T> successOf(T data, String msg) {
        ResponseWrapperV3<T> res = new ResponseWrapperV3<>();
        res.setData(data);
        res.setMessage(msg);
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    public static <T> ResponseWrapperV3<T> successWithMsg(String msg) {
        return successOf(null, msg);
    }

    public static <T> ResponseWrapperV3<T> error(T data, String msg, EResCode code) {
        ResponseWrapperV3<T> res = new ResponseWrapperV3<>();
        res.setData(data);
        res.setSuccess(false);
        res.setMessage(msg);
        res.setResCode(code);
        res.setStatus(500);
        return res;
    }

    public static <T> ResponseWrapperV3<T> error(T data, String msg, EResCode code, int status) {
        ResponseWrapperV3<T> res = new ResponseWrapperV3<>();
        res.setData(data);
        res.setSuccess(false);
        res.setMessage(msg);
        res.setResCode(code);
        res.setStatus(status);
        return res;
    }

    public static <T> ResponseWrapperV3<T> errorOf(T data, String msg) {
        return error(data, msg, EResCode.APP);
    }

    public static <T> ResponseWrapperV3<T> errorOf(EResCode code) {
        return error(null, SipaBootCommonConstants.GLOBAL_MSG, code);
    }

    public static <T> ResponseWrapperV3<T> errorOf(String msg, EResCode code) {
        return error(null, msg, code);
    }

    public static <T> ResponseWrapperV3<T> errorOf(String msg, EResCode code, int status) {
        return error(null, msg, code, status);
    }

    public static <T> ResponseWrapperV3<T> errorOf(String msg) {
        return error(null, msg, EResCode.APP);
    }

    public static <T> ResponseWrapperV3<T> errorOf() {
        return error(null, null, EResCode.FALLBACK_GATEWAY_TIMEOUT);
    }

    @SuppressWarnings("unchecked")
    public Class<ResponseWrapperV3<T>> genericClass() {
        return (Class<ResponseWrapperV3<T>>)this.getClass();
    }

    public T getData() {
        return data;
    }

    private void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    private void setSuccess(boolean success) {
        this.success = success;
    }

    public EResCode getResCode() {
        return resCode;
    }

    public void setResCode(EResCode resCode) {
        this.resCode = resCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
