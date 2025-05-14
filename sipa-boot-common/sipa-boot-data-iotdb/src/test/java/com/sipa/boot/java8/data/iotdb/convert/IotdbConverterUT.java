package com.sipa.boot.java8.data.iotdb.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.junit.Test;

import com.aliyun.hitsdb.client.value.request.Point;
import com.sipa.boot.java8.data.iotdb.convert.impl.IotdbDataTypeConverterImpl;
import com.sipa.boot.java8.data.iotdb.convert.impl.IotdbDeviceIdConverterImpl;
import com.sipa.boot.java8.data.iotdb.property.IotdbProperties;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbConverterUT {
    @Test
    public void testDeviceIdConverter() {
        try {
            IotdbRecordConverter.getDeviceId(null, new IotdbDeviceIdConverterImpl(new IotdbProperties()));
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getClass()).isEqualTo(IllegalArgumentException.class);
        }
    }

    @Test
    public void testDataTypeConverter() {
        try {
            IotdbRecordConverter.getDataType(
                Point.metric("test").timestamp(System.currentTimeMillis()).value(BigDecimal.valueOf(1)).build(),
                new IotdbDataTypeConverterImpl());
        } catch (Exception e) {
            e.printStackTrace();
            assertThat(e.getClass()).isEqualTo(IllegalArgumentException.class);
        }
    }

    @Test
    public void testDataTypeString() {
        assertThat(IotdbRecordConverter.getDataType(
            Point.metric("test").timestamp(System.currentTimeMillis()).value("123").build(),
            new IotdbDataTypeConverterImpl())).isEqualTo(TSDataType.TEXT);
    }

    @Test
    public void testDataTypeInt32() {
        assertThat(IotdbRecordConverter.getDataType(
            Point.metric("test").timestamp(System.currentTimeMillis()).value(123).build(),
            new IotdbDataTypeConverterImpl())).isEqualTo(TSDataType.INT32);
    }

    @Test
    public void testDataTypeInt64() {
        assertThat(IotdbRecordConverter.getDataType(
            Point.metric("test").timestamp(System.currentTimeMillis()).value(123L).build(),
            new IotdbDataTypeConverterImpl())).isEqualTo(TSDataType.INT64);
    }

    @Test
    public void testDataTypeFloat() {
        assertThat(IotdbRecordConverter.getDataType(
            Point.metric("test").timestamp(System.currentTimeMillis()).value(0.1F).build(),
            new IotdbDataTypeConverterImpl())).isEqualTo(TSDataType.FLOAT);
    }

    @Test
    public void testDataTypeDouble() {
        assertThat(IotdbRecordConverter.getDataType(
            Point.metric("test").timestamp(System.currentTimeMillis()).value(0.1D).build(),
            new IotdbDataTypeConverterImpl())).isEqualTo(TSDataType.DOUBLE);
    }

    @Test
    public void testDataTypeBoolean() {
        assertThat(IotdbRecordConverter.getDataType(
            Point.metric("test").timestamp(System.currentTimeMillis()).value(true).build(),
            new IotdbDataTypeConverterImpl())).isEqualTo(TSDataType.BOOLEAN);
    }
}
