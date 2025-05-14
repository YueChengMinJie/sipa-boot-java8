package com.sipa.boot.java8.common.log.property;

import java.util.Set;

/**
 * @author feizhihao
 */
public class RequestLoggingProperties extends CommonLoggingProperties {
    private boolean includeQueryString = false;

    private boolean includeClientInfo = false;

    private Set<String> blackList;

    public boolean isIncludeQueryString() {
        return includeQueryString;
    }

    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    public boolean isIncludeClientInfo() {
        return includeClientInfo;
    }

    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    public Set<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(Set<String> blackList) {
        this.blackList = blackList;
    }

    @Override
    public String getBeforeMessagePrefix() {
        return "[Server] " + super.getBeforeMessagePrefix();
    }

    @Override
    public String getAfterMessagePrefix() {
        return "[Server] " + super.getAfterMessagePrefix();
    }
}
