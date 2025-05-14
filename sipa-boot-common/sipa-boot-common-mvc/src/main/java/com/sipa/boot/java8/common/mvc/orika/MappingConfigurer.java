package com.sipa.boot.java8.common.mvc.orika;

import ma.glasnost.orika.MapperFactory;

/**
 * @author feizhihao
 * @date 2019-04-08
 */
public interface MappingConfigurer {
    /**
     * use MapperFactory to config
     *
     * @param factory
     *            MapperFactory
     */
    void configure(MapperFactory factory);
}
