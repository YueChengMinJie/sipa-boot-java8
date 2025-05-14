package com.sipa.boot.java8.common.oauth2.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.oauth2.enhancer.CustomTokenEnhancer;
import com.sipa.boot.java8.common.oauth2.enumerate.ELoginStrategy;
import com.sipa.boot.java8.common.oauth2.property.AuthorizationServerProperties;
import com.sipa.boot.java8.common.oauth2.store.SipaBootRedisTokenStore;
import com.sipa.boot.java8.common.oauth2.translator.AuthorizationServerWebResponseExceptionTranslator;

/**
 * @author zhouxiajie
 * @date 2019-01-23
 */
@Configuration
@ConditionalOnClass({AuthorizationServerProperties.class, CustomTokenEnhancer.class,
    AuthorizationServerWebResponseExceptionTranslator.class})
@ConditionalOnProperty(prefix = "sipa.boot", name = "security.authorizationServer", havingValue = "true")
@EnableAuthorizationServer
@EnableConfigurationProperties(AuthorizationServerProperties.class)
public class AuthorizationServerAutoConfiguration extends AuthorizationServerConfigurerAdapter {
    private static final Log LOGGER = LogFactory.get(AuthorizationServerAutoConfiguration.class);

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final AuthorizationServerProperties authorizationServerProperties;

    private final RedisConnectionFactory redisConnectionFactory;

    public AuthorizationServerAutoConfiguration(AuthenticationManager authenticationManager,
        UserDetailsService userDetailsService, AuthorizationServerProperties authorizationServerProperties,
        @Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authorizationServerProperties = authorizationServerProperties;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient(authorizationServerProperties.getClientId())
            .secret(authorizationServerProperties.getClientSecret())
            .authorizedGrantTypes(authorizationServerProperties.getAuthorizedGrantTypes().toArray(new String[] {}))
            .scopes(authorizationServerProperties.getScopes().toArray(new String[] {}))
            .accessTokenValiditySeconds(authorizationServerProperties.getAccessTokenValiditySeconds())
            .refreshTokenValiditySeconds(authorizationServerProperties.getRefreshTokenValiditySeconds());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        ELoginStrategy loginStrategy = authorizationServerProperties.getLoginStrategy();
        LOGGER.info("Use [{}] login strategy.", loginStrategy.getCode());
        TokenStore tokenStore;
        if (ELoginStrategy.STATEFUL == loginStrategy) {
            tokenStore = sipaBootRedisStore();
        } else {
            tokenStore = jwtTokenStore();
        }

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));

        endpoints.tokenStore(tokenStore)
            .tokenEnhancer(tokenEnhancerChain)
            .reuseRefreshTokens(true)
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService)
            .exceptionTranslator(new AuthorizationServerWebResponseExceptionTranslator());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
            new KeyStoreKeyFactory(new ClassPathResource(authorizationServerProperties.getJksPath()),
                authorizationServerProperties.getJksPassword().toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(authorizationServerProperties.getJksAlias()));
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public TokenStore sipaBootRedisStore() {
        return new SipaBootRedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients().tokenKeyAccess("denyAll()").checkTokenAccess("isAuthenticated()");
    }
}
