package com.sipa.boot.java8.data.iotdb.sql;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.TimeUtils;

/**
 * Date 2021/7/28
 *
 * @author shenxuanyu
 * @since 2.0.0
 */
public class GroupBuilder extends SelectBuilder {
    String DATE_RANGE_GROUP_PATTERN = "([{0}, {1}), {2})";

    public GroupBuilder() {
        super();
    }

    public GroupBuilder(String table) {
        super(table);
    }

    public GroupBuilder table(String table) {
        super.table(table);
        return this;
    }

    public GroupBuilder groupBy(LocalDateTime from, LocalDateTime to, String interval) {
        super.groupBy(MessageFormat.format(DATE_RANGE_GROUP_PATTERN, format(from), format(to), interval));
        return this;
    }

    public GroupBuilder groupBy(LocalDateTime from, LocalDateTime to, Long intervalSecond) {
        super.groupBy("");
        return this;
    }

    public GroupBuilder column(String name) {
        super.column(name);
        return this;
    }

    public GroupBuilder column(List<String> names) {
        for (String name : names) {
            column(name);
        }
        return this;
    }

    public GroupBuilder count(String measurement) {
        super.column("count(" + measurement + ")");
        return this;
    }

    public GroupBuilder avg(String measurement) {
        super.column("AVG (" + measurement + ")");
        return this;
    }

    public GroupBuilder first(String measurement) {
        super.column("FIRST_VALUE (" + measurement + ")");
        return this;
    }

    public GroupBuilder avg(List<String> measurements) {
        for (String measurement : measurements) {
            this.avg(measurement);
        }
        return this;
    }

    public GroupBuilder count(List<String> measurements) {
        for (String measurement : measurements) {
            super.column("count(" + measurement + ")");
        }
        return this;
    }

    public GroupBuilder dateRange(LocalDateTime from, LocalDateTime to) {
        this.timeFrom(from);
        this.timeTo(to);
        return this;
    }

    public GroupBuilder timeFrom(LocalDateTime from) {
        super.where("time >= " + format(from));
        return this;
    }

    public GroupBuilder timeTo(LocalDateTime to) {
        super.where("time <= " + format(to));
        return this;
    }

    String format(LocalDateTime start) {
        return TimeUtils.format(start, SipaBootCommonConstants.TimeFormatKey.DEFAULT_WITH_POINT_MILS);
    }
}
