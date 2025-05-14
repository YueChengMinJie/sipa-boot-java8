package com.sipa.boot.java8.common.auth.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.auth.entrypoint.RestAuthenticationEntryPoint;
import com.sipa.boot.java8.common.auth.extractor.ITokenExtractor;
import com.sipa.boot.java8.common.auth.extractor.JwtExtractor;
import com.sipa.boot.java8.common.auth.filter.JwtAuthenticationFilter;
import com.sipa.boot.java8.common.auth.filter.LoginAuthenticationFilter;
import com.sipa.boot.java8.common.auth.matcher.SkipPathRequestMatcher;
import com.sipa.boot.java8.common.auth.property.AuthProperties;
import com.sipa.boot.java8.common.auth.provider.ILoginAuthenticationProvider;
import com.sipa.boot.java8.common.auth.provider.JwtAuthenticationProvider;
import com.sipa.boot.java8.common.auth.verifier.JwtVerifier;

/**
 * @author zhouxiajie
 * @date 2018/2/5
 */
@Configuration
@ConditionalOnClass(value = {JwtExtractor.class, JwtVerifier.class, AuthProperties.class})
@EnableWebSecurity
@ComponentScan(value = {"com.sipa.boot.java8.**.common.auth.**"})
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
public class AuthAutoConfiguration extends WebSecurityConfigurerAdapter {
    private static final String FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login";

    private static final String TOKEN_REFRESH_ENTRY_POINT = "/api/auth/refresh";

    private static final String TOKEN_REGISTER_ENTRY_POINT = "/api/auth/register";

    private static final String TOKEN_LOGOUT_ENTRY_POINT = "/api/auth/logout";

    private static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationSuccessHandler successHandler;

    @Autowired
    private AuthenticationFailureHandler failureHandler;

    @Autowired(required = false)
    private ILoginAuthenticationProvider loginAuthenticationProvider;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private ITokenExtractor tokenExtractor;

    @Autowired
    private AuthProperties authProperties;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(loginAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> permitAllEndpointList = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT,
            TOKEN_REGISTER_ENTRY_POINT, TOKEN_LOGOUT_ENTRY_POINT);

        // @formatter:off
        http
            .csrf().disable()
            .anonymous().disable()
            .logout().disable()
            .formLogin().disable()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // @formatter:on

        if (!authProperties.isAuthorized()) {
            String[] antPatterns = {TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT, TOKEN_REGISTER_ENTRY_POINT,
                TOKEN_LOGOUT_ENTRY_POINT};
            LoginAuthenticationFilter loginAuthenticationFilter = buildAjaxLoginProcessingFilter();
            JwtAuthenticationFilter jwtAuthenticationFilter =
                buildJwtTokenAuthenticationProcessingFilter(permitAllEndpointList);

            // @formatter:off
            http
                .authorizeRequests()
                    .antMatchers(antPatterns).permitAll()
                .and()
                .authorizeRequests()
                    .antMatchers(TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()
                .and()
                .addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            // @formatter:on
        }
    }

    private LoginAuthenticationFilter buildAjaxLoginProcessingFilter() {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter(
            AuthAutoConfiguration.FORM_BASED_LOGIN_ENTRY_POINT, successHandler, failureHandler, objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    private JwtAuthenticationFilter buildJwtTokenAuthenticationProcessingFilter(List<String> pathsToSkip) {
        SkipPathRequestMatcher matcher =
            new SkipPathRequestMatcher(pathsToSkip, AuthAutoConfiguration.TOKEN_BASED_AUTH_ENTRY_POINT);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(failureHandler, tokenExtractor, matcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    /**
     * 回环调用解决.
     */
    @Configuration
    public static class DependencyAutoConfiguration {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
