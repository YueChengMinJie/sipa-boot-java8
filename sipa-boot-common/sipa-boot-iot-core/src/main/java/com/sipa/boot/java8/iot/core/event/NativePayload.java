package com.sipa.boot.java8.iot.core.event;

import java.util.*;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;
import com.sipa.boot.java8.iot.core.codec.base.IDecoder;
import com.sipa.boot.java8.iot.core.codec.base.IEncoder;
import com.sipa.boot.java8.iot.core.event.base.IPayload;
import com.sipa.boot.java8.iot.core.metadata.base.IJsonable;
import com.sipa.boot.java8.iot.core.util.RecyclerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class NativePayload<T> extends AbstractReferenceCounted implements IPayload {
    private static final Log log = LogFactory.get(NativePayload.class);

    private static final Recycler<NativePayload> POOL =
        RecyclerUtils.newRecycler(NativePayload.class, NativePayload::new, 1);

    private final Recycler.Handle<NativePayload> handle;

    private T nativeObject;

    private IEncoder<T> encoder;

    private volatile IPayload ref;

    private ByteBuf buf;

    private NativePayload(Recycler.Handle<NativePayload> handle) {
        this.handle = handle;
    }

    public T getNativeObject() {
        return nativeObject;
    }

    public void setNativeObject(T nativeObject) {
        this.nativeObject = nativeObject;
    }

    public IEncoder<T> getEncoder() {
        return encoder;
    }

    public void setEncoder(IEncoder<T> encoder) {
        this.encoder = encoder;
    }

    public IPayload getRef() {
        return ref;
    }

    public void setRef(IPayload ref) {
        this.ref = ref;
    }

    public ByteBuf getBuf() {
        return buf;
    }

    public void setBuf(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public IPayload slice() {
        return ref != null ? ref.slice() : this;
    }

    public static <T> NativePayload<T> of(T nativeObject, IEncoder<T> encoder) {
        NativePayload<T> payload;
        try {
            payload = POOL.get();
        } catch (Exception e) {
            payload = new NativePayload<>(null);
        }
        payload.setRefCnt(1);
        payload.nativeObject = nativeObject;
        payload.encoder = encoder;
        return payload;
    }

    public static <T> NativePayload<T> of(T nativeObject, Supplier<IPayload> bodySupplier) {
        return of(nativeObject, v -> bodySupplier.get());
    }

    @Override
    public <T> T decode(IDecoder<T> decoder, boolean release) {
        try {
            if (decoder.isDecodeFrom(nativeObject)) {
                return (T)nativeObject;
            }
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
        Class<T> type = decoder.forType();
        if (type == JSONObject.class || type == Map.class) {
            return (T)bodyToJson(release);
        }
        if (Map.class.isAssignableFrom(decoder.forType())) {
            return bodyToJson(release).toJavaObject(decoder.forType());
        }
        return IPayload.super.decode(decoder, release);
    }

    @Override
    public Object decode() {
        return decode(true);
    }

    @Override
    public Object decode(boolean release) {
        try {
            return nativeObject;
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    @Nonnull
    @Override
    public ByteBuf getBody() {
        if (buf == null) {
            synchronized (this) {
                if (buf != null) {
                    return buf;
                }
                ref = encoder.encode(nativeObject);
                buf = Unpooled.unreleasableBuffer(ref.getBody());
            }
        }
        return buf;
    }

    @Override
    public int refCnt() {
        return super.refCnt();
    }

    @Override
    protected void deallocate() {
        this.buf = null;
        this.nativeObject = null;
        this.encoder = null;
        if (this.ref != null) {
            ReferenceCountUtil.safeRelease(this.ref);
            this.ref = null;
        }
        if (handle != null) {
            handle.recycle(this);
        }
    }

    @Override
    public NativePayload<T> touch(Object o) {
        return this;
    }

    @Override
    public NativePayload<T> touch() {
        super.touch();
        return this;
    }

    @Override
    public NativePayload<T> retain() {
        return retain(1);
    }

    @Override
    public NativePayload<T> retain(int inc) {
        super.retain(inc);
        return this;
    }

    @Override
    public boolean release() {
        return this.release(1);
    }

    @Override
    public boolean release(int decrement) {
        return super.release(decrement);
    }

    @Override
    public String bodyToString(boolean release) {
        try {
            return nativeObject.toString();
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    @Override
    public JSONArray bodyToJsonArray(boolean release) {
        try {
            if (nativeObject == null) {
                return new JSONArray();
            }
            if (nativeObject instanceof JSONArray) {
                return ((JSONArray)nativeObject);
            }
            List<Object> collection;
            if (nativeObject instanceof List) {
                collection = ((List<Object>)nativeObject);
            } else if (nativeObject instanceof Collection) {
                collection = new ArrayList<>(((Collection<Object>)nativeObject));
            } else if (nativeObject instanceof Object[]) {
                collection = Arrays.asList(((Object[])nativeObject));
            } else {
                throw new UnsupportedOperationException("body is not arry");
            }
            return new JSONArray(collection);
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    @Override
    public JSONObject bodyToJson(boolean release) {
        try {
            if (nativeObject == null) {
                return new JSONObject();
            }
            if (nativeObject instanceof IJsonable) {
                return ((IJsonable)nativeObject).toJson();
            }
            return FastBeanCopier.copy(nativeObject, JSONObject::new);
        } finally {
            if (release) {
                ReferenceCountUtil.safeRelease(this);
            }
        }
    }

    @Override
    public String toString() {
        return nativeObject == null ? "null" : nativeObject.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        if (refCnt() > 0) {
            log.warn(
                "payload {} was not release properly, release() was not called before it's garbage-collected. refCnt={}",
                nativeObject, refCnt());
        }
        super.finalize();
    }
}
