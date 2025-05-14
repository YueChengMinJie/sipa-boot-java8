package com.sipa.boot.java8.iot.core.codec;

import java.util.*;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.codec.base.ICodecsSupport;
import com.sipa.boot.java8.iot.core.codec.tacitly.*;
import com.sipa.boot.java8.iot.core.event.Subscription;
import com.sipa.boot.java8.iot.core.event.TopicPayload;
import com.sipa.boot.java8.iot.core.event.base.IPayload;
import com.sipa.boot.java8.iot.core.message.base.IDeviceMessage;
import com.sipa.boot.java8.iot.core.message.base.IMessage;

import io.netty.buffer.ByteBuf;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class DefaultCodecsSupport implements ICodecsSupport {
    private static Map<Class, ICodec> staticCodec = new HashMap<>();

    static {
        staticCodec.put(byte.class, ByteCodec.INSTANCE);
        staticCodec.put(Byte.class, ByteCodec.INSTANCE);

        staticCodec.put(int.class, IntegerCodec.INSTANCE);
        staticCodec.put(Integer.class, IntegerCodec.INSTANCE);

        staticCodec.put(long.class, LongCodec.INSTANCE);
        staticCodec.put(Long.class, LongCodec.INSTANCE);

        staticCodec.put(double.class, DoubleCodec.INSTANCE);
        staticCodec.put(Double.class, DoubleCodec.INSTANCE);

        staticCodec.put(float.class, FloatCodec.INSTANCE);
        staticCodec.put(Float.class, FloatCodec.INSTANCE);

        staticCodec.put(boolean.class, BooleanCodec.INSTANCE);
        staticCodec.put(Boolean.class, BooleanCodec.INSTANCE);

        staticCodec.put(String.class, StringCodec.UTF8);
        staticCodec.put(byte[].class, BytesCodec.INSTANCE);

        staticCodec.put(Void.class, VoidCodec.INSTANCE);
        staticCodec.put(void.class, VoidCodec.INSTANCE);

        staticCodec.put(IDeviceMessage.class, DeviceMessageCodec.INSTANCE);
        staticCodec.put(IMessage.class, DeviceMessageCodec.INSTANCE);

        {
            JsonCodec<Map> codec = JsonCodec.of(Map.class);
            staticCodec.put(Map.class, codec);
            staticCodec.put(HashMap.class, codec);
            staticCodec.put(LinkedHashMap.class, codec);
        }

        staticCodec.put(TopicPayload.class, TopicPayloadCodec.INSTANCE);
        staticCodec.put(Subscription.class, SubscriptionCodec.INSTANCE);

        staticCodec.put(ByteBuf.class, ByteBufCodec.INSTANCE);

        staticCodec.put(JSONObject.class, FastJsonCodec.INSTANCE);

        staticCodec.put(JSONArray.class, FastJsonArrayCodec.INSTANCE);
    }

    @Override
    public <T> Optional<ICodec<T>> lookup(ResolvableType type) {
        ResolvableType ref = type;
        if (Publisher.class.isAssignableFrom(ref.toClass())) {
            ref = ref.getGeneric(0);
        }
        Class refType = ref.toClass();

        ICodec<T> codec = staticCodec.get(refType);
        if (codec == null) {
            if (List.class.isAssignableFrom(refType)) {
                codec = (ICodec<T>)JsonArrayCodec.of(ref.getGeneric(0).toClass());
            } else if (ref.toClass().isEnum()) {
                codec = (ICodec<T>)new EnumCodec((Enum[])ref.toClass().getEnumConstants());
            } else if (IPayload.class.isAssignableFrom(refType)) {
                codec = (ICodec<T>)DirectCodec.INSTANCE;
            } else if (Set.class.isAssignableFrom(ref.toClass())) {
                codec = (ICodec<T>)JsonArrayCodec.of(ref.getGeneric(0).toClass(), HashSet.class, HashSet::new);
            } else if (ByteBuf.class.isAssignableFrom(refType)) {
                codec = (ICodec<T>)ByteBufCodec.INSTANCE;
            } else if (IMessage.class.isAssignableFrom(refType)) {
                codec = (ICodec<T>)DeviceMessageCodec.INSTANCE;
            }
        }

        if (codec != null) {
            return Optional.of(codec);
        }
        if (refType.isInterface()) {
            return Optional.empty();
        }
        if (codec == null) {
            codec = JsonCodec.of(refType);
        }
        return Optional.of(codec);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
