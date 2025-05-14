package com.sipa.boot.java8.common.mail.util;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.archs.lru.LruCache;
import com.sipa.boot.java8.common.mail.property.MailProperties;
import com.sipa.boot.java8.common.mail.provider.IMailConfigProvider;
import com.sipa.boot.java8.common.utils.AppUtils;

/**
 * @author zhouxiajie
 * @date 2020/12/14
 */
@Component
public class JavaMailSenderUtils {
    private static final Map<MailProperties, JavaMailSender> sender = Collections.synchronizedMap(new LruCache<>(1));

    private static final Map<String, Map<MailProperties, JavaMailSender>> tenantSender =
        Collections.synchronizedMap(new HashMap<>());

    /**
     * from 1, 脑残用法，不用判断是否为空
     */
    @Nonnull
    public static JavaMailSender requireSenderQuietly() {
        return sender.entrySet().iterator().next().getValue();
    }

    /**
     * from 1, 用这个
     */
    @Nullable
    public static JavaMailSender requireSender() {
        if (MapUtils.isNotEmpty(sender)) {
            for (Map.Entry<MailProperties, JavaMailSender> entry : sender.entrySet()) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * from 1, 用这个
     */
    @Nullable
    public synchronized static String requireUsername() {
        if (MapUtils.isNotEmpty(sender)) {
            for (Map.Entry<MailProperties, JavaMailSender> entry : sender.entrySet()) {
                return entry.getKey().getUsername();
            }
        }
        return null;
    }

    /**
     * from 1, 用这个
     */
    public static void putSender(MailProperties properties, JavaMailSender javaMailSender) {
        sender.put(properties, javaMailSender);
    }

    /**
     * from 2, 用这个
     */
    public static void putTenantSender(MailProperties properties, JavaMailSender javaMailSender, String tenantId) {
        Map<MailProperties, JavaMailSender> sender = new HashMap<>(1);
        sender.put(properties, javaMailSender);
        tenantSender.put(tenantId, sender);
    }

    /**
     * from 2, 用这个
     */
    @Nullable
    public synchronized static JavaMailSender initAndRequireSender(String tenantId) {
        IMailConfigProvider mailConfigProvider = AppUtils.getBean(IMailConfigProvider.class);
        Objects.requireNonNull(mailConfigProvider, "mailConfigProvider not null");

        MailProperties properties = mailConfigProvider.getProperties(tenantId);
        Map<MailProperties, JavaMailSender> sender = tenantSender.get(tenantId);
        if (MapUtils.isEmpty(sender) || !sender.containsKey(properties)) {
            JavaMailSender mailSender = initJavaMailSender(properties);
            putTenantSender(properties, mailSender, tenantId);
        }
        return requireTenantSender(tenantId);
    }

    /**
     * from 1, 用这个
     */
    @Nullable
    public static JavaMailSender requireTenantSender(String tenantId) {
        if (MapUtils.isNotEmpty(tenantSender)) {
            Map<MailProperties, JavaMailSender> sender = tenantSender.get(tenantId);
            if (MapUtils.isNotEmpty(sender)) {
                for (Map.Entry<MailProperties, JavaMailSender> entry : sender.entrySet()) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * from 2, 用这个
     */
    @Nullable
    public synchronized static String requireUsername(String tenantId) {
        if (MapUtils.isNotEmpty(tenantSender)) {
            Map<MailProperties, JavaMailSender> sender = tenantSender.get(tenantId);
            if (MapUtils.isNotEmpty(sender)) {
                for (Map.Entry<MailProperties, JavaMailSender> entry : sender.entrySet()) {
                    return entry.getKey().getUsername();
                }
            }
        }
        return null;
    }

    @Nullable
    public static JavaMailSender initJavaMailSender(MailProperties mailProperties) {
        if (mailProperties.getFrom() == 0) {
            return null;
        }

        Objects.requireNonNull(mailProperties.getHost(), "mail host is required");
        Objects.requireNonNull(mailProperties.getPort(), "mail port is required");
        Objects.requireNonNull(mailProperties.getUsername(), "mail username is required");
        Objects.requireNonNull(mailProperties.getPassword(), "mail password is required");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());

        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");

        switch (mailProperties.getEncrypt()) {
            case 0:
                break;
            case 1:
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.port", mailProperties.getPort());
                break;
            case 2:
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.socketFactory.port", mailProperties.getPort());
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                break;
            default:
                break;
        }

        if (mailProperties.isDebug()) {
            props.put("mail.debug", "true");
        }

        return mailSender;
    }
}
