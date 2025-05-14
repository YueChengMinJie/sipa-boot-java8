package com.sipa.boot.java8.common.zuul.service;

import java.util.List;

import com.sipa.boot.java8.common.dtos.ResponseWrapper;

/**
 * @author caszhou
 * @date 2021/9/1
 */
public interface IRoleService {
    ResponseWrapper<List<String>> roleByUri(String method, String url);
}
