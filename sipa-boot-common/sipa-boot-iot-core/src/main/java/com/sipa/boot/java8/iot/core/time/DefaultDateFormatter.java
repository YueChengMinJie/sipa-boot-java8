package com.sipa.boot.java8.iot.core.time;

import java.util.Date;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.sipa.boot.java8.iot.core.time.base.IDateFormatter;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class DefaultDateFormatter implements IDateFormatter {
    private final DateTimeFormatter formatter;

    private final Predicate<String> predicate;

    private String formatterString;

    public DefaultDateFormatter(Pattern formatPattern, String formatter) {
        this(str -> formatPattern.matcher(str).matches(), DateTimeFormat.forPattern(formatter));
        this.formatterString = formatter;
    }

    public DefaultDateFormatter(Predicate<String> predicate, DateTimeFormatter formatter) {
        this.predicate = predicate;
        this.formatter = formatter;
    }

    @Override
    public boolean support(String str) {
        return predicate.test(str);
    }

    @Override
    public Date format(String str) {
        return formatter.parseDateTime(str).toDate();
    }

    @Override
    public String getPattern() {
        return formatterString;
    }

    @Override
    public String toString(Date date) {
        return new DateTime(date).toString(getPattern());
    }
}
