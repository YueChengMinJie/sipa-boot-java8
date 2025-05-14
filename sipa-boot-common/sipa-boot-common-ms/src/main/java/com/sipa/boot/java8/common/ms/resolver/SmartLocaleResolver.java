package com.sipa.boot.java8.common.ms.resolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author zhouxiajie
 * @date 2019-08-13
 */
public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {
    private static final Log LOGGER = LogFactory.get(SmartLocaleResolver.class);

    private static final String LOCALE_HEADER = "Accept-Language";

    private static final List<Locale> LOCALES =
        Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("de"), new Locale("ja"));

    public SmartLocaleResolver() {
        LOGGER.info("Locale.getDefault() = [{}]", Locale.getDefault());
    }

    @NonNull
    @Override
    public Locale resolveLocale(@NonNull HttpServletRequest request) {
        try {
            if (StringUtils.isBlank(request.getHeader(LOCALE_HEADER))) {
                return Locale.getDefault();
            }
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader("Accept-Language"));
            return Optional.ofNullable(Locale.lookup(list, LOCALES)).orElse(Locale.getDefault());
        } catch (Exception e) {
            LOGGER.warn(e.toString());
            return Locale.getDefault();
        }
    }
}
