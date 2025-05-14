package com.sipa.boot.java8.data.mysql.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sipa.boot.java8.common.bos.SipaBootServiceResult;
import com.sipa.boot.java8.common.exceptions.SipaBootServiceException;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.services.IMessageService;

/**
 * @author zhouxiajie
 * @date 2019-02-02
 */
public class SipaBootServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IService<T> {
    protected final Log logger = LogFactory.get(this.getClass());

    protected SipaBootServiceException getSipaBootServiceException(IMessageService messageService, String code) {
        return new SipaBootServiceException(
            SipaBootServiceResult.newBuilder().message(messageService.getMessage(code)).build());
    }

    protected T selectOne(Wrapper<T> queryWrapper) {
        return this.getOne(queryWrapper, false);
    }
}
