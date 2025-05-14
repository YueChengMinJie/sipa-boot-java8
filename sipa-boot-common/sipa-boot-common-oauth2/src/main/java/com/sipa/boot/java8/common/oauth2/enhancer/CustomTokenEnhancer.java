package com.sipa.boot.java8.common.oauth2.enhancer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.oauth2.entity.SipaBootUser;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
public class CustomTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        SipaBootUser user = (SipaBootUser)authentication.getPrincipal();

        final Map<String, Object> additionalInfo = new HashMap<>(16);

        additionalInfo.put(SipaBootCommonConstants.SIPA_BOOT_USER_ID_KEY, user.getId());

        additionalInfo.put(SipaBootCommonConstants.SIPA_BOOT_TENANT_ID_KEY, user.getTenantId());

        additionalInfo.put(SipaBootCommonConstants.SIPA_BOOT_SEQUENCE_KEY, user.getSequence());

        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}
