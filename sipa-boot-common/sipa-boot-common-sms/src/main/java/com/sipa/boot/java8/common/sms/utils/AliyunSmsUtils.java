package com.sipa.boot.java8.common.sms.utils;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.sipa.boot.java8.common.sms.properties.AliyunSmsProperties;

/**
 * @author sunyukun
 * @date 2019/4/4
 */
@Component
public class AliyunSmsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunSmsUtils.class);

    private static final Random RANDOM = new SecureRandom();

    private static final String SYMBOLS = "0123456789";

    private static final String CODE_FORMAT = "{\"code\":\"%s\"}";

    private static final String MESSAGE_OK = "\"Message\":\"OK\"";

    private static final String ALIYUN_SEND_API_DOMAIN = "dysmsapi.aliyuncs.com";

    private static final String API_VERSION = "2017-05-25";

    private static final String ACTION_SEND_SMS = "SendSms";

    public static final String PARAM_PHONE_NUMBERS = "PhoneNumbers";

    public static final String PARAM_SIGN_NAME = "SignName";

    public static final String PARAM_TEMPLATE_CODE = "TemplateCode";

    public static final String PARAM_TEMPLATE_PARAM = "TemplateParam";

    private static AliyunSmsProperties aliyunSmsProperties;

    private static IAcsClient messageClient;

    private static Integer version;

    private static IAcsClient getAcsClient() {
        LOGGER.info("aliyun current sms config is [{}]", aliyunSmsProperties);
        if (messageClient == null || !version.equals(aliyunSmsProperties.getVersion())) {
            DefaultProfile profile = DefaultProfile.getProfile(aliyunSmsProperties.getRegionId(),
                aliyunSmsProperties.getAccessKey(), aliyunSmsProperties.getSecretKey());
            messageClient = new DefaultAcsClient(profile);
            version = aliyunSmsProperties.getVersion();
        }
        return messageClient;
    }

    public static Boolean sendSms(String tel, String templateCode, String params) {
        LOGGER.info("Start to send sms message, tel [{}], templateCode [{}], params [{}]", tel, templateCode, params);
        CommonRequest request = new CommonRequest();
        request.putQueryParameter(PARAM_PHONE_NUMBERS, tel);
        request.putQueryParameter(PARAM_SIGN_NAME, aliyunSmsProperties.getSignName());
        request.putQueryParameter(PARAM_TEMPLATE_CODE, templateCode);
        request.putQueryParameter(PARAM_TEMPLATE_PARAM, params);
        return doSendSms(request);
    }

    public static Boolean sendVerifyCode(String code, String tel) {
        LOGGER.info("Start to send sms message, verify code [{}], tel is [{}]", code, tel);
        CommonRequest request = new CommonRequest();
        request.putQueryParameter(PARAM_PHONE_NUMBERS, tel);
        request.putQueryParameter(PARAM_SIGN_NAME, aliyunSmsProperties.getSignName());
        request.putQueryParameter(PARAM_TEMPLATE_CODE, aliyunSmsProperties.getTemplateCode());
        request.putQueryParameter(PARAM_TEMPLATE_PARAM, String.format(CODE_FORMAT, code));
        return doSendSms(request);
    }

    public static Boolean doSendSms(CommonRequest request) {
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(ALIYUN_SEND_API_DOMAIN);
        request.setSysVersion(API_VERSION);
        request.setSysAction(ACTION_SEND_SMS);
        try {
            CommonResponse response = getAcsClient().getCommonResponse(request);
            if (response.getHttpStatus() == HttpStatus.SC_OK && response.getData().contains(MESSAGE_OK)) {
                LOGGER.info("Message send success! Response is [{}]", response.getData());
                return Boolean.TRUE;
            } else {
                LOGGER.warn("Message send failed! Response is [{}], status code [{}]", response.getData(),
                    response.getHttpStatus());
            }
        } catch (ServerException e) {
            LOGGER.error("Message send failed! Caused by ServerException:", e);
        } catch (ClientException e) {
            LOGGER.error("Message send failed! Caused by ClientException:", e);
        }
        return Boolean.FALSE;
    }

    public static String getRandomVerifyCode() {
        char[] nonceChars = new char[6];
        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }
        return new String(nonceChars);
    }

    @Autowired
    public void setAliyunMessageConfig(AliyunSmsProperties aliyunSmsProperties) {
        AliyunSmsUtils.aliyunSmsProperties = aliyunSmsProperties;
    }
}
