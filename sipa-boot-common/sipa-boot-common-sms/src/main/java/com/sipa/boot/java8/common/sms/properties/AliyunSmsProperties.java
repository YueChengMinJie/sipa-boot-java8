package com.sipa.boot.java8.common.sms.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sunyukun
 * @date 2019/4/4
 */
@Component
public class AliyunSmsProperties {
    @Value("${sipa.boot.aliyun.message.regionId:cn-hangzhou}")
    private String regionId;

    @Value("${sipa.boot.aliyun.message.accessKey:}")
    private String accessKey;

    @Value("${sipa.boot.aliyun.message.secretKey:}")
    private String secretKey;

    @Value("${sipa.boot.aliyun.message.templateCode:}")
    private String templateCode;

    @Value("${sipa.boot.aliyun.message.signName:}")
    private String signName;

    @Value("${sipa.boot.aliyun.message.version:0}")
    private Integer version;

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AliyunSmsProperties{" + "regionId='" + regionId + '\'' + ", accessKey='" + accessKey + '\''
            + ", secretKey='" + secretKey + '\'' + ", templateCode='" + templateCode + '\'' + ", signName='" + signName
            + '\'' + ", version=" + version + '}';
    }
}
