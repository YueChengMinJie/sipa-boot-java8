package com.sipa.boot.java8.common.mvc.endpoint;

import com.sipa.boot.java8.common.bos.SipaBootServiceResult;
import com.sipa.boot.java8.common.dtos.ResponseWrapper;
import com.sipa.boot.java8.common.exceptions.SipaBootServiceException;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author zhouxiajie
 * @date 2019-01-23
 */
public abstract class AbstractEndpoint {
    protected final Log logger = LogFactory.get(this.getClass());

    /**
     * use BadRequestException instead.
     */
    @Deprecated
    protected <T> ResponseWrapper<T> getErrorResponseWrapper(SipaBootServiceException e) {
        SipaBootServiceResult<?> isr = e.getSipaBootServiceResult();
        return ResponseWrapper.errorOf(isr.getMessage());
    }
}
