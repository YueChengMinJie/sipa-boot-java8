package com.sipa.boot.java8.data.iotdb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.springframework.stereotype.Component;

import com.aliyun.hitsdb.client.value.request.Point;
import com.aliyun.hitsdb.client.value.response.QueryResult;
import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.CheckUtils;
import com.sipa.boot.java8.common.utils.MeasurementUtils;
import com.sipa.boot.java8.data.iotdb.convert.IotdbRecordConverter;
import com.sipa.boot.java8.data.iotdb.property.IotdbProperties;
import com.sipa.boot.java8.data.iotdb.property.IotdbServerProperties;
import com.sipa.boot.java8.data.iotdb.sql.SelectBuilder;
import com.sipa.boot.java8.data.iotdb.transfer.IotdbResultTransfer;
import com.sipa.boot.java8.data.iotdb.transfer.IotdbSqlBuilderTransfer;
import com.sipa.boot.java8.data.iotdb.value.iotdb.IotdbQueryResult;
import com.sipa.boot.java8.data.iotdb.value.iotdb.IotdbRecord;
import com.sipa.boot.java8.data.iotdb.value.tsdb.TsdbQuery;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@Component
@SuppressWarnings("unused")
public class IotdbUtils {
    private static final Log LOGGER = LogFactory.get(IotdbUtils.class);

    private static IotdbProperties iotdbProperties;

    private volatile static SessionPool pool;

    public IotdbUtils(IotdbProperties iotdbProperties) {
        IotdbUtils.iotdbProperties = iotdbProperties;

        List<IotdbServerProperties> servers = CheckUtils.requireNonNull(iotdbProperties.getServers());

        synchronized (IotdbUtils.class) {
            if (Objects.isNull(pool)) {
                pool = new SessionPool(servers.get(0).getHost(), servers.get(0).getPort(), servers.get(0).getUsername(),
                    servers.get(0).getPassword(), iotdbProperties.getSession().getMaxSize());
            }
        }
    }

    // ********************************************************
    // ************************ write *************************
    // ********************************************************

    public static void insertRecord(Point point) {
        insertRecord(IotdbRecordConverter.convert(point));
    }

    public static void insertRecords(List<Point> points) {
        insertRecord(IotdbRecordConverter.convert(points));
    }

    public static void insertRecord(String deviceId, long time, List<String> measurements, List<TSDataType> types,
        List<Object> values) {
        try {
            IotdbUtils.pool.insertRecord(transformDeviceId(deviceId), time, MeasurementUtils.transform(measurements),
                types, values);
        } catch (Exception e) {
            LOGGER.error("IotdbUtils.insertRecord failed.", e);
            throw new RuntimeException(e);
        }
    }

    public static void insertRecord(String deviceId, long time, List<String> measurements, List<String> values) {
        try {
            IotdbUtils.pool.insertRecord(transformDeviceId(deviceId), time, MeasurementUtils.transform(measurements),
                values);
        } catch (Exception e) {
            LOGGER.error("IotdbUtils.insertRecord failed.", e);
            throw new RuntimeException(e);
        }
    }

    // ********************************************************
    // **************** write private method ******************
    // ********************************************************

    private static void insertRecord(IotdbRecord record) {
        if (Objects.nonNull(record)) {
            try {
                IotdbUtils.pool.insertRecords(record.getDeviceIds(), record.getTimes(), record.getMeasurementsList(),
                    record.getTypesList(), record.getValuesList());
            } catch (Exception e) {
                LOGGER.error("IotdbUtils.insertRecord failed.", e);
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.error("IotdbUtils.insertRecord record is null.");
        }
    }

    private static String transformDeviceId(String deviceId) {
        return getIotdbProperties().getOther().getStorageGroups().get(0) + SipaBootCommonConstants.POINT + deviceId;
    }

    // ********************************************************
    // ************************* read *************************
    // ********************************************************

    public static IotdbQueryResult compatibilityQuery(TsdbQuery query) {
        if (Objects.nonNull(query)) {
            return query(IotdbSqlBuilderTransfer.transfer(query));
        }
        return new IotdbQueryResult();
    }

    public static List<QueryResult> compatibilityQuery(List<TsdbQuery> queries) {
        List<QueryResult> qrs = new ArrayList<>();
        for (TsdbQuery query : queries) {
            qrs.addAll(compatibilityQuery(IotdbSqlBuilderTransfer.transfer(query)));
        }
        return qrs;
    }

    public static List<QueryResult> compatibilityQuery(SelectBuilder builder) {
        IotdbQueryResult from = query(builder);
        List<QueryResult> to = new ArrayList<>();
        IotdbResultTransfer.transfer(from, to);
        return to;
    }

    public static IotdbQueryResult query(SelectBuilder builder) {
        return query(builder.toString());
    }

    public static IotdbQueryResult query(String sql) {
        SessionDataSetWrapper wrapper = eqs(sql);
        try {
            List<RowRecord> records = Lists.newArrayList();
            while (wrapper.hasNext()) {
                records.add(wrapper.next());
            }

            List<String> columnNames =
                wrapper.getColumnNames().stream().map(MeasurementUtils::untransform).collect(Collectors.toList());
            Map<String, String> columnNameMap =
                wrapper.getColumnNames().stream().collect(Collectors.toMap(MeasurementUtils::untransform, s -> s));
            return IotdbQueryResult.IotdbQueryResultBuilder.anIotdbQueryResult()
                .withColumnNames(columnNames)
                .withColumnTypes(wrapper.getColumnTypes())
                .withRowRecords(records)
                .withColumnNameMap(columnNameMap)
                .build();
        } catch (Exception e) {
            LOGGER.error("IotdbUtils.queryRecord failed.", e);
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(wrapper)) {
                pool.closeResultSet(wrapper);
            }
        }
    }

    public static SessionDataSetWrapper eqs(String sql) {
        try {
            return pool.executeQueryStatement(sql);
        } catch (Exception e) {
            LOGGER.error("IotdbUtils.getWrapper failed.", e);
            throw new RuntimeException(e);
        }
    }

    // ********************************************************
    // ***************** read private method ******************
    // ********************************************************

    // ********************************************************
    // ************************ raw use ***********************
    // ********************************************************

    public static SessionPool getPool() {
        return pool;
    }

    public static IotdbProperties getIotdbProperties() {
        return iotdbProperties;
    }

    // ********************************************************
    // ************************* hook *************************
    // ********************************************************

    @PreDestroy
    public void destroy() {
        pool.close();
    }
}
