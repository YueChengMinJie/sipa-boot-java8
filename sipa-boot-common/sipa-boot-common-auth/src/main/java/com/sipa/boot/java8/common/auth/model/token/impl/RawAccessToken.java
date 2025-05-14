package com.sipa.boot.java8.common.auth.model.token.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sipa.boot.java8.common.auth.exception.InvalidJwtException;
import com.sipa.boot.java8.common.auth.exception.SipaBootExpiredJwtException;
import com.sipa.boot.java8.common.auth.model.token.JwtToken;

import io.jsonwebtoken.*;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
public class RawAccessToken implements JwtToken {
    private static Logger logger = LoggerFactory.getLogger(RawAccessToken.class);

    private String token;

    public RawAccessToken(String token) {
        this.token = token;
    }

    /**
     * Parses and validates JWT Token signature.
     */
    public Jws<Claims> parseClaims(String signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            logger.error("Invalid JWT Token");
            throw new InvalidJwtException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            logger.debug("JWT Token is expired");
            throw new SipaBootExpiredJwtException(this, "JWT Token expired", expiredEx);
        }
    }

    @Override
    public String getToken() {
        return token;
    }
}
