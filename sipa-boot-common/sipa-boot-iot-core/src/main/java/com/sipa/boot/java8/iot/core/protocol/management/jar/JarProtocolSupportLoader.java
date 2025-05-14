package com.sipa.boot.java8.iot.core.protocol.management.jar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.protocol.base.IProtocolSupport;
import com.sipa.boot.java8.iot.core.protocol.management.ProtocolSupportDefinition;
import com.sipa.boot.java8.iot.core.protocol.management.base.IProtocolSupportLoaderProvider;
import com.sipa.boot.java8.iot.core.spi.base.IProtocolSupportProvider;
import com.sipa.boot.java8.iot.core.spi.base.IServiceContext;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public class JarProtocolSupportLoader implements IProtocolSupportLoaderProvider {
    private static final Log log = LogFactory.get(JarProtocolSupportLoader.class);

    private IServiceContext serviceContext;

    private final Map<String, ProtocolClassLoader> protocolLoaders = new ConcurrentHashMap<>();

    private final Map<String, IProtocolSupportProvider> loaded = new ConcurrentHashMap<>();

    @Override
    public String getProvider() {
        return "jar";
    }

    protected ProtocolClassLoader createClassLoader(URL location) {
        return new ProtocolClassLoader(new URL[] {location}, this.getClass().getClassLoader());
    }

    protected void closeAll() {
        protocolLoaders.values().forEach(this::closeLoader);
        protocolLoaders.clear();
        loaded.clear();
    }

    protected void closeLoader(ProtocolClassLoader loader) {
        try {
            loader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<? extends IProtocolSupport> load(ProtocolSupportDefinition definition) {
        return Mono.defer(() -> {
            try {
                Map<String, Object> config = definition.getConfiguration();
                String location = Optional.ofNullable(config.get("location"))
                    .map(String::valueOf)
                    .orElseThrow(() -> new IllegalArgumentException("location"));

                String provider =
                    Optional.ofNullable(config.get("provider")).map(String::valueOf).map(String::trim).orElse(null);
                URL url;

                if (!location.contains("://")) {
                    url = new File(location).toURI().toURL();
                } else {
                    url = new URL("jar:" + location + "!/");
                }

                ProtocolClassLoader loader;
                URL fLocation = url;
                {
                    IProtocolSupportProvider oldProvider = loaded.remove(provider);
                    if (null != oldProvider) {
                        oldProvider.dispose();
                    }
                }
                loader = protocolLoaders.compute(definition.getId(), (key, old) -> {
                    if (null != old) {
                        try {
                            closeLoader(old);
                        } catch (Exception ignore) {
                        }
                    }
                    return createClassLoader(fLocation);
                });

                IProtocolSupportProvider supportProvider;
                log.debug("load protocol support from : {}", location);
                if (provider != null) {
                    supportProvider = (IProtocolSupportProvider)Class.forName(provider, true, loader).newInstance();
                } else {
                    supportProvider = ServiceLoader.load(IProtocolSupportProvider.class, loader).iterator().next();
                }
                IProtocolSupportProvider oldProvider = loaded.put(provider, supportProvider);
                try {
                    if (null != oldProvider) {
                        oldProvider.dispose();
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                return supportProvider.create(serviceContext);
            } catch (Throwable e) {
                return Mono.error(e);
            }
        }).subscribeOn(Schedulers.elastic());
    }

    public void setServiceContext(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }
}
