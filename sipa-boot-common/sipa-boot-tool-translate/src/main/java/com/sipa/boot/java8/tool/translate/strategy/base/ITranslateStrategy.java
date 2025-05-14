package com.sipa.boot.java8.tool.translate.strategy.base;

import com.sipa.boot.java8.tool.translate.enumerate.base.TranslateCode;

/**
 * @author caszhou
 * @date 2021/9/10
 */
public interface ITranslateStrategy {
    /**
     * 翻译.
     *
     * @param from
     *            源语言
     * @param to
     *            目标语言
     * @param q
     * @return 翻译结果
     */
    String translate(TranslateCode from, TranslateCode to, String q);
}
