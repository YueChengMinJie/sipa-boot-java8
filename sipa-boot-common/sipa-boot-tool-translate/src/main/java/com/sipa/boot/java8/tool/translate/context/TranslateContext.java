package com.sipa.boot.java8.tool.translate.context;

import org.springframework.stereotype.Component;

import com.sipa.boot.java8.tool.translate.enumerate.base.TranslateCode;
import com.sipa.boot.java8.tool.translate.strategy.base.ITranslateStrategy;

/**
 * @author caszhou
 * @date 2021/9/10
 */
@Component
public class TranslateContext {
    private static ITranslateStrategy translateStrategy;

    public TranslateContext(ITranslateStrategy translateStrategy) {
        TranslateContext.translateStrategy = translateStrategy;
    }

    public static String translate(TranslateCode from, TranslateCode to, String q) {
        return translateStrategy.translate(from, to, q);
    }
}
