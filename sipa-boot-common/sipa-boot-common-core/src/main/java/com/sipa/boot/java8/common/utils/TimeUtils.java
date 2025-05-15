package com.sipa.boot.java8.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author sunyukun
 * @since 2019/5/9 13:23
 */
public class TimeUtils {
    // ********************************************************
    // ************************ format ************************
    // ********************************************************
    /**
     * local date time to format date.
     *
     * @param ldt
     *            local date time
     * @return format date
     */
    public static String format(LocalDateTime ldt) {
        return format(parse(ldt), SipaBootCommonConstants.TimeFormatKey.DEFAULT);
    }

    /**
     * local date time to format date with format.
     *
     * @param ldt
     *            local date time
     * @param format
     *            format
     * @return format date
     */
    public static String format(LocalDateTime ldt, String format) {
        return format(parse(ldt), format);
    }

    /**
     * date to format date.
     *
     * @param date
     *            date
     * @return format date
     */
    public static String format(Date date) {
        return format(date, SipaBootCommonConstants.TimeFormatKey.DEFAULT);
    }

    /**
     * date to format date.
     *
     * @param date
     *            date
     * @return format date
     */
    public static String formatWithMilliseconds(Date date) {
        return format(date, SipaBootCommonConstants.TimeFormatKey.DEFAULT_WITH_MILS);
    }

    /**
     * date to format date with format.
     *
     * @param date
     *            date
     * @param format
     *            format
     * @return format date
     */
    public static String format(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    // ********************************************************
    // ************************ parse *************************
    // ********************************************************

    /**
     * format date to date with format.
     *
     * @param date
     *            string date
     * @param format
     *            format
     * @return date
     */
    public static Date parse(String date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * local date time to date.
     *
     * @param ldt
     *            local date time
     * @return date
     */
    public static Date parse(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    // ********************************************************
    // ********************** ts utils ************************
    // ********************************************************

    /**
     * @return 当前毫秒数
     */
    public static long nowMs() {
        return System.currentTimeMillis();
    }

    /**
     * timestamp to format date.
     *
     * @param ts
     *            timestamp
     * @return format date
     */
    public static String ts2fd(Long ts) {
        Date date = new Date(ts);
        return TimeUtils.format(date, SipaBootCommonConstants.TimeFormatKey.DEFAULT_WITH_MILS);
    }

    /**
     * timestamp to format date with format.
     *
     * @param ts
     *            timestamp
     * @param format
     *            format
     * @return format date
     */
    public static String ts2fd(Long ts, String format) {
        Date date = new Date(ts);
        return TimeUtils.format(date, format);
    }

    /**
     * ts to local date.
     *
     * @param ts
     *            timestamp
     * @return local date
     */
    public static LocalDate ts2ld(Long ts) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * ts to local date string.
     *
     * @param ts
     *            timestamp
     * @return local date string
     */
    public static String ts2lds(Long ts) {
        return ts2ld(ts).toString();
    }

    /**
     * ts to local date time.
     *
     * @param ts
     *            timestamp
     * @return local date time
     */
    public static LocalDateTime ts2ldt(Long ts) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
    }

    /**
     * format date time to local date time.
     *
     * @param fdt
     *            format date time
     * @return local date time
     */
    public static LocalDateTime fdt2ldt(String fdt) {
        return LocalDateTime.parse(fdt, SipaBootCommonConstants.LocalDateTimeFormatter.DEFAULT);
    }

    /**
     * ts to zoned date time, for scala use.
     *
     * @param ts
     *            timestamp
     * @return zoned date time
     */
    public static ZonedDateTime ts2zdt(Long ts) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
    }

    /**
     * timestamp to date.
     *
     * @param ts
     *            timestamp
     * @return date
     */
    public static Date ts2d(Long ts) {
        return new Date(ts);
    }

    // ********************************************************
    // ********************* ldt utils ************************
    // ********************************************************

    /**
     * local date time to timestamp.
     *
     * @param ldt
     *            local date time
     * @return timestamp
     */
    public static Long ldt2ts(LocalDateTime ldt) {
        return ldt.toInstant(OffsetDateTime.now(ZoneId.systemDefault()).getOffset()).toEpochMilli();
    }

    /**
     * local date time to date.
     *
     * @param ldt
     *            local date time
     * @return date
     */
    public static Date ldt2d(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    // ********************************************************
    // ******************* time operation *********************
    // ********************************************************

    /**
     * add day to date.
     *
     * @param date
     *            date
     * @param day
     *            day of month
     * @return new date
     */
    public static Date addDay(Date date, int day) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DAY_OF_MONTH, day);
        return ca.getTime();
    }

    // ********************************************************
    // ********************* time other ***********************
    // ********************************************************

    /**
     * get second timestamp.
     *
     * @param ts
     *            timestamp
     * @return second timestamp
     */
    public static Long stripMillisecond(Long ts) {
        if (Objects.isNull(ts)) {
            return 0L;
        } else {
            String timestamp = String.valueOf(ts);
            int length = timestamp.length();
            if (length > SipaBootCommonConstants.Number.INT_3) {
                return Long.valueOf(timestamp.substring(0, length - 3));
            } else {
                return 0L;
            }
        }
    }

    /**
     * from start to end, split by day.
     *
     * @param startTime
     *            start time
     * @param endTime
     *            end time
     * @param day
     *            day
     * @return dateRanges
     */
    public static List<List<LocalDateTime>> splitLdtByDay(LocalDateTime startTime, LocalDateTime endTime, long day) {
        List<List<LocalDateTime>> dayList = new ArrayList<>();
        if (day > 0) {
            Duration between = Duration.between(startTime, endTime);
            long betweenDay = between.toDays();
            if (betweenDay > 0) {
                for (long i = 0; i <= betweenDay; i = i + day) {
                    List<LocalDateTime> list = new ArrayList<>();

                    LocalDateTime startDateTime;
                    if (i == 0) {
                        startDateTime = startTime;
                    } else {
                        startDateTime = LocalDateTime.of(startTime.toLocalDate(), LocalTime.MIN).plusDays(i);
                    }
                    list.add(startDateTime);

                    LocalDateTime endDateTime = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.MAX);
                    if (endDateTime.isAfter(endTime)) {
                        endDateTime = endTime;
                    }
                    list.add(endDateTime);

                    dayList.add(list);
                }
            } else {
                List<LocalDateTime> list = new ArrayList<>();
                list.add(startTime);
                list.add(endTime);
                dayList.add(list);
            }
        }
        return dayList;
    }

    /**
     * from start to end, get seconds
     *
     * @param start
     *            start
     * @param end
     *            end
     * @return seconds
     */
    public static Double getSeconds(long start, long end) {
        return end - start / 1000.0;
    }

    /**
     * 判断2个时间段是否有重叠（交集）
     *
     * @param startDate1
     *            时间段1开始时间戳
     * @param endDate1
     *            时间段1结束时间戳
     * @param startDate2
     *            时间段2开始时间戳
     * @param endDate2
     *            时间段2结束时间戳
     * @param isStrict
     *            是否严格重叠，true 严格，没有任何相交或相等；false 不严格，可以首尾相等，比如2021/5/29-2021/5/31和2021/5/31-2021/6/1，不重叠。
     * @return 返回是否重叠
     */
    public static boolean isOverlap(long startDate1, long endDate1, long startDate2, long endDate2, boolean isStrict) {
        if (endDate1 < startDate1) {
            throw new DateTimeException("endDate1不能小于startDate1");
        }
        if (endDate2 < startDate2) {
            throw new DateTimeException("endDate2不能小于startDate2");
        }
        if (isStrict) {
            return !(endDate1 < startDate2 || startDate1 > endDate2);
        } else {
            return !(endDate1 <= startDate2 || startDate1 >= endDate2);
        }
    }

    /**
     * 判断2个时间段是否有重叠（交集）
     *
     * @param startDate1
     *            时间段1开始时间
     * @param endDate1
     *            时间段1结束时间
     * @param startDate2
     *            时间段2开始时间
     * @param endDate2
     *            时间段2结束时间
     * @param isStrict
     *            是否严格重叠，true 严格，没有任何相交或相等；false 不严格，可以首尾相等，比如2021-05-29到2021-05-31和2021-05-31到2021-06-01，不重叠。
     * @return 返回是否重叠
     */
    public static boolean isTimeOverlap(Long startDate1, Long endDate1, Long startDate2, Long endDate2,
        boolean isStrict) {
        Objects.requireNonNull(startDate1, "startDate1");
        Objects.requireNonNull(endDate1, "endDate1");
        Objects.requireNonNull(startDate2, "startDate2");
        Objects.requireNonNull(endDate2, "endDate2");
        return isOverlap(startDate1, endDate1, startDate2, endDate2, isStrict);
    }

    /**
     * 判断时间是否相等.
     *
     * @param ldt1
     *            时间1
     * @param ldt2
     *            时间2
     * @return 是否相等
     */
    public static boolean equals(LocalDateTime ldt1, LocalDateTime ldt2) {
        return Objects.nonNull(ldt1) ? ldt1.equals(ldt2) : Objects.isNull(ldt2);
    }
}
