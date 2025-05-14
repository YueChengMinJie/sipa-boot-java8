package com.sipa.boot.java8.common.aop.base;

/**
 * @author 甘华根
 * @since 2020/7/31 15:01
 */
public interface ISameUser {
    /**
     * 是否为同一用户在当前模块.
     *
     * @param module
     *            模块名
     * @param moduleId
     *            模块id
     * @return 是否是同一用户
     */
    boolean checkAuth(String module, String moduleId);
}
