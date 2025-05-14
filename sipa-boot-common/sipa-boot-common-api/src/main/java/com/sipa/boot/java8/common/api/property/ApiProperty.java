package com.sipa.boot.java8.common.api.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 密钥配置
 *
 * @author rstyro
 */
@Data
@ConfigurationProperties(prefix = "sipa.boot.api")
public class ApiProperty {
    /**
     * rsa 公钥
     */
    private String rsaPublicKey;

    /**
     * rsa 私钥
     */
    private String rsaPrivateKey;

    /**
     * 前端rsa 公钥
     */
    private String frontRsaPublicKey;
}
