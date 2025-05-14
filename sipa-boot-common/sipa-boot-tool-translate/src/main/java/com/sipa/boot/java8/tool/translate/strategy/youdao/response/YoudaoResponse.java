package com.sipa.boot.java8.tool.translate.strategy.youdao.response;

import java.util.List;

/**
 * @author caszhou
 * @date 2021/9/10
 */
public class YoudaoResponse {
    private String errorCode;

    private String query;

    private List<String> translation;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }
}
