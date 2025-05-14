package com.sipa.boot.java8.common.utils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sunyukun compute tools
 */
public class MathUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(MathUtils.class);

    private static final double ZERO_POINT_ZERO_FIVE = 0.05;

    /**
     * logarithm
     *
     * @param base
     *            base param
     * @param antilogarithm
     *            antilogarithm
     * @return double
     */
    public static double logarithms(double base, double antilogarithm) {
        if (base == 0 || antilogarithm == 0 || base == 1) {
            return 0;
        } else {
            return Math.log(antilogarithm) / Math.log(base);
        }
    }

    /**
     * Average
     *
     * @param number
     *            number
     * @return double
     */
    public static double averageDouble(double... number) {
        if (number.length == 0) {
            return 0;
        } else {
            BigDecimal total = new BigDecimal(0);
            for (double value : number) {
                total = total.add(new BigDecimal(value));
            }
            return total.divide(new BigDecimal(number.length), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }

    /**
     * Average
     *
     * @param number
     *            number
     * @return BigDecimal
     */
    public static BigDecimal average(BigDecimal... number) {
        if (number.length == 0) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal total = new BigDecimal(0);
            for (BigDecimal value : number) {
                total = total.add(value);
            }
            return total.divide(new BigDecimal(number.length), 4, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * round float
     *
     * @param number
     *            number
     * @param decimalPlaces
     *            decimal places
     * @return float
     */
    public static float roundFloat(final float number, final int decimalPlaces) {
        float precision = 1.0F;
        for (int i = 0; i < decimalPlaces; i++) {
            precision *= 10;
        }
        return Math.round(number * precision) / precision;
    }

    /**
     * round float
     *
     * @param number
     *            number
     * @param decimalPlaces
     *            decimal places
     * @return float
     */
    public static double roundDouble(final double number, final int decimalPlaces) {
        double precision = 1.0d;
        for (int i = 0; i < decimalPlaces; i++) {
            precision *= 10;
        }

        return Math.round(number * precision) / precision;
    }

    /**
     * convert doubleNumber To String with specified format ROUND_HALF_UP in the first number behind decimal point clean
     * the decimal point if the last number is zero 2.25->2.3 2.24->2.2 2.0->2 2.03->2
     *
     * @param doubleNumber
     *            double number
     * @return result
     */
    public static String convertDoubleToString(Double doubleNumber) {
        BigDecimal bigDecimalTemp = new BigDecimal(doubleNumber);
        Double f1 = bigDecimalTemp.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Math.abs(f1 - f1.intValue()) < ZERO_POINT_ZERO_FIVE) {
            return String.valueOf(f1.intValue());
        }
        return f1.toString();
    }

    /**
     * double类型转 bigdecimal
     *
     * @param d
     *            double source
     * @return target BigDecimal
     */
    public static BigDecimal parseDoubleToBigDecimal(Double d) {
        try {
            if (Objects.nonNull(d)) {
                return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("can not parse [{}] of double to bigDecimal ", d);
        }
        return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 传入一个数列x计算方差 方差s^2=[（x1-x）^2+（x2-x）^2+......（xn-x）^2]/（n）（x为平均数）
     *
     * @param numbers
     *            要计算的数列
     * @return 方差
     */
    public static double variance(double[] numbers) {
        double n = numbers.length; // 数列元素个数
        double avg = averageDouble(numbers); // 求平均值
        double var = 0;
        for (double number : numbers) {
            var += (number - avg) * (number - avg);
        }
        return var / n;
    }

    /**
     * 传入一个数列x计算标准差 标准差σ=sqrt(s^2)，即标准差=方差的平方根
     *
     * @param numbers
     *            要计算的数列
     * @return 标准差
     */
    public static BigDecimal standardDiviation(List<Double> numbers) {
        if (CollectionUtils.isEmpty(numbers)) {
            return null;
        }
        return parseDoubleToBigDecimal(Math.sqrt(variance(numbers.stream().mapToDouble(i -> i).toArray()))).setScale(2,
            BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 中位数
     */
    public static BigDecimal median(List<Double> numbers) {
        if (CollectionUtils.isEmpty(numbers)) {
            return null;
        }
        Collections.sort(numbers);
        int size = numbers.size();
        if (size % 2 == 1) {
            return parseDoubleToBigDecimal(numbers.get((size - 1) / 2)).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return parseDoubleToBigDecimal((numbers.get(size / 2 - 1) + numbers.get(size / 2)) / 2).setScale(2,
                BigDecimal.ROUND_HALF_UP);
        }
    }
}
