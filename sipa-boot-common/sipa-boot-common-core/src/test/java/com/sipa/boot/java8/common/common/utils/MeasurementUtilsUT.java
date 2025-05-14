package com.sipa.boot.java8.common.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sipa.boot.java8.common.utils.MeasurementUtils;

/**
 * @author caszhou
 * @date 2021/7/13
 */
public class MeasurementUtilsUT {
    @Test
    public void testColumnNameToMeasurement() {
        assertThat(MeasurementUtils.columnNameToMeasurement("DFES_numDFC_[0]")).isEqualTo("DFES_numDFC_[0]");
        assertThat(MeasurementUtils.columnNameToMeasurement("first_value(DFES_numDFC_[0])"))
            .isEqualTo("DFES_numDFC_[0]");
        assertThat(MeasurementUtils.columnNameToMeasurement("median(DFES_numDFC_[0], \"position\"=\"left\")"))
            .isEqualTo("DFES_numDFC_[0]");
    }

    @Test
    public void testColumnNameToMeasurement2() {
        assertThat(MeasurementUtils.columnNameToMeasurement("first_value(DFES_num(DF)C_[0])")).isEqualTo("DFES_num(DF");
    }
}
