package com.sipa.boot.java8.common.resttemplate.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import com.sipa.boot.java8.common.resttemplate.interceptor.LoggingClientHttpRequestInterceptor;
import com.sipa.boot.java8.common.resttemplate.property.RestTemplateProperties;

/**
 * @author sunyukun
 * @since 2019/5/8 19:52
 */
@Configuration
@ConditionalOnProperty(prefix = "sipa.boot.resttemplate", name = "enabled", havingValue = "true")
@ConditionalOnClass({RestTemplateProperties.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.common.resttemplate.**"})
@EnableConfigurationProperties
public class RestTemplateAutoConfiguration {
    private static final String REST_TEMPLATE_CLASS_TYPE_BEAN_NAME = "restTemplateClass";

    private final RestTemplateProperties restTemplateProperties;

    public RestTemplateAutoConfiguration(RestTemplateProperties restTemplateProperties) {
        this.restTemplateProperties = restTemplateProperties;
    }

    @Bean
    @ConditionalOnMissingBean(value = {HttpRequestRetryHandler.class})
    public DefaultHttpRequestRetryHandler
        defaultHttpRequestRetryHandler(RestTemplateProperties restTemplateProperties) {
        return new DefaultHttpRequestRetryHandler(restTemplateProperties.getRetryCount(),
            restTemplateProperties.isRequestSentRetryEnabled());
    }

    @Bean(name = REST_TEMPLATE_CLASS_TYPE_BEAN_NAME)
    @ConditionalOnMissingBean(name = REST_TEMPLATE_CLASS_TYPE_BEAN_NAME)
    public Class<? extends RestTemplate> restTemplateClass() {
        return RestTemplate.class;
    }

    public PoolingHttpClientConnectionManager getConnectionManager() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException ignored) {
        }
        SSLConnectionSocketFactory factory = null;
        try {
            factory = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException ignored) {
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("https", Objects.requireNonNull(factory))
            .register("http", new PlainConnectionSocketFactory())
            .build();

        PoolingHttpClientConnectionManager poolingConnectionManager =
            new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(restTemplateProperties.getMaxConnTotal());
        return poolingConnectionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClientBuilder httpClientBuilder() {
        return HttpClients.custom().setConnectionManager(getConnectionManager()).setConnectionManagerShared(true);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient(HttpClientBuilder httpClientBuilder, HttpRequestRetryHandler httpRequestRetryHandler,
        RestTemplateProperties restTemplateProperties) {
        return httpClientBuilder.setMaxConnTotal(restTemplateProperties.getMaxConnTotal())
            .setMaxConnPerRoute(restTemplateProperties.getMaxConnPerRoute())
            .setRetryHandler(httpRequestRetryHandler)
            .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientHttpRequestFactory clientHttpRequestFactory(RestTemplateProperties restTemplateProperties,
        HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
            new HttpComponentsClientHttpRequestFactory(httpClient);
        clientHttpRequestFactory.setConnectTimeout(restTemplateProperties.getConnectTimeout());
        clientHttpRequestFactory.setReadTimeout(restTemplateProperties.getReadTimeout());
        clientHttpRequestFactory.setConnectionRequestTimeout(restTemplateProperties.getConnRequestTimeout());
        return new BufferingClientHttpRequestFactory(clientHttpRequestFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public UriTemplateHandler uriTemplateHandler() {
        return new DefaultUriBuilderFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplateBuilder restTemplateBuilder(ClientHttpRequestFactory clientHttpRequestFactory,
        UriTemplateHandler uriTemplateHandler) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder = builder.requestFactory(() -> clientHttpRequestFactory).uriTemplateHandler(uriTemplateHandler);
        return builder;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder,
        @Qualifier(REST_TEMPLATE_CLASS_TYPE_BEAN_NAME) Class<? extends RestTemplate> restTemplateClass) {
        return buildRestTemplate(restTemplateBuilder, restTemplateClass);
    }

    private RestTemplate buildRestTemplate(RestTemplateBuilder restTemplateBuilder,
        Class<? extends RestTemplate> restTemplateClass) {
        RestTemplate restTemplate = restTemplateBuilder.build(restTemplateClass);
        // add logging interceptor as last one
        if (restTemplateProperties.getLogging().isEnabled()) {
            restTemplate.getInterceptors()
                .add(new LoggingClientHttpRequestInterceptor(restTemplateProperties.getLogging()));
        }
        return restTemplate;
    }
}
