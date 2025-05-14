package com.sipa.boot.java8.tool.translate.strategy.youdao;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.tool.translate.constants.TranslateConstants;
import com.sipa.boot.java8.tool.translate.enumerate.base.TranslateCode;
import com.sipa.boot.java8.tool.translate.property.TranslateProperties;
import com.sipa.boot.java8.tool.translate.strategy.base.ITranslateStrategy;
import com.sipa.boot.java8.tool.translate.strategy.youdao.form.YoudaoForm;
import com.sipa.boot.java8.tool.translate.strategy.youdao.helper.YoudaoSignHelper;
import com.sipa.boot.java8.tool.translate.strategy.youdao.response.YoudaoResponse;

/**
 * @author caszhou
 * @date 2021/9/10
 */
@Component
@ConditionalOnProperty(prefix = "sipa.boot.translate", name = "type", havingValue = "youdao")
public class YoudaoTranslateStrategy implements ITranslateStrategy {
    private static final Log LOGGER = LogFactory.get(YoudaoTranslateStrategy.class);

    private final TranslateProperties properties;

    private final RestTemplate restTemplate;

    public YoudaoTranslateStrategy(TranslateProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public String translate(TranslateCode from, TranslateCode to, String q) {
        String utf8Q = YoudaoSignHelper.utf8(q);
        YoudaoResponse youdaoResponse = Optional.ofNullable(restTemplate.postForObject(properties.getYoudao().getUrl(),
            getYoudaoForm(from, to, utf8Q), YoudaoResponse.class))
            .orElseThrow(() -> new RuntimeException(TranslateConstants.Error.FAIL));
        LOGGER.info(JSONObject.toJSONString(youdaoResponse));
        return StringUtils.join(youdaoResponse.getTranslation(), SipaBootCommonConstants.EMPTY);
    }

    private HttpEntity<LinkedMultiValueMap<String, String>> getYoudaoForm(TranslateCode from, TranslateCode to,
        String q) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String appKey = properties.getYoudao().getAppKey();
        String appSecret = properties.getYoudao().getAppSecret();
        String salt = String.valueOf(System.currentTimeMillis());
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        // @formatter:off
        YoudaoForm form = YoudaoForm.YoudaoFormBuilder.anYoudaoForm()
            .withQ(q)
            .withFrom(from.getCode())
            .withTo(to.getCode())
            .withAppKey(appKey)
            .withSalt(salt)
            .withSign(YoudaoSignHelper.sign(appKey, YoudaoSignHelper.input(q), salt, curtime, appSecret))
            .withCurtime(curtime)
            .build();
        // @formatter:on
        return new HttpEntity<>(getLinkedMultiValueMap(form), headers);
    }

    private LinkedMultiValueMap<String, String> getLinkedMultiValueMap(YoudaoForm form) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("q", form.getQ());
        map.add("from", form.getFrom());
        map.add("to", form.getTo());
        map.add("appKey", form.getAppKey());
        map.add("salt", form.getSalt());
        map.add("sign", form.getSign());
        map.add("signType", form.getSignType());
        map.add("curtime", form.getCurtime());
        return map;
    }
}
