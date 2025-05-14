package com.sipa.boot.java8.iot.core.dict.base;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public interface IItemDefine extends IEnumDict<String> {
    @Override
    String getText();

    @Override
    String getValue();

    @Override
    String getComments();

    int getOrdinal();

    @Override
    default int ordinal() {
        return getOrdinal();
    }
}
