package com.sipa.boot.java8.data.iotdb.utils;

import java.util.Collections;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aliyun.hitsdb.client.value.request.Point;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.data.iotdb.convert.IotdbRecordConverter;
import com.sipa.boot.java8.data.iotdb.convert.impl.IotdbDataTypeConverterImpl;
import com.sipa.boot.java8.data.iotdb.convert.impl.IotdbDeviceIdConverterImpl;
import com.sipa.boot.java8.data.iotdb.property.IotdbOtherProperties;
import com.sipa.boot.java8.data.iotdb.property.IotdbProperties;
import com.sipa.boot.java8.data.iotdb.property.IotdbServerProperties;
import com.sipa.boot.java8.data.iotdb.sql.SelectBuilder;
import com.sipa.boot.java8.data.iotdb.util.IotdbUtils;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbUtilsIT {
    @Before
    public void init() {
        ioc();
    }

    // original
    @Test
    public void testSessionPoolQuery() throws IoTDBConnectionException, StatementExecutionException {
        SessionPool pool = new SessionPool("127.0.0.1", 6667, "root", "root", 1);
        SessionDataSetWrapper wrapper = pool.executeQueryStatement("select * from root.sg.d1");
        System.out.println(wrapper.getColumnNames());
        System.out.println(wrapper.getColumnTypes());
        while (wrapper.hasNext()) {
            System.out.println(wrapper.next());
        }
    }

    @Test
    public void testInsertRecord() {
        ioc();
        Point point = Point.metric("s1")
            .timestamp(System.currentTimeMillis())
            .value(5)
            .tag(Collections.singletonMap(SipaBootCommonConstants.TagName.COLLECTION_ID, "d1"))
            .build();
        IotdbUtils.insertRecord(point);
    }

    @Test
    public void testCompatibilityQuery() {
        Assert.assertTrue(IotdbUtils.compatibilityQuery(new SelectBuilder("root.sg.d1")).size() > 0);
    }

    @SuppressWarnings("all")
    private void ioc() {
        IotdbProperties properties = getIotdbProperties();
        new IotdbUtils(properties);
        new IotdbRecordConverter(new IotdbDeviceIdConverterImpl(properties), new IotdbDataTypeConverterImpl());
    }

    private IotdbProperties getIotdbProperties() {
        IotdbProperties properties = new IotdbProperties();
        IotdbServerProperties iotdbServerProperties = new IotdbServerProperties();
        iotdbServerProperties.setHost("127.0.0.1");
        iotdbServerProperties.setPort(6667);
        iotdbServerProperties.setUsername("root");
        iotdbServerProperties.setPassword("root");
        properties.setServers(Collections.singletonList(iotdbServerProperties));
        IotdbOtherProperties iotdbOtherProperties = new IotdbOtherProperties();
        iotdbOtherProperties.setStorageGroups(Collections.singletonList("root.sg"));
        properties.setOther(iotdbOtherProperties);
        return properties;
    }
}
