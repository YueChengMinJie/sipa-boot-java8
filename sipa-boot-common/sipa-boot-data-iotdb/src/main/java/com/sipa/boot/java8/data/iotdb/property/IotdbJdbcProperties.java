package com.sipa.boot.java8.data.iotdb.property;

import java.util.concurrent.TimeUnit;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbJdbcProperties {
    private long clientQueryTimeoutMs = TimeUnit.MINUTES.toMillis(SipaBootCommonConstants.Number.INT_5);

    public long getClientQueryTimeoutMs() {
        return clientQueryTimeoutMs;
    }

    public void setClientQueryTimeoutMs(long clientQueryTimeoutMs) {
        this.clientQueryTimeoutMs = clientQueryTimeoutMs;
    }
}
