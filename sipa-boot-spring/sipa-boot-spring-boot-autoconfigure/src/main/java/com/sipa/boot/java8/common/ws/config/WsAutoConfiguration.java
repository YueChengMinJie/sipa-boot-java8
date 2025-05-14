package com.sipa.boot.java8.common.ws.config;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.sipa.boot.java8.common.ws.property.WsProperties;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@Configuration
@ConditionalOnClass(WsProperties.class)
@EnableConfigurationProperties({WsProperties.class})
@EnableWebSocketMessageBroker
public class WsAutoConfiguration implements WebSocketMessageBrokerConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsAutoConfiguration.class);

    private final WsProperties wsProperties;

    private final RemoteTokenServices tokenServices;

    public WsAutoConfiguration(WsProperties wsProperties, RemoteTokenServices tokenServices) {
        this.wsProperties = wsProperties;
        this.tokenServices = tokenServices;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes(wsProperties.getAppPrefix());
        config.enableSimpleBroker(wsProperties.getSimpleBrokers().toArray(new String[] {}));
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(wsProperties.getUrl())
            .addInterceptors(handshakeInterceptor())
            .setAllowedOrigins("*")
            .withSockJS();
    }

    private HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler wsHandler, Map<String, Object> attributes) {
                ServletServerHttpRequest req = (ServletServerHttpRequest)request;
                String token = req.getServletRequest().getParameter("access_token");
                if (StringUtils.isNotBlank(token)) {
                    try {
                        OAuth2Authentication authentication = tokenServices.loadAuthentication(token);
                        return !Objects.isNull(authentication.getPrincipal());
                    } catch (AuthenticationException e) {
                        LOGGER.info("auth fail.", e);
                    } catch (InvalidTokenException e) {
                        LOGGER.info("invalid token.", e);
                    } catch (Exception e) {
                        LOGGER.info("common error.", e);
                    }
                }
                return false;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                WebSocketHandler webSocketHandler, Exception e) {}
        };
    }
}
