package com.sipa.boot.java8.common.log.property;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author feizhihao
 */
public class CommonLoggingProperties {
    public static final String APPLICATION_LOG_FORMAT = "{} :: {} :: {} :: {}";

    public static final String APPLICATION_LOG_FORMAT_WITH_STACK = "{} :: {} :: {} :: {} :: {}";

    public static final String PLACE_HOLDER = "-";

    public static final String UNKNOWN = "[unknown]";

    protected boolean enabled = false;

    protected boolean includeHeaders = false;

    protected boolean includePayload = false;

    protected Set<String> headerWhiteList = Collections.emptySet();

    protected Set<String> headerBlackList =
        new HashSet<>(Arrays.asList("authorization", "cookie", "sw8-correlation", "sw8-x", "sw8", "content-length",
            "x-request-id", "x-tenant-id", "x-user-id", "x-authorities", "x-request-from", "x-user-agent"));

    protected int maxPayloadLength = 5120;

    protected String beforeMessagePrefix = "Before request [{} ";

    protected String beforeMessageSuffix = "]";

    protected String afterMessagePrefix = "After request [{} ";

    protected String afterMessageSuffix = " status={} spend={}ms]";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isIncludePayload() {
        return includePayload;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    public Set<String> getHeaderWhiteList() {
        return headerWhiteList;
    }

    public void setHeaderWhiteList(Set<String> headerWhiteList) {
        this.headerWhiteList = headerWhiteList;
    }

    public Set<String> getHeaderBlackList() {
        return headerBlackList;
    }

    public void setHeaderBlackList(Set<String> headerBlackList) {
        this.headerBlackList = headerBlackList;
    }

    public String getBeforeMessagePrefix() {
        return beforeMessagePrefix;
    }

    public void setBeforeMessagePrefix(String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }

    public String getBeforeMessageSuffix() {
        return beforeMessageSuffix;
    }

    public void setBeforeMessageSuffix(String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }

    public String getAfterMessagePrefix() {
        return afterMessagePrefix;
    }

    public void setAfterMessagePrefix(String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }

    public String getAfterMessageSuffix() {
        return afterMessageSuffix;
    }

    public void setAfterMessageSuffix(String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
    }

    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
    }
}
