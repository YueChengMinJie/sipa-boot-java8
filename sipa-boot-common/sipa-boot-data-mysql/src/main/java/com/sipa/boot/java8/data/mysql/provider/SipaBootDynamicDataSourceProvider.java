package com.sipa.boot.java8.data.mysql.provider;

import java.util.Map;

import javax.sql.DataSource;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;

/**
 * @author zhouxiajie
 * @date 2019-06-08
 */
public class SipaBootDynamicDataSourceProvider implements DynamicDataSourceProvider {
    private final Map<String, DataSource> dataSourceMap;

    public SipaBootDynamicDataSourceProvider(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        return dataSourceMap;
    }
}
