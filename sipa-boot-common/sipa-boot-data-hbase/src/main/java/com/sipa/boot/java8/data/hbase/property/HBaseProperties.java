package com.sipa.boot.java8.data.hbase.property;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2019-07-12
 */
@Component
public class HBaseProperties {
    @Value("${sipa.boot.hbase.quorum:cdh2.ycmj.com,cdh3.ycmj.com,cdh4.ycmj.com}")
    private String quorum;

    @Value("${sipa.boot.hbase.clientPort:2181}")
    private Integer clientPort;

    @Value("${sipa.boot.hbase.maxSize:2}")
    private Integer maxSize;

    @Value("${sipa.boot.hbase.version:20210415}")
    private Integer version;

    @Value("#{${sipa.boot.hbase.tableNameMap:null}}")
    private Map<String, String> tableNameMap;

    public String getQuorum() {
        return quorum;
    }

    public void setQuorum(String quorum) {
        this.quorum = quorum;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Map<String, String> getTableNameMap() {
        return tableNameMap;
    }

    public void setTableNameMap(Map<String, String> tableNameMap) {
        this.tableNameMap = tableNameMap;
    }
}
