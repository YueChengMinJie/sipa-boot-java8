package com.sipa.boot.java8.common.auth.verifier;

import org.springframework.stereotype.Component;

/**
 * token验证器.
 *
 * @author zhouxiajie
 * @date 2018/2/6
 */
@Component
public class JwtVerifier implements ITokenVerifier {
    @Override
    public boolean verify(String jti) {
        return true;
    }
}
