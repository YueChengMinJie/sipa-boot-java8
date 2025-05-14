package com.sipa.boot.java8.common.api.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.sipa.boot.java8.common.api.annotation.Encode;
import com.sipa.boot.java8.common.api.annotation.Encrypt;
import com.sipa.boot.java8.common.api.property.ApiProperty;
import com.sipa.boot.java8.common.api.utils.ApiUtil;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.utils.JsonUtils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口返回对象加密
 *
 * @author rstyro
 */
@Slf4j
@ControllerAdvice(basePackages = {"com"})
public class EncryptResponseAdvice implements ResponseBodyAdvice<Object> {
    private final ApiProperty apiProperty;

    public EncryptResponseAdvice(ApiProperty apiProperty) {
        this.apiProperty = apiProperty;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // return true 有效
        return true;
    }

    /**
     * 返回结果加密
     *
     * @param obj
     *            接口返回的对象
     * @param methodParameter
     *            method
     * @param mediaType
     *            mediaType
     * @param aClass
     *            HttpMessageConverter class
     * @param serverHttpRequest
     *            request
     * @param serverHttpResponse
     *            response
     * @return obj
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType mediaType,
        Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
        ServerHttpResponse serverHttpResponse) {
        // 方法或类上有注解
        if (ApiUtil.hasMethodAnnotation(methodParameter, new Class[] {Encrypt.class, Encode.class})) {
            // 这里假设已经定义好返回的model就是Result
            if (obj instanceof ResponseWrapper) {
                try {
                    ResponseWrapper<Object> wrapper = (ResponseWrapper<Object>)obj;
                    // 1、随机aes密钥
                    byte[] randomAesKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128).getEncoded();
                    // 2、数据体
                    Object data = wrapper.getData();
                    // 3、转json字符串
                    String jsonString = JsonUtils.writeValueAsString(data);
                    // 4、aes加密数据体
                    String encryptString = SecureUtil.aes(randomAesKey).encryptBase64(jsonString);
                    // 5、重新设置数据体
                    wrapper.setData(encryptString);
                    // 6、使用前端的rsa公钥加密 aes密钥 返回给前端
                    RSA rsa = new RSA(null, ApiUtil.cleanKey(apiProperty.getFrontRsaPublicKey()));
                    wrapper.setKey(rsa.encryptBase64(HexUtil.encodeHexStr(randomAesKey), KeyType.PublicKey));
                    // 7、返回
                    return obj;
                } catch (Exception e) {
                    log.error("加密失败", e);
                }
            }
        }
        return obj;
    }
}
