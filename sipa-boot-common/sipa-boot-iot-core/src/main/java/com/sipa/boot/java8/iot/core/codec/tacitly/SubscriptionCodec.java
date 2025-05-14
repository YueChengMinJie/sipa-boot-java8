package com.sipa.boot.java8.iot.core.codec.tacitly;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sipa.boot.java8.common.utils.BytesUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;
import com.sipa.boot.java8.iot.core.event.Subscription;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class SubscriptionCodec implements ICodec<Subscription> {
    public static final SubscriptionCodec INSTANCE = new SubscriptionCodec();

    @Override
    public Class<Subscription> forType() {
        return Subscription.class;
    }

    @Nullable
    @Override
    public Subscription decode(@Nonnull IPayload payload) {
        ByteBuf body = payload.getBody();

        byte[] subscriberLenArr = new byte[4];
        body.getBytes(0, subscriberLenArr);
        int subscriberLen = BytesUtils.beToInt(subscriberLenArr);

        byte[] subscriber = new byte[subscriberLen];
        body.getBytes(4, subscriber);
        String subscriberStr = new String(subscriber);

        byte[] featureBytes = new byte[8];
        body.getBytes(4 + subscriberLen, featureBytes);
        Subscription.EFeature[] features =
            IEnumDict.getByMask(Subscription.EFeature.class, BytesUtils.beToLong(featureBytes))
                .toArray(new Subscription.EFeature[0]);

        int headerLen = 12 + subscriberLen;
        body.resetReaderIndex();
        return Subscription.of(subscriberStr,
            body.slice(headerLen, body.readableBytes() - headerLen).toString(StandardCharsets.UTF_8).split("[\t]"),
            features);
    }

    @Override
    public IPayload encode(Subscription body) {
        // bytes 4
        byte[] subscriber = body.getSubscriber().getBytes();
        byte[] subscriberLen = BytesUtils.intToBe(subscriber.length);

        // bytes 8
        long features = IEnumDict.toMask(body.getFeatures());
        byte[] featureBytes = BytesUtils.longToBe(features);

        byte[] topics = String.join("\t", body.getTopics()).getBytes();

        return IPayload
            .of(Unpooled.buffer(subscriberLen.length + subscriber.length + featureBytes.length + topics.length)
                .writeBytes(subscriberLen)
                .writeBytes(subscriber)
                .writeBytes(featureBytes)
                .writeBytes(topics));
    }
}
