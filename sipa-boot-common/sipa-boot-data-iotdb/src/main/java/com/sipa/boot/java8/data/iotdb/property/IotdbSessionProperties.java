package com.sipa.boot.java8.data.iotdb.property;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbSessionProperties {
    /**
     * cpu logic cores - 1
     */
    private int maxSize = SipaBootCommonConstants.Number.INT_7;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
