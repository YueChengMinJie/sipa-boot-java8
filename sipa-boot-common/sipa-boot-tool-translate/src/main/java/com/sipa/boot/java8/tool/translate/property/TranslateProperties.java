package com.sipa.boot.java8.tool.translate.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author caszhou
 * @date 2021/9/10
 */
@Component
@ConfigurationProperties(prefix = "sipa.boot.translate")
public class TranslateProperties {
    private String type;

    @NestedConfigurationProperty
    private YoudaoTranslateProperties youdao = new YoudaoTranslateProperties();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public YoudaoTranslateProperties getYoudao() {
        return youdao;
    }

    public void setYoudao(YoudaoTranslateProperties youdao) {
        this.youdao = youdao;
    }
}
