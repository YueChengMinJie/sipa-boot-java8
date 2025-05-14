package com.sipa.boot.java8.iot.core.codec.tacitly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.BytesUtils;
import com.sipa.boot.java8.iot.core.codec.base.ICodec;
import com.sipa.boot.java8.iot.core.event.TopicPayload;
import com.sipa.boot.java8.iot.core.event.base.IPayload;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class TopicPayloadCodec implements ICodec<TopicPayload> {
    private static final Log log = LogFactory.get(TopicPayloadCodec.class);

    public static final TopicPayloadCodec INSTANCE = new TopicPayloadCodec();

    @Override
    public Class<TopicPayload> forType() {
        return TopicPayload.class;
    }

    public static TopicPayload doDecode(ByteBuf byteBuf) {
        byte[] topicLen = new byte[4];

        byteBuf.getBytes(0, topicLen);
        int bytes = BytesUtils.beToInt(topicLen);

        byte[] topicBytes = new byte[bytes];

        byteBuf.getBytes(4, topicBytes);
        String topic = new String(topicBytes);

        int idx = 4 + bytes;

        ByteBuf body = byteBuf.slice(idx, byteBuf.writerIndex() - idx);
        byteBuf.resetReaderIndex();
        return TopicPayload.of(topic, IPayload.of(body));
    }

    public static ByteBuf doEncode(TopicPayload body) {
        byte[] topic = body.getTopic().getBytes();
        byte[] topicLen = BytesUtils.intToBe(topic.length);
        try {
            ByteBuf bodyBuf = body.getBody();
            return ByteBufAllocator.DEFAULT.buffer(topicLen.length + topic.length + bodyBuf.writerIndex())
                .writeBytes(topicLen)
                .writeBytes(topic)
                .writeBytes(bodyBuf, 0, bodyBuf.writerIndex());
        } catch (Throwable e) {
            log.error("encode topic [{}] payload error", body.getTopic());
            throw e;
        }
    }

    @Nullable
    @Override
    public TopicPayload decode(@Nonnull IPayload payload) {
        return doDecode(payload.getBody());
    }

    @Override
    public IPayload encode(TopicPayload body) {
        byte[] topic = body.getTopic().getBytes();
        byte[] topicLen = BytesUtils.intToBe(topic.length);
        try {
            ByteBuf bodyBuf = body.getBody();
            return IPayload.of(ByteBufAllocator.DEFAULT.buffer(topicLen.length + topic.length + bodyBuf.writerIndex())
                .writeBytes(topicLen)
                .writeBytes(topic)
                .writeBytes(bodyBuf, 0, bodyBuf.writerIndex()));
        } catch (Throwable e) {
            log.error("encode topic [{}] payload error", body.getTopic());
            throw e;
        }
    }
}
