package com.sipa.boot.java8.iot.core.event;

import javax.annotation.Nonnull;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.event.base.IPayload;
import com.sipa.boot.java8.iot.core.util.RecyclerUtils;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ByteBufPayload implements IPayload {
    private static final Log log = LogFactory.get(ByteBufPayload.class);

    public static boolean POOL_ENABLED =
        Boolean.parseBoolean(System.getProperty("sipa.boot.eventbus.payload.pool.enabled", "true"));

    private static final Recycler<ByteBufPayload> RECYCLER =
        RecyclerUtils.newRecycler(ByteBufPayload.class, ByteBufPayload::new);

    private final Recycler.Handle<ByteBufPayload> handle;

    ByteBufPayload(Recycler.Handle<ByteBufPayload> handle) {
        this.handle = handle;
    }

    private ByteBuf body;

    private String caller;

    public static IPayload of(ByteBuf body) {
        ByteBufPayload payload;
        if (POOL_ENABLED) {
            try {
                payload = RECYCLER.get();
            } catch (Exception e) {
                payload = new ByteBufPayload(null);
            }
        } else {
            payload = new ByteBufPayload(null);
        }
        if (log.isTraceEnabled()) {
            for (StackTraceElement element : (new Exception()).getStackTrace()) {
                if (!"com.sipa.boot.java8.iot.core.event.base.IPayload".equals(element.getClassName())
                    && !"com.sipa.boot.java8.iot.core.event.ByteBufPayload".equals(element.getClassName())
                    && !element.getClassName().startsWith("com.sipa.boot.java8.iot.core.codec")) {
                    payload.caller = element.toString();
                    break;
                }
            }
        }
        payload.body = body;
        return payload;
    }

    @Override
    public boolean release() {
        return handleRelease(ReferenceCountUtil.release(body));
    }

    @Override
    public boolean release(int dec) {
        return handleRelease(ReferenceCountUtil.release(body, dec));
    }

    @Override
    public IPayload retain(int inc) {
        ReferenceCountUtil.retain(body, inc);
        return this;
    }

    @Override
    public IPayload retain() {
        ReferenceCountUtil.retain(body);
        return this;
    }

    @Nonnull
    @Override
    public ByteBuf getBody() {
        return body;
    }

    protected boolean handleRelease(boolean release) {
        if (release && handle != null) {
            body = null;
            caller = null;
            handle.recycle(this);
        }
        return release;
    }

    @Override
    protected void finalize() throws Throwable {
        int refCnt = ReferenceCountUtil.refCnt(body);
        if (refCnt > 0) {
            log.trace(
                "payload {} was not release properly, release() was not called before it's garbage-collected. refCnt={}. caller: {}",
                body, refCnt, caller);
        }
        super.finalize();
    }

    @Override
    public String toString() {
        return "ByteBufPayload{" + "body=" + body + ", caller='" + caller + '\'' + '}';
    }
}
