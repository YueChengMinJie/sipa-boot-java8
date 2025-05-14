package com.sipa.boot.java8.common.services;

/**
 * @author zhouxiajie
 * @date 2019-02-02
 */
public interface IMessageService {
    String getMessage(String code);

    String getMessage(String code, String[] param);

    String getMessageAlwaysReturn(String code);

    String getMessageAlwaysReturn(String code, String[] param);
}
