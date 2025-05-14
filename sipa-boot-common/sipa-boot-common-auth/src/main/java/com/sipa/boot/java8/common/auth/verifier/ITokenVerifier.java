package com.sipa.boot.java8.common.auth.verifier;

/**
 * token验证接口.
 *
 * @author zhouxiajie
 * @date 2018/2/6
 */
public interface ITokenVerifier {
    /**
     * 验证jti是否正确.
     *
     * @param jti
     *            java token interface.
     * @return 是否正确
     */
    boolean verify(String jti);
}
