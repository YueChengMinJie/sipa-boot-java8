package com.sipa.boot.java8.wx.miniapp.config;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.wx.miniapp.property.WxMaProperties;
import com.sipa.boot.java8.wx.miniapp.util.WxMaBeanUtils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaKefuMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.message.WxMaMessageHandler;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author zhouxiajie
 */
@Configuration
@EnableConfigurationProperties(WxMaProperties.class)
@ConditionalOnClass(WxMaProperties.class)
public class WxMaAutoConfiguration {
    private static final Log LOGGER = LogFactory.get(WxMaAutoConfiguration.class);

    private final WxMaProperties properties;

    public WxMaAutoConfiguration(WxMaProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        List<WxMaProperties.Config> configs = this.properties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("大哥，你至少配一个config吧！");
        }

        WxMaBeanUtils.maServices = configs.stream().map(a -> {
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(a.getAppid());
            config.setSecret(a.getSecret());
            config.setToken(a.getToken());
            config.setAesKey(a.getAesKey());
            config.setMsgDataFormat(a.getMsgDataFormat());

            WxMaService service = new WxMaServiceImpl();
            service.setWxMaConfig(config);
            WxMaBeanUtils.routers.put(a.getAppid(), this.newRouter(service));
            return service;
        }).collect(Collectors.toMap(s -> s.getWxMaConfig().getAppid(), a -> a));
    }

    private WxMaMessageRouter newRouter(WxMaService service) {
        final WxMaMessageRouter router = new WxMaMessageRouter(service);
        router.rule()
            .handler(logHandler)
            .next()
            // .rule()
            // .async(false)
            // .content("模板")
            // .handler(templateMsgHandler)
            // .end()
            .rule()
            .async(false)
            .content("文本")
            .handler(textHandler)
            .end()
            .rule()
            .async(false)
            .content("图片")
            .handler(picHandler)
            .end()
            .rule()
            .async(false)
            .content("二维码")
            .handler(qrcodeHandler)
            .end();
        return router;
    }

    // private final WxMaMessageHandler templateMsgHandler = (wxMessage, context, service, sessionManager) -> {
    // service.getMsgService()
    // .sendTemplateMsg(WxMaTemplateMessage.builder()
    // .templateId("templateId")
    // .formId("formId")
    // .data(Lists.newArrayList(new WxMaTemplateData("keyword1", "339208499", "#173177")))
    // .toUser(wxMessage.getFromUser())
    // .build());
    // return null;
    // };

    private final WxMaMessageHandler logHandler = (wxMessage, context, service, sessionManager) -> {
        service.getMsgService()
            .sendKefuMsg(WxMaKefuMessage.newTextBuilder()
                .content("收到信息为：" + wxMessage.toJson())
                .toUser(wxMessage.getFromUser())
                .build());
        return null;
    };

    private final WxMaMessageHandler textHandler = (wxMessage, context, service, sessionManager) -> {
        service.getMsgService()
            .sendKefuMsg(WxMaKefuMessage.newTextBuilder().content("回复文本消息").toUser(wxMessage.getFromUser()).build());
        return null;
    };

    private final WxMaMessageHandler picHandler = (wxMessage, context, service, sessionManager) -> {
        try {
            WxMediaUploadResult uploadResult = service.getMediaService()
                .uploadMedia("image", "png", ClassLoader.getSystemResourceAsStream("image.png"));
            service.getMsgService()
                .sendKefuMsg(WxMaKefuMessage.newImageBuilder()
                    .mediaId(uploadResult.getMediaId())
                    .toUser(wxMessage.getFromUser())
                    .build());
        } catch (WxErrorException e) {
            LOGGER.error(e);
        }
        return null;
    };

    private final WxMaMessageHandler qrcodeHandler = (wxMessage, context, service, sessionManager) -> {
        try {
            final File file = service.getQrcodeService().createQrcode("path", 430);
            WxMediaUploadResult uploadResult = service.getMediaService().uploadMedia("image", file);
            service.getMsgService()
                .sendKefuMsg(WxMaKefuMessage.newImageBuilder()
                    .mediaId(uploadResult.getMediaId())
                    .toUser(wxMessage.getFromUser())
                    .build());
        } catch (WxErrorException e) {
            LOGGER.error(e);
        }
        return null;
    };
}
