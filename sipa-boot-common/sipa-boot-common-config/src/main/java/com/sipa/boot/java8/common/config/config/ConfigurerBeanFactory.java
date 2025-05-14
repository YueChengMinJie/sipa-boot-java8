package com.sipa.boot.java8.common.config.config;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanHelper;
import org.apache.commons.lang.StringUtils;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class ConfigurerBeanFactory {
    private ConfigurerBeanFactory() {
        // Add a private constructor to hide the implicit public one
    }

    public static <T> T getDeclaredBean(String keyPrefix, Class<T> type) {
        return getDeclaredBean(Configurer.getInstance(), keyPrefix, type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDeclaredBean(Configuration configuration, String keyPrefix, Class<T> type) {
        ConfigurerBeanDeclaration beanDecl = (ConfigurerBeanDeclaration)getBeanDeclaration(configuration, keyPrefix);
        beanDecl.setBeanClassName(type.getName());
        return (T)BeanHelper.createBean(beanDecl, type);
    }

    public static BeanDeclaration getBeanDeclaration(Configuration configuration, String keyPrefix) {
        if (StringUtils.isBlank(keyPrefix)) {
            throw new IllegalArgumentException("key prefix is blank or null");
        }

        ConfigurerBeanDeclaration decl = new ConfigurerBeanDeclaration();
        Iterator<String> iter = configuration.getKeys(keyPrefix);
        while (iter.hasNext()) {
            String key = iter.next();
            // 1 for the '.'
            String subKey = key.substring(keyPrefix.length() + 1);
            if ("*class*".equals(subKey)) {
                decl.setBeanClassName(configuration.getString(key));
                continue;
            }

            Object value = configuration.getString(key);
            decl.addBeanProperty(translate(subKey), value);
        }

        return decl;
    }

    /**
     * replace all 'xxx_aa' and 'xxx_bb' to 'xxxAa' and 'xxxBb'
     *
     * @param input
     *            property key
     * @return result without '-' and '_'
     */
    private static String translate(String input) {
        int length = input.length();
        StringBuilder result = new StringBuilder(length * 2);
        int resultLength = 0;
        boolean wasPrevTranslated = true;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            // skip first starting underscore or slash
            if (i > 0) {
                if (c == '_' || c == '-') {
                    wasPrevTranslated = false;
                    continue;
                } else {
                    if (!wasPrevTranslated && resultLength > 0
                        && !Character.isUpperCase(result.charAt(resultLength - 1))) {
                        c = Character.toUpperCase(c);
                    }
                    wasPrevTranslated = true;
                }
            }

            result.append(c);
            resultLength++;
        }
        return resultLength > 0 ? result.toString() : input;
    }
}
