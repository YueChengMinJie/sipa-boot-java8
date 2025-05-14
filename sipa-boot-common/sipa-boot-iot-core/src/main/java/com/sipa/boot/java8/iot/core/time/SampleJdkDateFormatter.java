package com.sipa.boot.java8.iot.core.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.joda.time.DateTime;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.time.base.IDateFormatter;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class SampleJdkDateFormatter implements IDateFormatter {
    private static final Log LOGGER = LogFactory.get(SampleJdkDateFormatter.class);

    private final Predicate<String> predicate;

    private final Supplier<SimpleDateFormat> formatSupplier;

    public SampleJdkDateFormatter(Predicate<String> predicate, Supplier<SimpleDateFormat> formatSupplier) {
        this.predicate = predicate;
        this.formatSupplier = formatSupplier;
    }

    @Override
    public boolean support(String str) {
        return predicate.test(str);
    }

    @Override
    public Date format(String str) {
        try {
            return formatSupplier.get().parse(str);
        } catch (ParseException e) {
            LOGGER.error(e);
            return null;
        }
    }

    @Override
    public String getPattern() {
        return formatSupplier.get().toPattern();
    }

    @Override
    public String toString(Date date) {
        return new DateTime(date).toString(getPattern());
    }
}
