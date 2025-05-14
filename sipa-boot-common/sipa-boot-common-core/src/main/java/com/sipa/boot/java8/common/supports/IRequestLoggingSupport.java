package com.sipa.boot.java8.common.supports;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.sipa.boot.java8.common.archs.validation.validator.EmailPlusValidator;
import com.sipa.boot.java8.common.log.property.CommonLoggingProperties;

public interface IRequestLoggingSupport {
    String BLIND_INDICATOR = "***";

    /**
     * build header part for log
     *
     * @param msg
     *            msg StringBuilder
     * @param headerMap
     *            all header
     * @param headerWhiteList
     *            header WhiteList
     */
    default void buildHeader(StringBuilder msg, Map<String, List<String>> headerMap, Set<String> headerWhiteList,
        Set<String> headerBlackList) {
        Map<String, List<String>> localHeaderMap = headerMap;
        if (CollectionUtils.isNotEmpty(headerBlackList)) {
            localHeaderMap = headerMap.entrySet()
                .stream()
                .filter(he -> !headerBlackList.contains(he.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        if (CollectionUtils.isNotEmpty(headerWhiteList)) {
            localHeaderMap = headerMap.entrySet()
                .stream()
                .filter(he -> headerWhiteList.contains(he.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        msg.append(" headers=");
        if (MapUtils.isEmpty(localHeaderMap)) {
            msg.append(CommonLoggingProperties.PLACE_HOLDER);
        } else {
            msg.append(localHeaderMap);
        }
    }

    /**
     * filter payload
     *
     * @param payload
     *            original payload
     * @return filtered payload
     */
    default String filterPayload(String payload) {
        return payload.replaceAll(EmailPlusValidator.EMAIL_BASE_PATTERN, BLIND_INDICATOR);
    }
}
