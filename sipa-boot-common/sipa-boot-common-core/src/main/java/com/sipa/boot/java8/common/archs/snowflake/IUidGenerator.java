package com.sipa.boot.java8.common.archs.snowflake;

/**
 * @author feizhihao
 * @date 2019-05-13 20:01
 */
public interface IUidGenerator {
    Long nextLid();

    String nextSid();
}
