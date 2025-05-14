package com.sipa.boot.java8.data.iotdb.sql.base;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.sipa.boot.java8.common.utils.CheckUtils;
import com.sipa.boot.java8.common.utils.TimeUtils;
import com.sipa.boot.java8.common.utils.Utils;
import com.sipa.boot.java8.data.iotdb.sql.SelectBuilder;
import com.sipa.boot.java8.data.iotdb.value.tsdb.ETsdbDownSample;
import com.sipa.boot.java8.data.iotdb.value.tsdb.TsdbQuery;

/**
 * @author caszhou
 * @date 2021/6/26
 */
public interface ISqlBuilder {
    String MEDIAN_PATTERN = "median({0}, \"position\"=\"left\")";

    String FIRST_PATTERN = "first_value({0})";

    String DATE_RANGE_GROUP_PATTERN = "([{0}, {1}), {2})";

    // ********************************************************
    // ************************* main *************************
    // ********************************************************

    default SelectBuilder getSelectBuilder(TsdbQuery query) {
        check(query);
        return doGetSelectBuilder(query);
    }

    SelectBuilder doGetSelectBuilder(TsdbQuery tsdbQuery);

    // ********************************************************
    // ************************ utils *************************
    // ********************************************************

    default void check(TsdbQuery query) {
        Objects.requireNonNull(query, "TsdbQuery cannot be null.");
        Objects.requireNonNull(query.getStart(), "TsdbQuery start cannot be null.");
        Objects.requireNonNull(query.getEnd(), "TsdbQuery end cannot be null.");
        CheckUtils.requireNonNull(query.getMetrics(), "TsdbQuery metrics cannot be empty.");
        Objects.requireNonNull(query.getCollectionId(), "TsdbQuery collectionId cannot be null.");
        Objects.requireNonNull(query.getDownSample(), "TsdbQuery downSample cannot be null.");
    }

    default String timeEnd(LocalDateTime end) {
        return "time <= " + format(end);
    }

    default String timeStart(LocalDateTime start) {
        return "time >= " + format(start);
    }

    default String dateRangeGroup(LocalDateTime start, LocalDateTime end, ETsdbDownSample downSample) {
        return MessageFormat.format(DATE_RANGE_GROUP_PATTERN, format(start), format(end), slideTime(downSample));
    }

    default String format(LocalDateTime ldt) {
        return Utils.stringValueOf(TimeUtils.ldt2ts(ldt));
    }

    default String slideTime(ETsdbDownSample downSample) {
        if (downSample == ETsdbDownSample.S_MEDIAN) {
            return "1s";
        } else if (downSample == ETsdbDownSample.M_MEDIAN) {
            return "1m";
        } else if (downSample == ETsdbDownSample.D_FIRST) {
            return "1d";
        }
        throw new RuntimeException("Group by now only support second, minute and day.");
    }

    default List<String> firstAgg(List<String> metrics) {
        return metrics.stream().map(metric -> MessageFormat.format(FIRST_PATTERN, metric)).collect(Collectors.toList());
    }

    default List<String> medianAgg(List<String> metrics) {
        return metrics.stream()
            .map(metric -> MessageFormat.format(MEDIAN_PATTERN, metric))
            .collect(Collectors.toList());
    }
}
