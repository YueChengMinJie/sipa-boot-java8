package com.sipa.boot.java8.iot.core.device;

import com.sipa.boot.java8.common.utils.UuidUtils;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.message.base.IReadPropertyMessageSender;
import com.sipa.boot.java8.iot.core.message.property.ReadPropertyMessage;
import com.sipa.boot.java8.iot.core.message.property.ReadPropertyMessageReply;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultReadPropertyMessageSender implements IReadPropertyMessageSender {
    private final ReadPropertyMessage message = new ReadPropertyMessage();

    private final IDeviceOperator operator;

    public DefaultReadPropertyMessageSender(IDeviceOperator operator) {
        this.operator = operator;
        message.setMessageId(UuidUtils.generator());
        message.setDeviceId(operator.getDeviceId());
    }

    @Override
    public IReadPropertyMessageSender read(Collection<String> property) {
        message.setProperties(new ArrayList<>(property));
        return this;
    }

    @Override
    public IReadPropertyMessageSender custom(Consumer<ReadPropertyMessage> messageConsumer) {
        messageConsumer.accept(message);
        return this;
    }

    @Override
    public IReadPropertyMessageSender header(String header, Object value) {
        message.addHeader(header, value);
        return this;
    }

    @Override
    public IReadPropertyMessageSender messageId(String messageId) {
        message.setMessageId(messageId);
        return this;
    }

    @Override
    public Flux<ReadPropertyMessageReply> send() {
        return operator.messageSender().send(Mono.just(message));
    }
}
