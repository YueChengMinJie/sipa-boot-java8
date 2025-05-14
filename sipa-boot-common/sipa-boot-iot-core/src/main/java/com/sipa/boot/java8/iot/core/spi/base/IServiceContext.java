package com.sipa.boot.java8.iot.core.spi.base;

import java.util.List;
import java.util.Optional;

import com.sipa.boot.java8.iot.core.base.IValue;
import com.sipa.boot.java8.iot.core.config.base.IConfigKey;

/**
 * 服务上下文,用于从服务中获取其他服务(如获取spring容器中的bean),配置等操作.
 *
 * @author caszhou
 * @date 2021/10/2
 */
public interface IServiceContext {
    Optional<IValue> getConfig(IConfigKey<String> key);

    Optional<IValue> getConfig(String key);

    <T> Optional<T> getService(Class<T> service);

    <T> Optional<T> getService(String service);

    <T> List<T> getServices(Class<T> service);

    <T> List<T> getServices(String service);
}
