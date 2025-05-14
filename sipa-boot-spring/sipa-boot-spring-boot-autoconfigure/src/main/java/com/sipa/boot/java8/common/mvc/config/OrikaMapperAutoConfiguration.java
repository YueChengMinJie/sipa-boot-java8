package com.sipa.boot.java8.common.mvc.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.mvc.orika.JodaTimeMappingConfigurer;
import com.sipa.boot.java8.common.mvc.orika.MappingConfigurer;
import com.sipa.boot.java8.common.mvc.orika.OrikaMapperFactoryBean;

import ma.glasnost.orika.MapperFacade;

/**
 * @author feizhihao
 * @date 2019-04-08
 */
@Configuration
@ConditionalOnClass({MapperFacade.class, MappingConfigurer.class})
public class OrikaMapperAutoConfiguration {
    private final List<MappingConfigurer> mappingConfigurers;

    public OrikaMapperAutoConfiguration(List<MappingConfigurer> mappingConfigurers) {
        this.mappingConfigurers = mappingConfigurers;
    }

    @Bean
    @ConditionalOnMissingBean(OrikaMapperFactoryBean.class)
    public OrikaMapperFactoryBean mapperFactoryBean() {
        featJodaTimeMappingConfigurer();

        return new OrikaMapperFactoryBean(mappingConfigurers);
    }

    private void featJodaTimeMappingConfigurer() {
        boolean hasJodaTimeMappingConfigurer = false;
        for (MappingConfigurer mappingConfigurer : mappingConfigurers) {
            if (mappingConfigurer.getClass().equals(JodaTimeMappingConfigurer.class)) {
                hasJodaTimeMappingConfigurer = true;
                break;
            }
        }

        if (!hasJodaTimeMappingConfigurer) {
            mappingConfigurers.add(new JodaTimeMappingConfigurer());
        }
    }
}
