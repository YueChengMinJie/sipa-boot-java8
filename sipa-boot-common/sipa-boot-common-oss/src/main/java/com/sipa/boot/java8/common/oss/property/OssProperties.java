package com.sipa.boot.java8.common.oss.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sunyukun
 * @date 2019/3/19
 */
@Component
public class OssProperties {
    @Value("${sipa.boot.oss.endpoint:http://oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    @Value("${sipa.boot.oss.accessKey:}")
    private String accessKey;

    @Value("${sipa.boot.oss.secretKey:}")
    private String secretKey;

    @Value("${sipa.boot.oss.historyBucketName:sipa-boot-history-data-test}")
    private String historyBucketName;

    @Value("${sipa.boot.oss.tempBucketName:sipa-boot-temp}")
    private String tempBucketName;

    @Value("${sipa.boot.oss.platformBucketName:sipa-boot-platform-test}")
    private String platformBucketName;

    @Value("${sipa.boot.oss.saasBucketName:sipa-boot-saas-test}")
    private String saasBucketName;

    @Value("${sipa.boot.oss.configBucketName:sipa-boot-config-local}")
    private String configBucketName;

    @Value("${sipa.boot.oss.terraceBucketName:sipa-boot-terrace-test}")
    private String terraceBucketName;

    @Value("${sipa.boot.oss.publicTempBucketName:public-sipa-boot-temp}")
    private String publicTempBucketName;

    @Value("${sipa.boot.oss.exportBucketName:sipa-boot-export-test}")
    private String exportBucketName;

    @Value("${sipa.boot.oss.version:0}")
    private int version;

    @Value("${sipa.boot.oss.strategy:aliyun}")
    private String strategyType;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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

    public String getHistoryBucketName() {
        return historyBucketName;
    }

    public void setHistoryBucketName(String historyBucketName) {
        this.historyBucketName = historyBucketName;
    }

    public String getTempBucketName() {
        return tempBucketName;
    }

    public void setTempBucketName(String tempBucketName) {
        this.tempBucketName = tempBucketName;
    }

    public String getPlatformBucketName() {
        return platformBucketName;
    }

    public void setPlatformBucketName(String platformBucketName) {
        this.platformBucketName = platformBucketName;
    }

    public String getSaasBucketName() {
        return saasBucketName;
    }

    public void setSaasBucketName(String saasBucketName) {
        this.saasBucketName = saasBucketName;
    }

    public String getConfigBucketName() {
        return configBucketName;
    }

    public void setConfigBucketName(String configBucketName) {
        this.configBucketName = configBucketName;
    }

    public String getTerraceBucketName() {
        return terraceBucketName;
    }

    public void setTerraceBucketName(String terraceBucketName) {
        this.terraceBucketName = terraceBucketName;
    }

    public String getPublicTempBucketName() {
        return publicTempBucketName;
    }

    public void setPublicTempBucketName(String publicTempBucketName) {
        this.publicTempBucketName = publicTempBucketName;
    }

    public String getExportBucketName() {
        return exportBucketName;
    }

    public void setExportBucketName(String exportBucketName) {
        this.exportBucketName = exportBucketName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }
}
