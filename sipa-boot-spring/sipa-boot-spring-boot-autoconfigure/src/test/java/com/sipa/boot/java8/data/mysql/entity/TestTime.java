package com.sipa.boot.java8.data.mysql.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;

/**
 * @author zhouxiajie
 * @date 2021/4/27
 */
public class TestTime {
    @TableId(type = IdType.UUID)
    protected String id;

    protected String sn;

    protected LocalDateTime expireTime;

    protected LocalDateTime createTime;

    protected LocalDateTime updateTime;

    @Version
    protected Integer version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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

    public static final class TestTimeBuilder {
        protected String id;

        protected String sn;

        protected LocalDateTime expireTime;

        protected LocalDateTime createTime;

        protected LocalDateTime updateTime;

        protected Integer version;

        private TestTimeBuilder() {}

        public static TestTimeBuilder aTestTime() {
            return new TestTimeBuilder();
        }

        public TestTimeBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public TestTimeBuilder withSn(String sn) {
            this.sn = sn;
            return this;
        }

        public TestTimeBuilder withExpireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
            return this;
        }

        public TestTimeBuilder withCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public TestTimeBuilder withUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public TestTimeBuilder withVersion(Integer version) {
            this.version = version;
            return this;
        }

        public TestTime build() {
            TestTime testTime = new TestTime();
            testTime.setSn(sn);
            testTime.setExpireTime(expireTime);
            testTime.createTime = this.createTime;
            testTime.id = this.id;
            testTime.version = this.version;
            testTime.updateTime = this.updateTime;
            return testTime;
        }
    }
}
