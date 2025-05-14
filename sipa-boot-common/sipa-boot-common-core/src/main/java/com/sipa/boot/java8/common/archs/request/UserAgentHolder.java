package com.sipa.boot.java8.common.archs.request;

import java.util.Objects;

import org.slf4j.MDC;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public abstract class UserAgentHolder {
    public static final String USER_AGENT = "User-Agent";

    public static final String X_USER_AGENT = "X-User-Agent";

    public static final String X_BROWSER = "X-Browser";

    public static final String X_OS = "X-Os";

    public static final String X_DEVICE = "X-Device";

    private static final ThreadLocal<String> USER_AGENT_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<String> BROWSER_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<String> OS_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<String> DEVICE_HOLDER = new ThreadLocal<>();

    public static String get() {
        return USER_AGENT_HOLDER.get();
    }

    public static String getBrowser() {
        return BROWSER_HOLDER.get();
    }

    public static String getOS() {
        return OS_HOLDER.get();
    }

    public static String getDevice() {
        return DEVICE_HOLDER.get();
    }

    public static void set(String userAgentHeader) {
        USER_AGENT_HOLDER.set(userAgentHeader);

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentHeader);
        if (Objects.nonNull(userAgent.getBrowser()) && Objects.nonNull(userAgent.getBrowserVersion())) {
            BROWSER_HOLDER.set(userAgent.getBrowser().getName() + "," + userAgent.getBrowserVersion().getVersion());
        }

        if (Objects.nonNull(userAgent.getOperatingSystem())) {
            OS_HOLDER.set(userAgent.getOperatingSystem().getName());
            DEVICE_HOLDER.set(userAgent.getOperatingSystem().getDeviceType().getName());
        }

        MDC.put(X_BROWSER, BROWSER_HOLDER.get());
        MDC.put(X_OS, OS_HOLDER.get());
        MDC.put(X_DEVICE, DEVICE_HOLDER.get());
    }

    public static String remove() {
        final String from = USER_AGENT_HOLDER.get();

        USER_AGENT_HOLDER.remove();
        OS_HOLDER.remove();
        DEVICE_HOLDER.remove();
        BROWSER_HOLDER.remove();

        MDC.remove(X_BROWSER);
        MDC.remove(X_OS);
        MDC.remove(X_DEVICE);

        return from;
    }
}
