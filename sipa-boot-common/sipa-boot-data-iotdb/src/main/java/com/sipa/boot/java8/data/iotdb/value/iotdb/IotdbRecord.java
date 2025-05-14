package com.sipa.boot.java8.data.iotdb.value.iotdb;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbRecord {
    private List<String> deviceIds;

    private List<Long> times;

    private List<List<String>> measurementsList;

    private List<List<TSDataType>> typesList;

    private List<List<Object>> valuesList;

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public List<List<String>> getMeasurementsList() {
        return measurementsList;
    }

    public void setMeasurementsList(List<List<String>> measurementsList) {
        this.measurementsList = measurementsList;
    }

    public List<List<TSDataType>> getTypesList() {
        return typesList;
    }

    public void setTypesList(List<List<TSDataType>> typesList) {
        this.typesList = typesList;
    }

    public List<List<Object>> getValuesList() {
        return valuesList;
    }

    public void setValuesList(List<List<Object>> valuesList) {
        this.valuesList = valuesList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("deviceIds", deviceIds)
            .append("times", times)
            .append("measurementsList", measurementsList)
            .append("typesList", typesList)
            .append("valuesList", valuesList)
            .toString();
    }

    public static final class IotdbDtoBuilder {
        private List<String> deviceIds;

        private List<Long> times;

        private List<List<String>> measurementsList;

        private List<List<TSDataType>> typesList;

        private List<List<Object>> valuesList;

        private IotdbDtoBuilder() {}

        public static IotdbDtoBuilder anIotdbDto() {
            return new IotdbDtoBuilder();
        }

        public IotdbDtoBuilder withDeviceIds(List<String> deviceIds) {
            this.deviceIds = deviceIds;
            return this;
        }

        public IotdbDtoBuilder withTimes(List<Long> times) {
            this.times = times;
            return this;
        }

        public IotdbDtoBuilder withMeasurementsList(List<List<String>> measurementsList) {
            this.measurementsList = measurementsList;
            return this;
        }

        public IotdbDtoBuilder withTypesList(List<List<TSDataType>> typesList) {
            this.typesList = typesList;
            return this;
        }

        public IotdbDtoBuilder withValuesList(List<List<Object>> valuesList) {
            this.valuesList = valuesList;
            return this;
        }

        public IotdbRecord build() {
            IotdbRecord iotdbRecord = new IotdbRecord();
            iotdbRecord.setDeviceIds(deviceIds);
            iotdbRecord.setTimes(times);
            iotdbRecord.setMeasurementsList(measurementsList);
            iotdbRecord.setTypesList(typesList);
            iotdbRecord.setValuesList(valuesList);
            return iotdbRecord;
        }
    }
}
