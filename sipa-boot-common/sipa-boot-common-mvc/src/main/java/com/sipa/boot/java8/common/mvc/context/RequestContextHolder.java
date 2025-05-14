package com.sipa.boot.java8.common.mvc.context;

/**
 * @author sunyukun
 * @date 2019/2/21
 */
public class RequestContextHolder {
    private static InheritableThreadLocal<RequestContext> contextThreadLocal = new InheritableThreadLocal<>();

    public static RequestContext getRequestContext() {
        RequestContext requestContext = contextThreadLocal.get();
        if (requestContext == null) {
            requestContext = new RequestContext();
            contextThreadLocal.set(requestContext);
        }
        return requestContext;
    }

    public static void setRequestContext(RequestContext requestContext) {
        if (requestContext == null) {
            requestContext = new RequestContext();
        }
        contextThreadLocal.set(requestContext);
    }

    public static boolean contains(String key) {
        return getRequestContext().containsAttribute(key);
    }

    public static String getHostName() {
        return getRequestContext().getHostName();
    }

    public static void setHostName(String hostName) {
        getRequestContext().setHostName(hostName);
    }

    public static void release() {
        contextThreadLocal.remove();
    }
}
