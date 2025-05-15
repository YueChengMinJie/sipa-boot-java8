package com.sipa.boot.java8.common.swagger.config;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.swagger.plugin.property.SwaggerProperties;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author zhouxiajie
 * @date 2019-01-22
 */
@Configuration
@EnableOpenApi
@ConditionalOnClass({SwaggerProperties.class})
@EnableConfigurationProperties({SwaggerProperties.class})
@ComponentScan(value = {"com.sipa.boot.java8.**.common.swagger.**"})
public class SwaggerAutoConfiguration {
    private static final String ENV_OF_SHOW = "rel";

    private static final String PROFILE_OF_SHOW = "prod";

    @Value("${spring.application.name:}")
    private String appName;

    @Value("${current.apollo.env:}")
    private String currentApolloEnv;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final SwaggerProperties swaggerProperties;

    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    // @formatter:off
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
            .select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
            .paths(PathSelectors.any())
            .build()
            .securityContexts(Collections.singletonList(securityContexts()))
            .securitySchemes(Collections.singletonList(securitySchemes()))
            .enable(isEnable())
            .apiInfo(apiInfo());
    }

    private boolean isEnable() {
        if (StringUtils.isBlank(currentApolloEnv)) {
          return !SwaggerAutoConfiguration.PROFILE_OF_SHOW.equalsIgnoreCase(activeProfile);
        } else {
            return !SwaggerAutoConfiguration.ENV_OF_SHOW.equalsIgnoreCase(currentApolloEnv);
        }
    }

    private SecurityContext securityContexts() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.any())
            .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("all", "all scope");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
    }

    private SecurityScheme securitySchemes() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .description(appName.replaceAll(SipaBootCommonConstants.ACROSS, ""))
            .title(appName)
            .contact(new Contact("sipa.boot", "http://dev.ycmj.com", "dev@ycmj.com"))
            .version("1.0.0-SNAPSHOT")
            .license("MIT")
            .termsOfServiceUrl("http://dev.ycmj.com")
            .build();
    }
    // @formatter:on
}
