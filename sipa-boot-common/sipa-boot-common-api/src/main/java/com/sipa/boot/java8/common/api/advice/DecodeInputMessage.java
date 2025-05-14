package com.sipa.boot.java8.common.api.advice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StringUtils;

import com.sipa.boot.java8.common.api.property.ApiProperty;
import com.sipa.boot.java8.common.api.utils.ApiUtil;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.ListUtils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;

/**
 * 解码处理
 *
 * @author caszhou
 * @date 2020/9/18
 */
@Slf4j
public class DecodeInputMessage implements HttpInputMessage {
    private final HttpHeaders headers;

    private InputStream body;

    public DecodeInputMessage(HttpInputMessage httpInputMessage, ApiProperty apiProperty) {
        // 这里是body 读取之前的处理
        this.headers = httpInputMessage.getHeaders();

        String encodeAesKey = SipaBootCommonConstants.BLANK;
        List<String> keys = this.headers.get(SipaBootCommonConstants.SIPA_BOOT_API_KEY);
        if (CollectionUtils.isNotEmpty(keys)) {
            encodeAesKey = ListUtils.first(keys);
        }

        try {
            // 1、解码得到aes 密钥
            RSA rsa = new RSA(ApiUtil.cleanKey(apiProperty.getRsaPrivateKey()),
                ApiUtil.cleanKey(apiProperty.getRsaPublicKey()));
            byte[] decodeAesKey = rsa.decryptFromBase64(encodeAesKey, KeyType.PrivateKey);
            // 2、从inputStreamReader 得到aes 加密的内容
            String encodeAesContent = new BufferedReader(new InputStreamReader(httpInputMessage.getBody())).lines()
                .collect(Collectors.joining(System.lineSeparator()));
            if (!StringUtils.isEmpty(encodeAesContent)) {
                // 3、AES通过密钥CBC解码
                String aesDecode = SecureUtil.aes(decodeAesKey).decryptStrFromBase64(encodeAesContent);
                if (!StringUtils.isEmpty(aesDecode)) {
                    // 4、重新写入到controller
                    this.body = new ByteArrayInputStream(aesDecode.getBytes());
                }
            }
        } catch (Exception e) {
            log.error(SipaBootCommonConstants.BLANK, e);
        }
    }

    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
