package com.sipa.boot.java8.iot.core.device.base;

import java.io.Serializable;

import com.sipa.boot.java8.iot.core.message.codec.base.ITransport;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public interface IAuthenticationRequest extends Serializable {
    ITransport getTransport();
}
