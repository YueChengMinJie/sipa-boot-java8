package com.sipa.boot.java8.data.hbase.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.data.hbase.property.HBaseProperties;
import com.sipa.boot.java8.common.utils.Utils;

/**
 * @author zhouxiajie
 * @date 2019-07-12
 */
@Component
public class HBaseUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseUtils.class);

    private static final int BATCH_DELETE_SIZE = 10000;

    private static volatile Integer version;

    private static volatile Connection connection;

    private static volatile HBaseProperties property;

    public HBaseUtils(HBaseProperties property) {
        HBaseUtils.property = property;
    }

    // ********************************************************
    // ************************ Config ************************
    // ********************************************************

    private static synchronized Connection getConnection() {
        if (HBaseUtils.connection == null || HBaseUtils.version == null
            || !HBaseUtils.version.equals(HBaseUtils.property.getVersion())) {
            HBaseUtils.connection = createConnection();

            if (HBaseUtils.property.getVersion() == null) {
                HBaseUtils.property.setVersion(0);
                HBaseUtils.version = 0;
            } else {
                version = HBaseUtils.property.getVersion();
            }
        }

        return HBaseUtils.connection;
    }

    private static Connection createConnection() {
        try {
            Configuration conf = HBaseConfiguration.create();

            conf.set("hbase.zookeeper.quorum", HBaseUtils.property.getQuorum());
            conf.set("hbase.zookeeper.property.clientPort", Utils.stringValueOf(HBaseUtils.property.getClientPort()));
            conf.set("hbase.client.keyvalue.maxsize", String.valueOf(HBaseUtils.property.getMaxSize() * 1024 * 1024));

            conf.set("zookeeper.session.timeout", "500");
            conf.set("hbase.zookeeper.property.maxclientcnxns", "300");

            conf.set("hbase.client.pause", "50");
            conf.set("hbase.client.retries.number", "3");
            conf.set("hbase.ipc.client.socket.timeout.connect", "1000");
            conf.set("hbase.regionserver.handler.count", "500");

            // kerberos
            // kerberosLogin(conf);

            return ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // private static void kerberosLogin(Configuration conf) throws IOException {
    // System.setProperty("java.security.krb5.conf",
    // FileUtils.copyFileFromClassPathToFileSystem("kerberos/krb5.conf", "logs/krb5.conf"));
    //
    // conf.set("hadoop.security.authentication", "kerberos");
    // conf.set("hbase.security.authentication", "kerberos");
    // conf.set("hbase.cluster.distributed", "true");
    // conf.set("hbase.rpc.protection", "privacy");
    // conf.set("hbase.master.kerberos.principal", "hbase/_HOST@IVEHCORE.COM");
    // conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@IVEHCORE.COM");
    //
    // String principal = HBaseUtils.property.getPrincipal();
    // conf.set("kerberos.principal", principal);
    //
    // String keytab = HBaseUtils.property.getKeytab();
    // conf.set("keytab.file", keytab);
    //
    // UserGroupInformation.setConfiguration(conf);
    // UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytab);
    // ugi.reloginFromKeytab();
    // }

    // ********************************************************
    // ************************* API **************************
    // ********************************************************

    /**
     * 创建表.
     *
     * @param tableName
     *            表名
     * @param columnFamily
     *            列族名的集合
     * @return 是否创建成功
     */
    public static boolean createTable(String tableName, Set<String> columnFamily) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(columnFamily, "columnFamily cannot be null");

        Admin admin = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                LOGGER.info("table exists.");
                return false;
            } else {
                admin.createTable(getTableDescriptor(tableName, columnFamily));
                LOGGER.info("create table success.");
                return true;
            }
        } catch (IOException e) {
            LOGGER.info("create table fail.", e);
            return false;
        } finally {
            close(admin, null, null);
        }
    }

    private static TableDescriptor getTableDescriptor(String tableName, Set<String> columnFamily) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(columnFamily, "columnFamily cannot be null");

        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));

        if (CollectionUtils.isNotEmpty(columnFamily)) {
            builder.setColumnFamilies(columnFamily.stream()
                .map(cf -> ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cf)).build())
                .collect(Collectors.toList()));
        }

        return builder.build();
    }

    /**
     * 预分区创建表.
     *
     * @param tableName
     *            表名
     * @param columnFamily
     *            列族名的集合
     * @param splitKeys
     *            预分期region
     * @return 是否创建成功
     */
    public static boolean createTableBySplitKeys(String tableName, Set<String> columnFamily, byte[][] splitKeys) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(columnFamily, "columnFamily cannot be null");
        Objects.requireNonNull(splitKeys, "splitKeys cannot be null");

        Admin admin = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                LOGGER.info("table exists.");
                return false;
            } else {
                admin.createTable(getTableDescriptor(tableName, columnFamily), splitKeys);
                LOGGER.info("create table success.");
                return true;
            }
        } catch (IOException e) {
            LOGGER.info("create table fail.", e);
            return false;
        } finally {
            close(admin, null, null);
        }
    }

    /**
     * 自定义获取分区splitKeys.
     *
     * @param keys
     *            分区键
     * @return 预分期region
     */
    public static byte[][] getSplitKeys(String[] keys) {
        // "1|", "2|", "3|", "4|", "5|", "6|", "7|", "8|", "9|"
        Objects.requireNonNull(keys, "keys cannot be null");

        byte[][] splitKeys = new byte[keys.length][];
        TreeSet<byte[]> rows = new TreeSet<>(Bytes.BYTES_COMPARATOR);
        for (String key : keys) {
            rows.add(Bytes.toBytes(key));
        }

        Iterator<byte[]> rowKeyIter = rows.iterator();
        int i = 0;
        while (rowKeyIter.hasNext()) {
            byte[] tempRow = rowKeyIter.next();
            splitKeys[i] = tempRow;
            i++;
        }
        return splitKeys;
    }

    /**
     * 按startKey和endKey，分区数获取分区.
     *
     * @param startKeyHex
     *            开始key
     * @param endKeyHex
     *            结束key
     * @param numRegions
     *            分区数
     * @return 分区
     */
    public static byte[][] getHexSplits(String startKeyHex, String endKeyHex, Integer numRegions) {
        Objects.requireNonNull(startKeyHex, "startKeyHex cannot be null");
        Objects.requireNonNull(endKeyHex, "endKeyHex cannot be null");
        Objects.requireNonNull(numRegions, "numRegions cannot be null");

        byte[][] splits = new byte[numRegions - 1][];
        BigInteger lowestKey = new BigInteger(startKeyHex, 16);
        BigInteger highestKey = new BigInteger(endKeyHex, 16);
        BigInteger range = highestKey.subtract(lowestKey);
        BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));

        lowestKey = lowestKey.add(regionIncrement);
        for (int i = 0; i < numRegions - 1; i++) {
            BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
            byte[] b = String.format("%016x", key).getBytes();
            splits[i] = b;
        }
        return splits;
    }

    /**
     * 获取table.
     *
     * @param tableName
     *            表名
     * @return Table
     */
    private static Table getTable(String tableName) throws IOException {
        Objects.requireNonNull(tableName, "tableName cannot be null");

        return HBaseUtils.getConnection().getTable(TableName.valueOf(tableName));
    }

    /**
     * 查询库中所有表的表名.
     *
     * @return 所有表名
     */
    public static List<String> getAllTableNames() {
        List<String> result = new ArrayList<>();

        Admin admin = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            TableName[] tableNames = admin.listTableNames();

            for (TableName tableName : tableNames) {
                result.add(tableName.getNameAsString());
            }
        } catch (IOException e) {
            LOGGER.error("get all table names fail.", e);
        } finally {
            close(admin, null, null);
        }

        return result;
    }

    /**
     * 遍历查询指定表中的所有数据.
     *
     * @param tableName
     *            表名
     * @return 数据
     */
    public static Map<String, Map<String, String>> getResultScanner(String tableName) {
        Objects.requireNonNull(tableName, "tableName cannot be null");

        return queryData(tableName, new Scan(), -1);
    }

    /**
     * 根据startRowKey和stopRowKey遍历查询指定表中的所有数据.
     *
     * @param tableName
     *            表名
     * @param startRowKey
     *            起始rowKey
     * @param stopRowKey
     *            结束rowKey
     * @param limit
     *            单页限制（无需限制传-1）
     * @param startTime
     *            起始时间
     * @param stopTime
     *            结束时间
     * @param filter
     *            过滤条件.
     * @return 数据
     */
    public static Map<String, Map<String, String>> getResultScanner(String tableName, String startRowKey,
        String stopRowKey, int limit, long startTime, long stopTime, Filter filter) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(startRowKey, "startRowKey cannot be null");
        Objects.requireNonNull(stopRowKey, "stopRowKey cannot be null");

        Scan scan = new Scan();
        scan.withStartRow(Bytes.toBytes(startRowKey), true);
        scan.withStopRow(Bytes.toBytes(stopRowKey), true);

        try {
            if (startTime > 0 && stopTime > 0 && stopTime > startTime) {
                scan.setTimeRange(startTime, stopTime);
            }
        } catch (IOException e) {
            LOGGER.warn("set time range error", e);
        }
        scan.setFilter(filter);
        return queryData(tableName, scan, limit);
    }

    /**
     * 根据startRowKey和stopRowKey遍历查询指定表中的所有数据.
     *
     * @param tableName
     *            表名
     * @param startRowKey
     *            起始rowKey
     * @param stopRowKey
     *            结束rowKey
     * @param limit
     *            单页限制（无需限制传-1）.
     * @return 数据
     */
    public static Map<String, Map<String, String>> getResultScanner(String tableName, String startRowKey,
        String stopRowKey, int limit) {
        return getResultScanner(tableName, startRowKey, stopRowKey, limit, 0, 0, null);
    }

    /**
     * 根据startRowKey和stopRowKey遍历查询指定表中的所有数据.
     *
     * @param tableName
     *            表名
     * @param startRowKey
     *            起始rowKey
     * @param stopRowKey
     *            结束rowKey
     * @return 数据
     */

    public static Map<String, Map<String, String>> getResultScanner(String tableName, String startRowKey,
        String stopRowKey) {
        return getResultScanner(tableName, startRowKey, stopRowKey, -1);
    }

    /**
     * 通过行前缀过滤器查询数据.
     *
     * @param tableName
     *            表名
     * @param prefix
     *            以prefix开始的行键
     * @return 数据
     */
    public static Map<String, Map<String, String>> getPrefixRowFilterResultScanner(String tableName, String prefix) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(prefix, "prefix cannot be null");

        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(Bytes.toBytes(prefix)));
        return queryData(tableName, scan, -1);
    }

    /**
     * 通过列前缀过滤器查询数据.
     *
     * @param tableName
     *            表名
     * @param column
     *            以prefix开始的列名
     * @return 数据
     */
    public static Map<String, Map<String, String>> getPrefixColumnFilterResultScanner(String tableName, String column) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(column, "column cannot be null");

        Scan scan = new Scan();
        scan.setFilter(new ColumnPrefixFilter(Bytes.toBytes(column)));
        return queryData(tableName, scan, -1);
    }

    /**
     * 查询行键中包含特定字符的数据.
     *
     * @param tableName
     *            表名
     * @param keyword
     *            包含指定关键词的行键
     * @return 数据
     */
    public static Map<String, Map<String, String>> getRowFilterResultScanner(String tableName, String keyword) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(keyword, "keyword cannot be null");

        Scan scan = new Scan();
        scan.setFilter(new RowFilter(CompareOperator.GREATER_OR_EQUAL, new SubstringComparator(keyword)));
        return queryData(tableName, scan, -1);
    }

    /**
     * 查询列名中包含特定字符的数据.
     *
     * @param tableName
     *            表名
     * @param family
     *            列族
     * @param qualifier
     *            列
     * @param value
     *            值
     * @return 数据
     */
    public static Map<String, Map<String, String>> getFqvFilterRs(String tableName, String family, String qualifier,
        String value) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(family, "family cannot be null");
        Objects.requireNonNull(qualifier, "qualifier cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        Scan scan = new Scan();
        SingleColumnValueFilter scvf = new SingleColumnValueFilter(Bytes.toBytes(family), Bytes.toBytes(qualifier),
            CompareOperator.GREATER_OR_EQUAL, value.getBytes());
        scvf.setFilterIfMissing(true);
        scan.setFilter(scvf);
        return queryData(tableName, scan, -1);
    }

    /**
     * 查询列值中以特定字符开头的数据.
     *
     * @param tableName
     *            表名
     * @param keyword
     *            包含指定关键词的列名
     * @return 数据
     */
    public static Map<String, Map<String, String>> getQualifierFilterResultScanner(String tableName, String keyword) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(keyword, "keyword cannot be null");

        Scan scan = new Scan();
        scan.setFilter(new QualifierFilter(CompareOperator.GREATER_OR_EQUAL, new SubstringComparator(keyword)));
        return queryData(tableName, scan, -1);
    }

    /**
     * 提供另一种分页方法：查询分页数据.
     *
     * @param tableName
     *            表名
     * @param start
     *            开始时间
     * @param end
     *            结束时间
     * @param pageSize
     *            每页大小
     * @return 数据
     */
    public static Map<String, Map<String, String>> getPageFilterResultScanner(String tableName, String start,
        String end, Integer pageSize) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        Objects.requireNonNull(pageSize, "pageSize cannot be null");

        Scan scan = new Scan();
        scan.setFilter(new RowFilter(CompareOperator.GREATER_OR_EQUAL, new SubstringComparator(start)));
        scan.setFilter(new RowFilter(CompareOperator.LESS_OR_EQUAL, new SubstringComparator(end)));
        scan.setFilter(new PageFilter(pageSize));
        return queryData(tableName, scan, -1);
    }

    /**
     * 通过表名以及过滤条件查询数据.
     *
     * @param tableName
     *            表名
     * @param scan
     *            过滤条件
     * @return 数据
     */
    private static Map<String, Map<String, String>> queryData(String tableName, Scan scan, int limit) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(scan, "scan cannot be null");

        Map<String, Map<String, String>> result = new LinkedHashMap<>(16);
        Table table = null;
        ResultScanner rs = null;
        try {
            table = getTable(tableName);
            rs = table.getScanner(scan);
            int count = 0;
            for (Result r : rs) {
                if (limit != -1 && count >= limit) {
                    break;
                }
                Map<String, String> columnMap = new LinkedHashMap<>(16);
                String rowKey = null;
                for (Cell cell : r.listCells()) {
                    if (rowKey == null) {
                        rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    }
                    columnMap.put(
                        Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                }

                if (rowKey != null) {
                    result.put(rowKey, columnMap);
                    count++;
                }
            }
        } catch (IOException e) {
            LOGGER.error("scan data fail.", e);
        } finally {
            close(null, rs, table);
        }

        return result;
    }

    /**
     * 根据tableName和rowKey精确查询一行的数据.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            行键
     * @return 返回一行的数据
     */
    public static Map<String, String> getRowData(String tableName, String rowKey) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");

        Map<String, String> result = new HashMap<>(3);
        Table table = null;
        try {
            table = getTable(tableName);
            Result hTableResult = table.get(new Get(Bytes.toBytes(rowKey)));
            if (hTableResult != null && !hTableResult.isEmpty()) {
                for (Cell cell : hTableResult.listCells()) {
                    LOGGER.debug("family [{}]",
                        Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength()));
                    LOGGER.debug("qualifier [{}]",
                        Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()));
                    LOGGER.debug("value [{}]",
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                    LOGGER.debug("timestamp [{}]", cell.getTimestamp());

                    result.put(
                        Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()),
                        Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                }
            }
        } catch (IOException e) {
            LOGGER.error("select one row fail.", e);
        } finally {
            close(null, null, table);
        }

        return result;
    }

    /**
     * 根据tableName、rowKey、familyName、column查询指定单元格的数据.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @param familyName
     *            列族名
     * @param columnName
     *            列名
     * @return 返回一行的数据
     */
    public static String getColumnValue(String tableName, String rowKey, String familyName, String columnName) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");
        Objects.requireNonNull(familyName, "familyName cannot be null");
        Objects.requireNonNull(columnName, "columnName cannot be null");

        String rs = null;
        Table table = null;
        try {
            table = getTable(tableName);
            Result result = table.get(new Get(Bytes.toBytes(rowKey)));
            if (result != null && !result.isEmpty()) {
                Cell cell = result.getColumnLatestCell(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
                if (cell != null) {
                    rs = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                }
            }
        } catch (IOException e) {
            LOGGER.error("select one row by family fail.", e);
        } finally {
            close(null, null, table);
        }

        return rs;
    }

    /**
     * 根据tableName、rowKey、familyName、column查询指定单元格多个版本的数据.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @param familyName
     *            列族名
     * @param columnName
     *            列名
     * @param versions
     *            需要查询的版本数
     * @return 返回一行的多版本数据
     */
    public static List<String> getColumnValuesByVersion(String tableName, String rowKey, String familyName,
        String columnName, Integer versions) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");
        Objects.requireNonNull(familyName, "familyName cannot be null");
        Objects.requireNonNull(columnName, "columnName cannot be null");
        Objects.requireNonNull(versions, "versions cannot be null");

        List<String> result = new ArrayList<>(versions);
        Table table = null;
        try {
            table = getTable(tableName);

            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
            get.readVersions(versions);

            Result hTableResult = table.get(get);
            if (hTableResult != null && !hTableResult.isEmpty()) {
                for (Cell cell : hTableResult.listCells()) {
                    result.add(Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                }
            }
        } catch (IOException e) {
            LOGGER.error("select one versions row fail.", e);
        } finally {
            close(null, null, table);
        }

        return result;
    }

    /**
     * 新增数据 or 更新数据.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @param familyName
     *            列族名
     * @param columns
     *            列名数组
     * @param values
     *            列值得数组
     * @param timestamp
     *            数据时间
     */
    public static void putData(String tableName, String rowKey, String familyName, List<String> columns,
        List<String> values, long timestamp) {
        Objects.requireNonNull(tableName, "tableName cannot be null");

        Table table = null;
        try {
            table = getTable(tableName);

            putData(table, tableName, rowKey, familyName, columns, values, timestamp);
        } catch (Exception e) {
            LOGGER.error("insert or update fail.", e);
        } finally {
            close(null, null, table);
        }
    }

    /**
     * 新增数据 or 更新数据.
     *
     * @param table
     *            HTable
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @param familyName
     *            列族名
     * @param columns
     *            列名数组
     * @param values
     *            列值得数组
     * @param timestamp
     *            数据时间
     */
    private static void putData(Table table, String tableName, String rowKey, String familyName, List<String> columns,
        List<String> values, long timestamp) {
        Objects.requireNonNull(table, "table cannot be null");
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");
        Objects.requireNonNull(familyName, "familyName cannot be null");
        Objects.requireNonNull(columns, "tableName cannot be null");
        Objects.requireNonNull(values, "tableName cannot be null");

        try {
            Put put = new Put(Bytes.toBytes(rowKey));

            if (timestamp != 0) {
                put.setTimestamp(timestamp);
            }

            if (columns.size() == values.size()) {
                for (int i = 0; i < columns.size(); i++) {
                    String column = columns.get(i);
                    String value = values.get(i);
                    if (StringUtils.isNotBlank(column) && StringUtils.isNotBlank(value)) {
                        put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(column), Bytes.toBytes(value));
                    }
                }

                table.put(put);
            }
        } catch (Exception e) {
            LOGGER.error("insert or update fail.", e);
        } finally {
            close(null, null, table);
        }
    }

    /**
     * 为表的某个单元格赋值.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @param familyName
     *            列族名
     * @param column
     *            列名
     * @param value
     *            列值
     */
    public static void setColumnValue(String tableName, String rowKey, String familyName, String column, String value) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");
        Objects.requireNonNull(familyName, "familyName cannot be null");
        Objects.requireNonNull(column, "column cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        Table table = null;
        try {
            table = getTable(tableName);

            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(column), Bytes.toBytes(value));

            table.put(put);
        } catch (IOException e) {
            LOGGER.error("set column value fail.", e);
        } finally {
            close(null, null, table);
        }
    }

    /**
     * 删除指定的单元格.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @param familyName
     *            列族名
     * @param column
     *            列名
     * @return 是否成功
     */
    public static boolean deleteColumn(String tableName, String rowKey, String familyName, String column) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");
        Objects.requireNonNull(familyName, "familyName cannot be null");
        Objects.requireNonNull(column, "column cannot be null");

        Admin admin = null;
        Table table = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                table = getTable(tableName);

                Delete delete = new Delete(Bytes.toBytes(rowKey));
                delete.addColumns(Bytes.toBytes(familyName), Bytes.toBytes(column));

                table.delete(delete);
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("delete column fail.", e);
        } finally {
            close(admin, null, table);
        }
        return false;
    }

    /**
     * 根据rowKey删除指定的行.
     *
     * @param tableName
     *            表名
     * @param rowKey
     *            rowKey
     * @return 是否成功
     */
    public static boolean deleteRow(String tableName, String rowKey) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKey, "rowKey cannot be null");

        Admin admin = null;
        Table table = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                table = getTable(tableName);

                Delete delete = new Delete(Bytes.toBytes(rowKey));
                table.delete(delete);

                return true;
            }
        } catch (IOException e) {
            LOGGER.error("delete rowkey fail.", e);
        } finally {
            close(admin, null, table);
        }
        return false;
    }

    /**
     * 根据rowKey批量删除指定的行.
     *
     * @param tableName
     *            表名
     * @param rowKeys
     *            rowKeys
     * @return 是否成功
     */
    public static boolean deleteRows(String tableName, List<String> rowKeys) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(rowKeys, "rowKey cannot be null");
        Objects.requireNonNull(rowKeys.size() > BATCH_DELETE_SIZE ? null : 1,
            "rowKey cannot be greater " + BATCH_DELETE_SIZE);

        Admin admin = null;
        Table table = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                table = getTable(tableName);

                table.delete(rowKeys.stream().map(k -> new Delete(Bytes.toBytes(k))).collect(Collectors.toList()));

                return true;
            }
        } catch (IOException e) {
            LOGGER.error("delete rowkeys fail.", e);
        } finally {
            close(admin, null, table);
        }
        return false;
    }

    /**
     * 根据startRowKey和endRowKey批量删除指定的行.
     *
     * @param tableName
     *            表名
     * @param startRowKey
     *            开始Key
     * @param endRowKey
     *            结束Key
     * @return 是否成功
     */
    public static boolean deleteRows(String tableName, String startRowKey, String endRowKey) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(startRowKey, "startRowKey cannot be null");
        Objects.requireNonNull(endRowKey, "startRowKey cannot be null");

        Admin admin = null;
        Table table = null;
        ResultScanner rs = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                table = getTable(tableName);

                Scan scan = new Scan();
                scan.withStartRow(Bytes.toBytes(startRowKey), true);
                scan.withStopRow(Bytes.toBytes(endRowKey), true);
                rs = table.getScanner(scan);

                List<Delete> deletes = new ArrayList<>();
                for (Result r : rs) {
                    deletes.add(new Delete(r.getRow()));

                    if (deletes.size() >= BATCH_DELETE_SIZE) {
                        deleteRows(table, deletes);
                    }
                }
                deleteRows(table, deletes);

                return true;
            }
        } catch (IOException e) {
            LOGGER.error("delete data fail.", e);
        } finally {
            close(admin, rs, table);
        }
        return false;
    }

    private static void deleteRows(Table table, List<Delete> deletes) throws IOException {
        Objects.requireNonNull(table, "table cannot be null");
        Objects.requireNonNull(deletes, "deletes cannot be null");

        if (CollectionUtils.isNotEmpty(deletes)) {
            table.delete(deletes);
        }
        LOGGER.info("Batch delete size: [{}]", deletes.size());
        deletes.clear();
    }

    /**
     * 根据columnFamily删除指定的列族.
     *
     * @param tableName
     *            表名
     * @param columnFamily
     *            列族
     * @return 是否成功
     */
    public static boolean deleteColumnFamily(String tableName, String columnFamily) {
        Objects.requireNonNull(tableName, "tableName cannot be null");
        Objects.requireNonNull(columnFamily, "columnFamily cannot be null");

        Admin admin = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                admin.deleteColumnFamily(TableName.valueOf(tableName), Bytes.toBytes(columnFamily));
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("delete column family fail.", e);
        } finally {
            close(admin, null, null);
        }
        return false;
    }

    /**
     * 删除表.
     *
     * @param tableName
     *            表名
     * @return 是否成功
     */
    public static boolean deleteTable(String tableName) {
        Admin admin = null;
        try {
            admin = HBaseUtils.getConnection().getAdmin();

            if (admin.tableExists(TableName.valueOf(tableName))) {
                admin.disableTable(TableName.valueOf(tableName));
                admin.deleteTable(TableName.valueOf(tableName));
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("delete table fail.", e);
        } finally {
            close(admin, null, null);
        }
        return false;
    }

    /**
     * 关闭流.
     *
     * @param admin
     *            Admin
     * @param rs
     *            ResultScanner
     * @param table
     *            Table
     */
    private static void close(Admin admin, ResultScanner rs, Table table) {
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                LOGGER.error("close admin fail.", e);
            }
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                LOGGER.error("close rs fail.", e);
            }
        }

        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                LOGGER.error("close table fail.", e);
            }
        }
    }

    /**
     * 根据timeRange遍历查询指定表中的所有数据.
     *
     * @param tableName
     *            表名
     * @param startTime
     *            起始时间
     * @param stopTime
     *            结束时间
     * @param limit
     *            单页限制（无需限制传-1）
     * @param filter
     *            过滤条件
     * @return 查询结果
     */
    public static Map<String, Map<String, String>> getResultScanner(String tableName, long startTime, long stopTime,
        int limit, Filter filter) {
        Objects.requireNonNull(tableName, "tableName cannot be null");

        Scan scan = new Scan();
        try {
            scan.setTimeRange(startTime, stopTime);
            scan.setFilter(filter);
            return queryData(tableName, scan, limit);
        } catch (IOException e) {
            LOGGER.warn("time range error! startTime is [{}], stopTime is [{}]", startTime, stopTime);
        }
        return null;
    }

    /**
     * 只返回第一个kv，count数量.
     *
     * @param tableName
     *            表名
     * @param startRowKey
     *            开始rowkey
     * @param stopRowKey
     *            结束rowkey
     * @return count.
     */
    public static Integer count(String tableName, String startRowKey, String stopRowKey) {
        int result = 0;
        Table table = null;
        ResultScanner rs = null;
        try {
            table = getTable(tableName);

            Scan scan = new Scan();
            scan.setFilter(new FirstKeyOnlyFilter());
            scan.withStartRow(Bytes.toBytes(startRowKey), true);
            scan.withStopRow(Bytes.toBytes(stopRowKey), true);

            rs = table.getScanner(scan);
            for (Result r : rs) {
                result += r.size();
            }
        } catch (IOException e) {
            LOGGER.error("count data fail.", e);
        } finally {
            close(null, rs, table);
        }

        return result;
    }

    /**
     * 根据键获取表的名字
     *
     * @param tableKey
     *            表名字的key
     * @return 表名字
     */
    public static String getTableName(String tableKey) {
        return property.getTableNameMap().get(tableKey);
    }
}
