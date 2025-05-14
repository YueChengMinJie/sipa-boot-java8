package com.sipa.boot.java8.data.iotdb.value.iotdb;

import java.util.List;
import java.util.Map;

import org.apache.iotdb.tsfile.read.common.RowRecord;

/**
 * @author zhouxiajie
 * @date 2021/6/3
 */
public class IotdbQueryResult {
    private List<String> columnNames;

    private List<String> columnTypes;

    private List<RowRecord> rowRecords;

    private Map<String, String> columnNameMap;

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(List<String> columnTypes) {
        this.columnTypes = columnTypes;
    }

    public List<RowRecord> getRowRecords() {
        return rowRecords;
    }

    public void setRowRecords(List<RowRecord> rowRecords) {
        this.rowRecords = rowRecords;
    }

    public Map<String, String> getColumnNameMap() {
        return columnNameMap;
    }

    public void setColumnNameMap(Map<String, String> columnNameMap) {
        this.columnNameMap = columnNameMap;
    }

    public static final class IotdbQueryResultBuilder {
        private List<String> columnNames;

        private List<String> columnTypes;

        private List<RowRecord> rowRecords;

        private Map<String, String> columnNameMap;

        private IotdbQueryResultBuilder() {}

        public static IotdbQueryResultBuilder anIotdbQueryResult() {
            return new IotdbQueryResultBuilder();
        }

        public IotdbQueryResultBuilder withColumnNames(List<String> columnNames) {
            this.columnNames = columnNames;
            return this;
        }

        public IotdbQueryResultBuilder withColumnTypes(List<String> columnTypes) {
            this.columnTypes = columnTypes;
            return this;
        }

        public IotdbQueryResultBuilder withRowRecords(List<RowRecord> rowRecords) {
            this.rowRecords = rowRecords;
            return this;
        }

        public IotdbQueryResultBuilder withColumnNameMap(Map<String, String> columnNameMap) {
            this.columnNameMap = columnNameMap;
            return this;
        }

        public IotdbQueryResult build() {
            IotdbQueryResult iotdbQueryResult = new IotdbQueryResult();
            iotdbQueryResult.setColumnNames(columnNames);
            iotdbQueryResult.setColumnTypes(columnTypes);
            iotdbQueryResult.setRowRecords(rowRecords);
            iotdbQueryResult.setColumnNameMap(columnNameMap);
            return iotdbQueryResult;
        }
    }
}
