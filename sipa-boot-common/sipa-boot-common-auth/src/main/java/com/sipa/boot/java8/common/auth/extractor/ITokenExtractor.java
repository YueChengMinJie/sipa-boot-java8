package com.sipa.boot.java8.common.auth.extractor;

/**
 * Token抽取接口.
 *
 * @author zhouxiajie
 * @date 2018/2/6
 */
public interface ITokenExtractor {
    /**
     * 从POST的payload抽取token.
     *
     * @param payload
     *            载荷
     * @return token
     */
    String extract(String payload);
}
