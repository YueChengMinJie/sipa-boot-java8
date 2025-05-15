package com.sipa.boot.java8.common.utils;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;

/**
 * @author zhouxiajie
 * @date 2021/4/13
 */
public class AmapUtils {
    private static final String EMPTY_CITY = "[]";

    private static final String PROVINCE_KEY = "province";

    private static final String CITY_KEY = "city";

    private static final String DISTRICT_KEY = "district";

    public static String getProvince(Map<String, Object> addressComponent) {
        return Utils.stringValueOf(addressComponent.get(PROVINCE_KEY));
    }

    public static String getCity(Map<String, Object> addressComponent) {
        String city = Utils.stringValueOf(addressComponent.get(CITY_KEY));
        return Objects.isNull(city) || Lists.newArrayList(SipaBootCommonConstants.BLANK, EMPTY_CITY).contains(city)
            ? getProvince(addressComponent) : city;
    }

    public static String getDistrict(Map<String, Object> addressComponent) {
        return Utils.stringValueOf(addressComponent.get(DISTRICT_KEY));
    }
}
