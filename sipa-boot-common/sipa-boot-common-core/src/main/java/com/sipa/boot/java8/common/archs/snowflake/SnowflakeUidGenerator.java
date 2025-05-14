package com.sipa.boot.java8.common.archs.snowflake;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author feizhihao
 * @date 2019-05-13 19:59
 */
public class SnowflakeUidGenerator implements IUidGenerator {
    public static final Log logger = LogFactory.get(SnowflakeUidGenerator.class);

    private final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    @Override
    public Long nextLid() {
        return snowflakeIdWorker.nextId();
    }

    @Override
    public String nextSid() {
        return String.valueOf(nextLid());
    }
}
