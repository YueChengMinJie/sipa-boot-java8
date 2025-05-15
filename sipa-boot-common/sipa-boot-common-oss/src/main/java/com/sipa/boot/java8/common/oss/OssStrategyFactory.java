package com.sipa.boot.java8.common.oss;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.oss.annotation.OssStrategy;
import com.sipa.boot.java8.common.oss.property.OssProperties;
import com.sipa.boot.java8.common.oss.strategy.AliyunOssStrategy;
import com.sipa.boot.java8.common.oss.strategy.IOssStrategy;

/**
 * @author feizhihao
 * @date 2019-08-20 11:35
 */
public class OssStrategyFactory {
    private static final Log LOGGER = LogFactory.get(OssStrategyFactory.class);

    private static final OssStrategyFactory factory = new OssStrategyFactory();

    private final List<Class<? extends IOssStrategy>> strategyList = new ArrayList<>();

    private OssStrategyFactory() {
        strategyList.add(AliyunOssStrategy.class);
        // strategyList.add(MinioOssStrategy.class);
    }

    public IOssStrategy getOssStrategy(OssProperties ossProperties) {
        if (StringUtils.isBlank(ossProperties.getStrategyType())) {
            throw new RuntimeException("Strategy type is empty.");
        }
        if (StringUtils.isBlank(ossProperties.getEndpoint())) {
            throw new RuntimeException("Endpoint is empty.");
        }
        if (StringUtils.isBlank(ossProperties.getAccessKey())) {
            throw new RuntimeException("Access key is empty.");
        }
        if (StringUtils.isBlank(ossProperties.getSecretKey())) {
            throw new RuntimeException("Secret key is empty.");
        }

        try {
            for (Class<? extends IOssStrategy> clazz : strategyList) {
                OssStrategy ossStrategy = clazz.getAnnotation(OssStrategy.class);
                if (ossProperties.getStrategyType().equals(ossStrategy.value())) {
                    Constructor<?> ct = clazz.getDeclaredConstructor(OssProperties.class);
                    ct.setAccessible(true);
                    LOGGER.info("Init class [{}]", clazz.getName());
                    return (IOssStrategy)ct.newInstance(new Object[] {ossProperties});
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Can not find oss strategy.");
    }

    public static OssStrategyFactory getInstance() {
        return factory;
    }
}
