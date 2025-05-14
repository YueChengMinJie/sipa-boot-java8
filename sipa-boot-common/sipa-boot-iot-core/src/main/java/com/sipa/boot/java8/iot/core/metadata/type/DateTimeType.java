package com.sipa.boot.java8.iot.core.metadata.type;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.StringUtils;
import com.sipa.boot.java8.iot.core.metadata.ValidateResult;
import com.sipa.boot.java8.iot.core.metadata.base.IConverter;
import com.sipa.boot.java8.iot.core.metadata.base.IDataType;
import com.sipa.boot.java8.iot.core.metadata.type.base.AbstractType;
import com.sipa.boot.java8.iot.core.time.IsoDateTimeFormatter;
import com.sipa.boot.java8.iot.core.time.base.IDateFormatter;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class DateTimeType extends AbstractType<DateTimeType> implements IDataType, IConverter<Date> {
    private static final Log log = LogFactory.get(DateTimeType.class);

    public static final String ID = "date";

    public static final String TIMESTAMP_FORMAT = "timestamp";

    public static final DateTimeType GLOBAL = new DateTimeType();

    private String format = TIMESTAMP_FORMAT;

    private ZoneId zoneId = ZoneId.systemDefault();

    private DateTimeFormatter formatter;

    static {
        IDateFormatter.SUPPORT_FORMATTER.add(new IsoDateTimeFormatter());
    }

    public DateTimeType timeZone(ZoneId zoneId) {
        this.zoneId = zoneId;

        return this;
    }

    public DateTimeType format(String format) {
        this.format = format;
        this.getFormatter();
        return this;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "时间";
    }

    protected DateTimeFormatter getFormatter() {
        if (formatter == null && !TIMESTAMP_FORMAT.equals(format)) {
            formatter = DateTimeFormatter.ofPattern(format);
        }
        return formatter;
    }

    @Override
    public ValidateResult validate(Object value) {
        if ((value = convert(value)) == null) {
            return ValidateResult.fail("不是合法的时间格式");
        }
        return ValidateResult.success(value);
    }

    @Override
    public String format(Object value) {
        try {
            if (TIMESTAMP_FORMAT.equals(format)) {
                return String.valueOf(convert(value).getTime());
            }
            Date dateValue = convert(value);
            if (dateValue == null) {
                return "";
            }
            return LocalDateTime.ofInstant(dateValue.toInstant(), zoneId).format(getFormatter());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return "";
    }

    public Date convert(Object value) {
        if (value instanceof Instant) {
            return Date.from(((Instant)value));
        }
        if (value instanceof LocalDateTime) {
            return Date.from(((LocalDateTime)value).atZone(zoneId).toInstant());
        }

        if (value instanceof Date) {
            return ((Date)value);
        }
        if (value instanceof Number) {
            return new Date(((Number)value).longValue());
        }
        if (value instanceof String) {
            if (StringUtils.isNumber(value)) {
                return new Date(Long.parseLong((String)value));
            }
            Date data = IDateFormatter.fromString(((String)value));
            if (data != null) {
                return data;
            }
            DateTimeFormatter formatter = getFormatter();
            if (null == formatter) {
                throw new IllegalArgumentException("unsupported date format:" + value);
            }
            return Date.from(LocalDateTime.parse(((String)value), formatter).atZone(zoneId).toInstant());
        }
        throw new IllegalArgumentException("can not format datetime :" + value);
    }
}
