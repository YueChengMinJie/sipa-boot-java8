package com.sipa.boot.java8.iot.core.dict.base;

import java.io.Serializable;
import java.util.List;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public interface IDictDefine extends Serializable {
    String getId();

    String getAlias();

    String getComments();

    List<? extends IEnumDict<?>> getItems();
}
