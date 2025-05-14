package com.sipa.boot.java8.data.iotdb.transfer;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.Path;
import org.apache.iotdb.tsfile.read.common.RowRecord;

import com.aliyun.hitsdb.client.value.response.QueryResult;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.constants.SipaBootTsdbConstants;
import com.sipa.boot.java8.common.utils.ListUtils;
import com.sipa.boot.java8.common.utils.MeasurementUtils;
import com.sipa.boot.java8.data.iotdb.value.iotdb.IotdbQueryResult;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbResultTransfer {
    public static void transfer(IotdbQueryResult from, Map<String, Map<Long, Object>> to) {
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            List<RowRecord> rowRecords = from.getRowRecords();
            if (CollectionUtils.isNotEmpty(rowRecords)) {
                for (RowRecord rowRecord : rowRecords) {
                    long time = rowRecord.getTimestamp();

                    List<Field> fs = rowRecord.getFields();
                    Field field = ListUtils.first(fs);
                    Path path = new Path(field.getStringValue(), true);
                    String metric = path.getMeasurement();

                    field = ListUtils.second(fs);
                    Object value = field.getObjectValue(field.getDataType());

                    to.put(metric, Collections.singletonMap(time, value));
                }
            }
        }
    }

    public static void transfer(IotdbQueryResult from, List<QueryResult> to) {
        if (Objects.nonNull(from) && Objects.nonNull(to)) {
            List<Long> times = new ArrayList<>();
            Map<Path, List<Object>> measurementValues = new HashMap<>(SipaBootCommonConstants.Number.INT_16);

            for (RowRecord rowRecord : from.getRowRecords()) {
                times.add(rowRecord.getTimestamp());

                List<String> columnNames = from.getColumnNames();
                Map<String, String> columnNameMap = from.getColumnNameMap();
                for (int i = 1; i < columnNames.size(); i++) {
                    String columnName = columnNames.get(i);
                    Path path = new Path(MeasurementUtils.columnNameToMeasurement(columnNameMap.get(columnName)), true);
                    Field field = rowRecord.getFields().get(i - 1);

                    measurementValues.putIfAbsent(path, new ArrayList<>());
                    List<Object> values = measurementValues.get(path);
                    values.add(field.getObjectValue(field.getDataType()));
                }
            }

            for (Map.Entry<Path, List<Object>> entry : measurementValues.entrySet()) {
                Path path = entry.getKey();
                String measurement = path.getMeasurement();
                List<Object> values = entry.getValue();

                QueryResult qr = new QueryResult();
                qr.setMetric(MeasurementUtils.untransform(measurement));
                qr.setDps(getDps(times, values));
                qr.setTags(Collections.singletonMap(SipaBootTsdbConstants.TagName.COLLECTION_ID, path.getDevice()));
                to.add(qr);
            }
        }
    }

    private static LinkedHashMap<Long, Object> getDps(List<Long> times, List<Object> values) {
        LinkedHashMap<Long, Object> dps = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(times) && CollectionUtils.isNotEmpty(values) && times.size() == values.size()) {
            for (int i = 0; i < times.size(); i++) {
                Long time = times.get(i);
                Object value = values.get(i);
                if (Objects.nonNull(value)) {
                    dps.put(time, value);
                }
            }
        }
        return dps;
    }
}
