package com.sipa.boot.java8.common.utils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 * @date 2020/3/18
 */
public class MeasurementUtils {
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\((.*?)\\)");

    /**
     * A.B -> A$B
     *
     * @param metric
     *            度量
     * @return iotdb测点
     */
    public static String transform(String metric) {
        return Objects.requireNonNull(metric).replaceAll("\\.", Matcher.quoteReplacement("$"));
    }

    public static List<String> transform(List<String> metrics) {
        return Objects.requireNonNull(metrics).stream().map(MeasurementUtils::transform).collect(Collectors.toList());
    }

    /**
     * A$B -> A.B
     *
     * @param measurement
     *            iotdb测点
     * @return 度量
     */
    public static String untransform(String measurement) {
        return Objects.requireNonNull(measurement).replaceAll("\\$", ".");
    }

    /**
     * first_value(DFES_numDFC_[0]) -> DFES_numDFC_[0]
     * <p>
     * median(DFES_numDFC_[0], "position"="left") -> DFES_numDFC_[0].
     *
     * @param columnName
     *            iotdb return column name
     * @return measurement
     */
    public static String columnNameToMeasurement(String columnName) {
        Matcher matcher = FUNCTION_PATTERN.matcher(removeArgs(columnName));
        if (matcher.find() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return columnName;
    }

    /**
     * first_value(root.xxx.deviceId.DFES.numDFC.[0]) -> DFES.numDFC.[0]
     * <p>
     * median(root.xxx.deviceId.DFES.numDFC.[0], "position"="left") -> DFES.numDFC.[0].
     *
     * @param rawColumnName
     *            iotdb return column name
     * @param rawColumnName
     *            device id
     * @return measurement
     */
    public static String columnNameRemoveDeviceId(String rawColumnName, String deviceId) {
        String columnName = columnNameToMeasurement(rawColumnName);
        int index = columnName.indexOf(deviceId);
        return index == -1 ? columnName
            : columnName.substring(index).replace(deviceId + SipaBootCommonConstants.POINT, "");
    }

    /**
     * median(DFES_numDFC_[0], "position"="left") -> median(DFES_numDFC_[0]).
     *
     * @param columnName
     *            iotdb return column name
     * @return remove args column name
     */
    private static String removeArgs(String columnName) {
        if (columnName.contains(SipaBootCommonConstants.COMMA)) {
            return columnName.split(SipaBootCommonConstants.COMMA)[0] + SipaBootCommonConstants.RIGHT_BRACKET;
        }
        return columnName;
    }
}
