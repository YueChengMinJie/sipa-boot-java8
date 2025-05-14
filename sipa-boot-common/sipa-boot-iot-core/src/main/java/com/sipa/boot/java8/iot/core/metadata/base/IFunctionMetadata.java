package com.sipa.boot.java8.iot.core.metadata.base;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public interface IFunctionMetadata extends IMetadata, IJsonable {
    /**
     * @return 输入参数定义
     */
    @NotNull
    List<IPropertyMetadata> getInputs();

    /**
     * @return 输出类型，为null表示无输出
     */
    @Nullable
    IDataType getOutput();

    /**
     * @return 是否异步
     */
    boolean isAsync();

    default IFunctionMetadata merge(IFunctionMetadata another, IMergeOption... option) {
        throw new UnsupportedOperationException("不支持功能物模型合并");
    }
}
