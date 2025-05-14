package com.sipa.boot.java8.common.ws.config;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sipa.boot.java8.common.ws.filter.WsPermitAuthenticationFilter;
import com.sipa.boot.java8.common.ws.property.WsAuthProperties;

/**
 * @author zhouxiajie
 * @date 2019-06-05
 */
@Configuration
@ConditionalOnClass(WsAuthProperties.class)
@EnableConfigurationProperties({WsAuthProperties.class})
public class WsSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {
    private final WsAuthProperties wsAuthProperties;

    public WsSecurityAutoConfiguration(WsAuthProperties wsAuthProperties) {
        this.wsAuthProperties = wsAuthProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf().disable()
            .anonymous().disable()
            .logout().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(buildPermitAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
    }

    private Filter buildPermitAuthenticationFilter() {
        return new WsPermitAuthenticationFilter("**");
    }

    @Bean
    public RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setClientId(wsAuthProperties.getClientId());
        tokenService.setClientSecret(wsAuthProperties.getClientSecret());
        tokenService.setCheckTokenEndpointUrl(wsAuthProperties.getCheckTokenUrl());
        return tokenService;
    }
}
