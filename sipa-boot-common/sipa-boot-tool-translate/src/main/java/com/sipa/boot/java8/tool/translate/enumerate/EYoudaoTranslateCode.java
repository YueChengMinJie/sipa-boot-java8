package com.sipa.boot.java8.tool.translate.enumerate;

import com.sipa.boot.java8.tool.translate.enumerate.base.TranslateCode;

/**
 * @author caszhou
 * @date 2021/9/10
 */
public enum EYoudaoTranslateCode implements TranslateCode {
    // 中文
    ZH("zh-CHS"),
    // 英文
    EN("en");

    private final String code;

    EYoudaoTranslateCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
