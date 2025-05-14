package com.sipa.boot.java8.iot.core.device;

import com.sipa.boot.java8.common.utils.UuidUtils;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.base.IWritePropertyMessageSender;
import com.sipa.boot.java8.iot.core.message.property.WritePropertyMessage;
import com.sipa.boot.java8.iot.core.message.property.WritePropertyMessageReply;
import com.sipa.boot.java8.iot.core.metadata.base.IPropertyMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultWritePropertyMessageSender implements IWritePropertyMessageSender {
    private final WritePropertyMessage message = new WritePropertyMessage();

    private final IDeviceOperator operator;

    public DefaultWritePropertyMessageSender(IDeviceOperator operator) {
        this.operator = operator;
        message.setMessageId(UuidUtils.generator());
        message.setDeviceId(operator.getDeviceId());
    }

    @Override
    public IWritePropertyMessageSender custom(Consumer<WritePropertyMessage> messageConsumer) {
        messageConsumer.accept(message);
        return this;
    }

    @Override
    public IWritePropertyMessageSender header(String header, Object value) {
        message.addHeader(header, value);
        return this;
    }

    @Override
    public IWritePropertyMessageSender messageId(String messageId) {
        message.setMessageId(messageId);
        return this;
    }

    @Override
    public IWritePropertyMessageSender write(String property, Object value) {
        message.addProperty(property, value);
        return this;
    }

    @Override
    public Mono<IWritePropertyMessageSender> validate() {
        Map<String, Object> properties = message.getProperties();

        return operator.getMetadata().doOnNext(metadata -> {
            for (IPropertyMetadata meta : metadata.getProperties()) {
                Object property = properties.get(meta.getId());
                if (property == null) {
                    continue;
                }
                properties.put(meta.getId(), meta.getValueType().validate(property).assertSuccess());
            }
        }).thenReturn(this);
    }

    @Override
    public Flux<WritePropertyMessageReply> send() {
        return operator.messageSender().send(Mono.just(message));
    }
}
