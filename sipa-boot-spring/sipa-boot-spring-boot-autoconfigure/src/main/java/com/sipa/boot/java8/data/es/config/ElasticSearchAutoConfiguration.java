package com.sipa.boot.java8.data.es.config;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.data.es.client.ElasticRestClient;
import com.sipa.boot.java8.data.es.property.ElasticSearchProperties;

/**
 * @author caszhou
 * @date 2021/10/16
 */
@Configuration
@ConditionalOnClass({ElasticSearchProperties.class})
@EnableConfigurationProperties({ElasticSearchProperties.class})
@AutoConfigureBefore(ElasticsearchRestClientAutoConfiguration.class)
public class ElasticSearchAutoConfiguration {
    private static ElasticSearchProperties properties;

    public ElasticSearchAutoConfiguration(ElasticSearchProperties properties) {
        ElasticSearchAutoConfiguration.properties = properties;
    }

    @Bean
    public RestClientBuilder elasticsearchRestClientBuilder() {
        return RestClient.builder(properties.createHosts())
            .setRequestConfigCallback(properties::applyRequestConfigBuilder)
            .setHttpClientConfigCallback(properties::applyHttpAsyncClientBuilder);
    }

    @Bean
    public RestHighLevelClient elasticsearchRestHighLevelClient(RestClientBuilder restClientBuilder) {
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
        ElasticRestClient.setWriteClient(client);
        ElasticRestClient.setQueryClient(client);
        return client;
    }
}
