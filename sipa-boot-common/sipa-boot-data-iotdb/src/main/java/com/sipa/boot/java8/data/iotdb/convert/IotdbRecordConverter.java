package com.sipa.boot.java8.data.iotdb.convert;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Component;

import com.aliyun.hitsdb.client.value.request.AbstractPoint;
import com.aliyun.hitsdb.client.value.request.Point;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.MeasurementUtils;
import com.sipa.boot.java8.data.iotdb.convert.base.IIotdbDataTypeConverter;
import com.sipa.boot.java8.data.iotdb.convert.base.IIotdbDeviceIdConverter;
import com.sipa.boot.java8.data.iotdb.value.iotdb.IotdbRecord;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@Component
public class IotdbRecordConverter {
    private static final Log LOGGER = LogFactory.get(IotdbRecordConverter.class);

    private static IIotdbDeviceIdConverter defaultDeviceIdConverter;

    private static IIotdbDataTypeConverter defaultDataTypeConverter;

    public IotdbRecordConverter(IIotdbDeviceIdConverter defaultDeviceIdConverter,
        IIotdbDataTypeConverter defaultDataTypeConverter) {
        IotdbRecordConverter.defaultDeviceIdConverter = defaultDeviceIdConverter;
        IotdbRecordConverter.defaultDataTypeConverter = defaultDataTypeConverter;
    }

    public static IotdbRecord convert(Point from) {
        return convert(Collections.singletonList(from), IotdbRecordConverter.defaultDeviceIdConverter,
            IotdbRecordConverter.defaultDataTypeConverter);
    }

    public static IotdbRecord convert(List<Point> from) {
        return convert(from, IotdbRecordConverter.defaultDeviceIdConverter,
            IotdbRecordConverter.defaultDataTypeConverter);
    }

    public static IotdbRecord convert(Point from, IIotdbDeviceIdConverter deviceIdConvert,
        IIotdbDataTypeConverter dataTypeConverter) {
        return convert(Collections.singletonList(from), deviceIdConvert, dataTypeConverter);
    }

    public static IotdbRecord convert(List<Point> from, IIotdbDeviceIdConverter deviceIdConvert,
        IIotdbDataTypeConverter dataTypeConverter) {
        if (CollectionUtils.isNotEmpty(from)) {
            if (CollectionUtils.size(from) > 1) {
                from.sort(Comparator.comparing(AbstractPoint::getTimestamp));
            }

            Map<Long, List<String>> timeMeasurementsMap = Maps.newHashMap();
            Map<Long, List<TSDataType>> timeTypesMap = Maps.newHashMap();
            Map<Long, List<Object>> timeValuesMap = Maps.newHashMap();

            List<String> deviceIds = Lists.newArrayList();
            List<Long> times = Lists.newArrayList();
            List<List<String>> measurementsList = Lists.newArrayList();
            List<List<TSDataType>> typesList = Lists.newArrayList();
            List<List<Object>> valuesList = Lists.newArrayList();

            IotdbRecord.IotdbDtoBuilder builder = IotdbRecord.IotdbDtoBuilder.anIotdbDto()
                .withDeviceIds(deviceIds)
                .withTimes(times)
                .withMeasurementsList(measurementsList)
                .withTypesList(typesList)
                .withValuesList(valuesList);

            for (Point point : from) {
                Long ts = point.getTimestamp();
                String metric = point.getMetric();
                Object value = point.getValue();
                if (Objects.nonNull(ts) && timeMeasurementsMap.containsKey(ts)) {
                    timeMeasurementsMap.get(ts).add(transformMeasurement(metric));
                    timeTypesMap.get(ts).add(getDataType(point, dataTypeConverter));
                    timeValuesMap.get(ts).add(value);
                } else if (Objects.nonNull(ts)) {
                    deviceIds.add(getDeviceId(point, deviceIdConvert));
                    times.add(ts);

                    List<String> measurements = Lists.newArrayList();
                    List<TSDataType> types = Lists.newArrayList();
                    List<Object> values = Lists.newArrayList();

                    timeMeasurementsMap.put(ts, measurements);
                    timeTypesMap.put(ts, types);
                    timeValuesMap.put(ts, values);

                    measurementsList.add(measurements);
                    typesList.add(types);
                    valuesList.add(values);

                    measurements.add(transformMeasurement(metric));
                    types.add(getDataType(point, dataTypeConverter));
                    values.add(value);
                }
            }

            IotdbRecord record = builder.build();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(record.toString());
            }

            return record;
        }
        return null;
    }

    private static String transformMeasurement(String metric) {
        return MeasurementUtils.transform(metric);
    }

    public static TSDataType getDataType(Point point, IIotdbDataTypeConverter dataTypeConverter) {
        if (Objects.isNull(dataTypeConverter)) {
            throw new IllegalArgumentException("Check your [IIotdbDataTypeConverter] implement bean, now it is null.");
        }
        TSDataType dataType = dataTypeConverter.covert(point);
        if (Objects.isNull(dataType)) {
            throw new IllegalArgumentException(
                "Tsdb point convert data type result is null, please check your [IIotdbDataTypeConverter] implement bean.");
        }
        return dataType;
    }

    public static String getDeviceId(Point from, IIotdbDeviceIdConverter deviceIdConvert) {
        if (Objects.isNull(deviceIdConvert)) {
            throw new IllegalArgumentException("Check your [IIotdbDeviceIdConverter] implement bean, now it is null.");
        }
        String deviceId = deviceIdConvert.covert(from);
        if (StringUtils.isBlank(deviceId)) {
            throw new IllegalArgumentException(
                "Tsdb point convert device id result is empty, please check your [IIotdbDeviceIdConverter] implement bean.");
        }
        return deviceId;
    }
}
