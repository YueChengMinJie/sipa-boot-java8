package com.sipa.boot.java8.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sipa.boot.java8.common.archs.jackson.deseriallzer.EnumDeserializer;
import com.sipa.boot.java8.common.archs.jackson.serializer.EnumSerializer;
import com.sipa.boot.java8.common.archs.jackson.serializer.StringTrimSerializer;
import com.sipa.boot.java8.common.archs.snowflake.IUidGenerator;
import com.sipa.boot.java8.common.archs.snowflake.SnowflakeUidGenerator;

/**
 * @author sunyukun
 * @date 2019/3/19
 */
@Configuration
@ComponentScan(value = {"com.sipa.boot.java8.**.common.utils", "com.sipa.boot.java8.**.common.processors"})
public class CommonAutoConfiguration {
    @Bean
    public IUidGenerator snowflakeUidGenerator() {
        return new SnowflakeUidGenerator();
    }

    @Bean
    @Primary
    public ObjectMapper getObjectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        // 又改回来了，给前端一个教训，养成判断空和解构去空的习惯
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(String.class, StringTrimSerializer.instance);
        simpleModule.addSerializer(Enum.class, EnumSerializer.instance);

        simpleModule.addDeserializer(Enum.class, EnumDeserializer.instance);

        ObjectMapper objectMapper = builder.build();

        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);

        return objectMapper;
    }
}
