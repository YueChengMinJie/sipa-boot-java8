package com.sipa.boot.java8.common.oauth2.exception.serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.Maps;
import com.sipa.boot.java8.common.oauth2.enumerate.EOAuth2Error;
import com.sipa.boot.java8.common.oauth2.exception.SipaBootOAuth2Exception;
import com.sipa.boot.java8.common.services.IMessageService;
import com.sipa.boot.java8.common.utils.AppUtils;

/**
 * @author fzh
 */
public class CustomOAuth2ExceptionSerializer extends StdSerializer<OAuth2Exception> {
    public CustomOAuth2ExceptionSerializer() {
        super(OAuth2Exception.class);
    }

    @Override
    public void serialize(OAuth2Exception oauth2Exception, JsonGenerator jsonGenerator, SerializerProvider provider)
        throws IOException {
        jsonGenerator.writeStartObject();

        // append common error info
        EOAuth2Error error = EOAuth2Error.create(oauth2Exception.getOAuth2ErrorCode());
        String errorCode = SipaBootOAuth2Exception.DEFAULT_OAUTH2_EXCEPTION_ERROR_CODE_PREFIX + error.getErrorCode();

        jsonGenerator.writeNumberField("numericErrorCode", error.getNumericCode());
        jsonGenerator.writeStringField("errorCode", errorCode);

        IMessageService sipaBootMessageService = AppUtils.getBean("sipaBootMessageService", IMessageService.class);
        String errorMessageKey = "errorMessage";
        if (error.isUseOriginalMessage()) {
            jsonGenerator.writeStringField(errorMessageKey, oauth2Exception.getMessage());
        } else {
            jsonGenerator.writeStringField(errorMessageKey,
                Optional.ofNullable(sipaBootMessageService.getMessageAlwaysReturn(errorCode))
                    .orElse(error.getErrorMessage()));
        }

        jsonGenerator.writeStringField("originatingService", AppUtils.getAppName());
        jsonGenerator.writeStringField("timestamp", LocalDateTime.now().toString());

        Map<String, String> originatingException = Maps.newHashMap();
        originatingException.put("error", oauth2Exception.getOAuth2ErrorCode());
        originatingException.put("error_description", oauth2Exception.getMessage());
        if (oauth2Exception.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> entry : oauth2Exception.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                originatingException.put(key, add);
            }
        }
        jsonGenerator.writeObjectField("originatingException", originatingException);

        jsonGenerator.writeEndObject();
    }
}
