package com.sipa.boot.java8.iot.core.metadata.base;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public interface IJsonable {
    default JSONObject toJson() {
        return FastBeanCopier.copy(this, JSONObject::new);
    }

    default void fromJson(JSONObject json) {
        FastBeanCopier.copy(json, this);
    }
}
