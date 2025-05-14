package com.sipa.boot.java8.common.mvc.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunyukun
 * @date 2019/2/21
 */
public class RequestContext {
    public static final String HOST_NAME = "HOST_NAME";

    private Map<String, Object> contextMap = new HashMap<>();

    public boolean containsAttribute(String key) {
        return contextMap.containsKey(key);
    }

    private Object getAttribute(String key) {
        return contextMap.get(key);
    }

    public RequestContext addAttribute(String key, Object object) {
        contextMap.put(key, object);
        return this;
    }

    public String getHostName() {
        return (String)getAttribute(HOST_NAME);
    }

    public RequestContext setHostName(String hostName) {
        addAttribute(RequestContext.HOST_NAME, hostName);
        return this;
    }
}
