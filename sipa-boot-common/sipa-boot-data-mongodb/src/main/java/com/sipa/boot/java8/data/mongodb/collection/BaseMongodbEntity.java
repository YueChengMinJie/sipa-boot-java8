package com.sipa.boot.java8.data.mongodb.collection;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhouxiajie
 * @since 2020/3/16 13:03
 */
public abstract class BaseMongodbEntity {
    @Id
    @ApiModelProperty("主键")
    protected String id;

    @ApiModelProperty("创建时间")
    protected LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    protected LocalDateTime updateTime;

    @Version
    @ApiModelProperty("乐观锁")
    protected Integer version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
