package com.sipa.boot.java8.common.zuul.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.didispace.swagger.butler.EnableSwaggerButler;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.oauth2.enumerate.ELoginStrategy;
import com.sipa.boot.java8.common.oauth2.store.SipaBootRedisTokenStore;
import com.sipa.boot.java8.common.zuul.common.ZuulConstants;
import com.sipa.boot.java8.common.zuul.property.ZuulProperties;
import com.sipa.boot.java8.common.zuul.security.access.intercept.RbacSecurityInterceptor;
import com.sipa.boot.java8.common.zuul.security.oauth2.provider.authentication.HeaderTokenExtractor;
import com.sipa.boot.java8.common.zuul.security.oauth2.provider.error.CustomOAuth2AuthenticationEntryPoint;
import com.sipa.boot.java8.common.zuul.security.oauth2.provider.token.CustomAccessTokenConverter;

/**
 * @author zhouxiajie
 * @date 2019-02-03
 */
@Configuration
@ConditionalOnClass({ZuulProperties.class})
@EnableConfigurationProperties({ZuulProperties.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.common.zuul.**"})
@EnableZuulProxy
@EnableSwaggerButler
@EnableResourceServer
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true, jsr250Enabled = true)
public class ZuulAutoConfiguration extends ResourceServerConfigurerAdapter {
    private static final Log LOGGER = LogFactory.get(ZuulAutoConfiguration.class);

    private static final String[] AUTH_LIST =
        {ZuulConstants.TOKEN_BASE_URI, ZuulConstants.TOKEN_BASE2, ZuulConstants.TOKEN_BASE3};

    private static final String OPENAPI_URL = "/api/**/v2/api-docs";

    private final RbacSecurityInterceptor rbacSecurityInterceptor;

    private final CustomAccessTokenConverter customAccessTokenConverter;

    private final HeaderTokenExtractor headerTokenExtractor;

    private final CustomOAuth2AuthenticationEntryPoint customOAuth2AuthenticationEntryPoint;

    private final RedisConnectionFactory redisConnectionFactory;

    private final ZuulProperties zuulProperties;

    public ZuulAutoConfiguration(RbacSecurityInterceptor rbacSecurityInterceptor,
        CustomAccessTokenConverter customAccessTokenConverter, HeaderTokenExtractor headerTokenExtractor,
        CustomOAuth2AuthenticationEntryPoint customOAuth2AuthenticationEntryPoint,
        RedisConnectionFactory redisConnectionFactory, ZuulProperties zuulProperties) {
        this.rbacSecurityInterceptor = rbacSecurityInterceptor;
        this.customAccessTokenConverter = customAccessTokenConverter;
        this.headerTokenExtractor = headerTokenExtractor;
        this.customOAuth2AuthenticationEntryPoint = customOAuth2AuthenticationEntryPoint;
        this.redisConnectionFactory = redisConnectionFactory;
        this.zuulProperties = zuulProperties;
    }

    @Bean
    public UserInfoRestTemplateCustomizer restTemplateCustomizer(LoadBalancerInterceptor loadBalancerInterceptor) {
        return template -> {
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(loadBalancerInterceptor);

            AccessTokenProviderChain accessTokenProviderChain = Stream
                .of(new AuthorizationCodeAccessTokenProvider(), new ImplicitAccessTokenProvider(),
                    new ResourceOwnerPasswordAccessTokenProvider(), new ClientCredentialsAccessTokenProvider())
                .peek(tp -> tp.setInterceptors(interceptors))
                .collect(Collectors.collectingAndThen(Collectors.toList(), AccessTokenProviderChain::new));
            template.setAccessTokenProvider(accessTokenProviderChain);
        };
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        // @formatter:off
        config
            .tokenServices(tokenServices())
            .tokenExtractor(headerTokenExtractor)
            .authenticationEntryPoint(customOAuth2AuthenticationEntryPoint);
        // @formatter:on
    }

    @Bean
    public DefaultTokenServices tokenServices() {
        ELoginStrategy loginStrategy = zuulProperties.getSecurity().getLoginStrategy();
        LOGGER.info("Use [{}] login strategy.", loginStrategy.getCode());
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        if (ELoginStrategy.STATEFUL == loginStrategy) {
            defaultTokenServices.setTokenStore(sipaBootRedisStore());
        } else {
            defaultTokenServices.setTokenStore(tokenStore());
        }
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public TokenStore sipaBootRedisStore() {
        return new SipaBootRedisTokenStore(redisConnectionFactory);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(customAccessTokenConverter);
        converter.setVerifierKey(getPublicKey());
        return converter;
    }

    private String getPublicKey() {
        Resource resource = new ClassPathResource(ZuulConstants.PUBLIC_KEY);
        String publicKey;
        try {
            publicKey = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return publicKey;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests()
                .antMatchers(OPENAPI_URL).permitAll()
                .antMatchers(AUTH_LIST).authenticated()
                .anyRequest().permitAll();
        // @formatter:on
        if (zuulProperties.isEnableRbac()) {
            http.addFilterBefore(rbacSecurityInterceptor, FilterSecurityInterceptor.class);
        }
    }
}
