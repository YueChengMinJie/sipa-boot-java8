package com.sipa.boot.java8.common.dtos;

import java.io.Serializable;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.enums.EResCode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhouxiajie
 * @date 2018/3/1
 */
@ApiModel(description = "响应统一返回格式")
public class ResponseWrapper<T> implements Serializable {
    private static final long serialVersionUID = 4031700508559143217L;

    @ApiModelProperty(value = "响应对象")
    private T data;

    @ApiModelProperty(value = "是否成功", example = "true")
    private boolean success;

    @ApiModelProperty(value = "错误信息", example = "网络错误", hidden = true)
    private String message;

    @ApiModelProperty(value = "响应状态", example = "200")
    private int status;

    @ApiModelProperty(value = "响应编码", example = "GLOBAL", hidden = true)
    private EResCode resCode;

    @ApiModelProperty(value = "响应时间戳", example = "1618062176442")
    private Long timestamp;

    @ApiModelProperty(value = "响应key")
    private String key;

    private ResponseWrapper() {
        this.timestamp = System.currentTimeMillis();
    }

    public static ResponseWrapper<?> success() {
        ResponseWrapper<?> res = new ResponseWrapper<>();
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    public static <T> ResponseWrapper<T> successWithType() {
        ResponseWrapper<T> res = new ResponseWrapper<>();
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    public static <T> ResponseWrapper<T> successOf(T data) {
        return successOf(data, null);
    }

    public static <T> ResponseWrapper<T> successOf(T data, String msg) {
        ResponseWrapper<T> res = new ResponseWrapper<>();
        res.setData(data);
        res.setMessage(msg);
        res.setSuccess(true);
        res.setStatus(200);
        return res;
    }

    public static <T> ResponseWrapper<T> successWithMsg(String msg) {
        return successOf(null, msg);
    }

    public static <T> ResponseWrapper<T> error(T data, String msg, EResCode code) {
        ResponseWrapper<T> res = new ResponseWrapper<>();
        res.setData(data);
        res.setSuccess(false);
        res.setMessage(msg);
        res.setResCode(code);
        res.setStatus(500);
        return res;
    }

    public static <T> ResponseWrapper<T> error(T data, String msg, EResCode code, int status) {
        ResponseWrapper<T> res = new ResponseWrapper<>();
        res.setData(data);
        res.setSuccess(false);
        res.setMessage(msg);
        res.setResCode(code);
        res.setStatus(status);
        return res;
    }

    public static <T> ResponseWrapper<T> errorOf(T data, String msg) {
        return error(data, msg, EResCode.APP);
    }

    public static <T> ResponseWrapper<T> errorOf(EResCode code) {
        return error(null, SipaBootCommonConstants.GLOBAL_MSG, code);
    }

    public static <T> ResponseWrapper<T> errorOf(String msg, EResCode code) {
        return error(null, msg, code);
    }

    public static <T> ResponseWrapper<T> errorOf(String msg, EResCode code, int status) {
        return error(null, msg, code, status);
    }

    public static <T> ResponseWrapper<T> errorOf(String msg) {
        return error(null, msg, EResCode.APP);
    }

    public static <T> ResponseWrapper<T> errorOf() {
        return error(null, null, EResCode.FALLBACK_GATEWAY_TIMEOUT);
    }

    @SuppressWarnings("unchecked")
    public Class<ResponseWrapper<T>> genericClass() {
        return (Class<ResponseWrapper<T>>)this.getClass();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
