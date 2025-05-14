package com.sipa.boot.java8.iot.core.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sipa.boot.java8.common.utils.UuidUtils;
import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.exception.FunctionUndefinedException;
import com.sipa.boot.java8.iot.core.exception.IllegalParameterException;
import com.sipa.boot.java8.iot.core.message.base.IFunctionInvokeMessageSender;
import com.sipa.boot.java8.iot.core.message.base.IHeaders;
import com.sipa.boot.java8.iot.core.message.function.FunctionInvokeMessage;
import com.sipa.boot.java8.iot.core.message.function.FunctionInvokeMessageReply;
import com.sipa.boot.java8.iot.core.message.function.FunctionParameter;
import com.sipa.boot.java8.iot.core.metadata.ValidateResult;
import com.sipa.boot.java8.iot.core.metadata.base.IPropertyMetadata;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultFunctionInvokeMessageSender implements IFunctionInvokeMessageSender {
    private final FunctionInvokeMessage message = new FunctionInvokeMessage();

    private final IDeviceOperator operator;

    public DefaultFunctionInvokeMessageSender(IDeviceOperator operator, String functionId) {
        this.operator = operator;
        message.setMessageId(UuidUtils.generator());
        message.setFunctionId(functionId);
        message.setDeviceId(operator.getDeviceId());
    }

    @Override
    public IFunctionInvokeMessageSender custom(Consumer<FunctionInvokeMessage> messageConsumer) {
        messageConsumer.accept(message);
        return this;
    }

    @Override
    public IFunctionInvokeMessageSender addParameter(FunctionParameter parameter) {
        message.addInput(parameter);
        return this;
    }

    @Override
    public IFunctionInvokeMessageSender setParameter(List<FunctionParameter> parameter) {
        message.setInputs(new ArrayList<>(parameter));
        return this;
    }

    @Override
    public IFunctionInvokeMessageSender messageId(String messageId) {
        message.setMessageId(messageId);
        return this;
    }

    @Override
    public IFunctionInvokeMessageSender header(String header, Object value) {
        message.addHeader(header, value);
        return this;
    }

    @Override
    public Mono<IFunctionInvokeMessageSender> validate() {
        String function = message.getFunctionId();

        return operator.getMetadata()
            .flatMap(metadata -> Mono.justOrEmpty(metadata.getFunction(function)))
            .switchIfEmpty(Mono.error(() -> new FunctionUndefinedException(function, "功能[" + function + "]未定义")))
            .doOnNext(functionMetadata -> {
                List<IPropertyMetadata> metadataInputs = functionMetadata.getInputs();
                List<FunctionParameter> inputs = message.getInputs();

                Map<String, FunctionParameter> properties = inputs.stream()
                    .collect(Collectors.toMap(FunctionParameter::getName, Function.identity(), (t1, t2) -> t1));
                for (IPropertyMetadata metadata : metadataInputs) {
                    FunctionParameter parameter = properties.get(metadata.getId());
                    Object value = Optional.ofNullable(parameter).map(FunctionParameter::getValue).orElse(null);
                    if (value == null) {
                        continue;
                    }

                    ValidateResult validateResult = metadata.getValueType().validate(value);

                    validateResult.ifFail(result -> {
                        throw new IllegalParameterException(metadata.getId(), result.getErrorMsg());
                    });
                    if (validateResult.getValue() != null) {
                        parameter.setValue(validateResult.getValue());
                    }
                }
            })
            .thenReturn(this);
    }

    @Override
    public Flux<FunctionInvokeMessageReply> send() {
        if (message.getHeader(IHeaders.async).isPresent()) {
            return doSend();
        }
        return operator.getMetadata()
            .flatMap(meta -> Mono.justOrEmpty(meta.getFunction(message.getFunctionId())))
            .doOnNext(func -> async(func.isAsync()))
            .thenMany(doSend());
    }

    private Flux<FunctionInvokeMessageReply> doSend() {
        return operator.messageSender().send(Mono.just(message));
    }
}
