package com.sipa.boot.java8.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author sunyukun
 * @date 2019/2/18
 */
public class SqlUtils {
    /**
     * 针对mybatis的like相关参数的转译
     *
     * @param keyword
     *            未转译前语句
     * @return transfer keyword
     */
    public static String transfer(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        if (keyword.contains(SipaBootCommonConstants.Symbol.PERCENTAGE)
            || keyword.contains(SipaBootCommonConstants.Symbol.UNDERLINE)) {
            keyword = keyword.replaceAll("\\\\", "\\\\\\\\").replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
        }
        return keyword;
    }
}
