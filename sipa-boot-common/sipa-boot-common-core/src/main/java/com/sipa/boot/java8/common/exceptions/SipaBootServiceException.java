package com.sipa.boot.java8.common.exceptions;

import com.sipa.boot.java8.common.bos.SipaBootServiceResult;

/**
 * use BadRequestException instead.
 *
 * @author zhouxiajie
 * @date 2018/11/16
 */
@Deprecated
public class SipaBootServiceException extends Exception {
    private static final long serialVersionUID = 7243161273272949536L;

    private SipaBootServiceResult<?> sipaBootServiceResult;

    public SipaBootServiceException(SipaBootServiceResult<?> sipaBootServiceResult) {
        this.sipaBootServiceResult = sipaBootServiceResult;
    }

    public SipaBootServiceResult<?> getSipaBootServiceResult() {
        return sipaBootServiceResult;
    }
}
