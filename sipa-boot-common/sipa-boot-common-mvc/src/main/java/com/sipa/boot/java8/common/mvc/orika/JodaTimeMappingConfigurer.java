package com.sipa.boot.java8.common.mvc.orika;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;

/**
 * @author feizhihao
 * @date 2019-04-08
 */
public final class JodaTimeMappingConfigurer implements MappingConfigurer {
    @Override
    public void configure(MapperFactory factory) {
        // configure iso datetime
        factory.getConverterFactory().registerConverter(new PassThroughConverter(LocalDateTime.class, LocalDate.class));
    }
}
