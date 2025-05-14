package com.sipa.boot.java8.common.mvc.orika;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * @author feizhihao
 * @date 2019-04-08
 */
public final class OrikaMapperFactoryBean implements FactoryBean<MapperFacade> {
    private final List<MappingConfigurer> configurers;

    public OrikaMapperFactoryBean(List<MappingConfigurer> configurers) {
        super();
        this.configurers = configurers;
    }

    @Override
    public MapperFacade getObject() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        for (MappingConfigurer configurer : configurers) {
            configurer.configure(mapperFactory);
        }

        return mapperFactory.getMapperFacade();
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFacade.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
