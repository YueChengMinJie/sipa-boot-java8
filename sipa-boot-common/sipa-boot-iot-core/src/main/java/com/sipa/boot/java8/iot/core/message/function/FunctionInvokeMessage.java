package com.sipa.boot.java8.iot.core.message.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IRepayableDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class FunctionInvokeMessage extends CommonDeviceMessage
    implements IRepayableDeviceMessage<FunctionInvokeMessageReply> {
    private String functionId;

    private List<FunctionParameter> inputs = new ArrayList<>();

    public FunctionInvokeMessage() {
    }

    public List<FunctionParameter> getInputs() {
        return inputs;
    }

    public void setInputs(List<FunctionParameter> inputs) {
        this.inputs = inputs;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.INVOKE_FUNCTION;
    }

    public Optional<Object> getInput(String name) {
        return inputs.stream()
            .filter(param -> param.getName().equals(name))
            .map(FunctionParameter::getValue)
            .findFirst();
    }

    public Optional<Object> getInput(int index) {
        return inputs.size() > index ? Optional.ofNullable(inputs.get(index)) : Optional.empty();
    }

    public Map<String, Object> inputsToMap() {
        return inputs.stream()
            .collect(Collectors.toMap(FunctionParameter::getName, FunctionParameter::getValue, (a, b) -> a));
    }

    public <T> T inputsToBean(Class<T> beanType) {
        return new JSONObject(inputsToMap()).toJavaObject(beanType);
    }

    public List<Object> inputsToList() {
        return inputs.stream().map(FunctionParameter::getValue).collect(Collectors.toList());
    }

    public Object[] inputsToArray() {
        return inputs.stream().map(FunctionParameter::getValue).toArray();
    }

    public FunctionInvokeMessage addInput(String name, Object value) {
        return this.addInput(new FunctionParameter(name, value));
    }

    public FunctionInvokeMessage addInput(FunctionParameter parameter) {
        inputs.add(parameter);
        return this;
    }

    @Override
    public FunctionInvokeMessage addHeader(String header, Object value) {
        super.addHeader(header, value);
        return this;
    }

    @Override
    public FunctionInvokeMessage removeHeader(String header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        super.fromJson(jsonObject);
        this.functionId = jsonObject.getString("functionId");
    }

    @Override
    public FunctionInvokeMessageReply newReply() {
        FunctionInvokeMessageReply reply = new FunctionInvokeMessageReply().from(this);
        reply.setFunctionId(this.functionId);
        return reply;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }
}
