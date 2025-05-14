package com.sipa.boot.java8.data.iotdb.property;

import java.util.List;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class IotdbOtherProperties {
    private List<String> storageGroups;

    public List<String> getStorageGroups() {
        return storageGroups;
    }

    public void setStorageGroups(List<String> storageGroups) {
        this.storageGroups = storageGroups;
    }
}
