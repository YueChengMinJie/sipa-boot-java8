package com.sipa.boot.java8.data.mysql;

import java.lang.reflect.Method;
import java.util.Map;

import javax.sql.DataSource;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author zhouxiajie
 * @date 2021/3/22
 */
public class SipaBootDynamicRoutingDataSource extends DynamicRoutingDataSource {
    private static final Log LOGGER = LogFactory.get(SipaBootDynamicRoutingDataSource.class);

    @Override
    public void destroy() throws Exception {
        LOGGER.info("dynamic-datasource start closing.");
        for (Map.Entry<String, DataSource> item : getCurrentDataSources().entrySet()) {
            DataSource dataSource = item.getValue();
            Class<? extends DataSource> clazz = dataSource.getClass();
            try {
                Method closeMethod = clazz.getMethod("close");
                closeMethod.invoke(dataSource);
            } catch (NoSuchMethodException e) {
                LOGGER.warn("dynamic-datasource close the datasource named [{}] failed,", item.getKey());
            }
        }
        LOGGER.info("dynamic-datasource all closed success, bye!");
    }
}
