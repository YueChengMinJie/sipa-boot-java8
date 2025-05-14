package com.sipa.boot.java8.iot.core.metadata.base;

import java.util.Arrays;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IConfigScopeSupport {
    IConfigScope[] all = new IConfigScope[0];

    default IConfigScope[] getScopes() {
        return all;
    }

    default boolean hasAnyScope(IConfigScope... target) {
        if (target.length == 0 || getScopes() == all) {
            return true;
        }
        return Arrays.stream(target).anyMatch(this::hasScope);
    }

    default boolean hasScope(IConfigScope target) {
        if (getScopes() == all) {
            return true;
        }
        for (IConfigScope scope : getScopes()) {
            if (scope.getId().equals(target.getId())) {
                return true;
            }
        }
        return false;
    }
}
