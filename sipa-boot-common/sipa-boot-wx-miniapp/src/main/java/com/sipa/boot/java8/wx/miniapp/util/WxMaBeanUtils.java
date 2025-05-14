package com.sipa.boot.java8.wx.miniapp.util;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.Maps;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;

/**
 * @author zhouxiajie
 * @date 2020/5/2
 */
public class WxMaBeanUtils {
    public static Map<String, WxMaMessageRouter> routers = Maps.newHashMap();

    public static Map<String, WxMaService> maServices;

    public static WxMaService getMaService(String appid) {
        return maServices.get(appid);
    }

    public static WxMaMessageRouter getRouter(String appid) {
        return routers.get(appid);
    }

    public static WxMaService getFirstMaService() {
        return new ArrayList<>(maServices.values()).get(0);
    }

    public static WxMaMessageRouter getFirstRouter() {
        return new ArrayList<>(routers.values()).get(0);
    }
}
