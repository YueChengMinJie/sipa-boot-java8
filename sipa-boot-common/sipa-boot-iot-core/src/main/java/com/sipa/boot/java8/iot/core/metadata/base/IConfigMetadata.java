package com.sipa.boot.java8.iot.core.metadata.base;

import java.io.Serializable;
import java.util.List;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IConfigMetadata extends IConfigScopeSupport, Serializable {
    /**
     * @return 配置名称
     */
    String getName();

    /**
     * @return 配置说明
     */
    String getDescription();

    /**
     * @return 配置属性信息
     */
    List<IConfigPropertyMetadata> getProperties();

    /**
     * 复制为新的配置,并按指定的scope过滤属性,只返回符合scope的属性.
     *
     * @param scopes
     *            范围
     * @return 新的配置
     */
    IConfigMetadata copy(IConfigScope... scopes);
}
