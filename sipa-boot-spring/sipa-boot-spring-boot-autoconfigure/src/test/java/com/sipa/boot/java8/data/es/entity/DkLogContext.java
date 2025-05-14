package com.sipa.boot.java8.data.es.entity;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author caszhou
 * @date 2021/10/19
 */
public class DkLogContext {
    @Field(type = FieldType.Keyword)
    private String vin;

    @Field(type = FieldType.Keyword)
    private String vid;

    @Field(type = FieldType.Keyword)
    private String iccid;

    @Field(type = FieldType.Keyword)
    private String deviceId;

    @Field(type = FieldType.Keyword)
    private String mobile;

    @Field(type = FieldType.Keyword)
    private String userId;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static final class DkLogContextBuilder {
        private String vin;

        private String vid;

        private String iccid;

        private String deviceId;

        private String mobile;

        private String userId;

        private DkLogContextBuilder() {}

        public static DkLogContextBuilder aDkLogContext() {
            return new DkLogContextBuilder();
        }

        public DkLogContextBuilder withVin(String vin) {
            this.vin = vin;
            return this;
        }

        public DkLogContextBuilder withVid(String vid) {
            this.vid = vid;
            return this;
        }

        public DkLogContextBuilder withIccid(String iccid) {
            this.iccid = iccid;
            return this;
        }

        public DkLogContextBuilder withDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public DkLogContextBuilder withMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public DkLogContextBuilder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public DkLogContext build() {
            DkLogContext dkLogContext = new DkLogContext();
            dkLogContext.setVin(vin);
            dkLogContext.setVid(vid);
            dkLogContext.setIccid(iccid);
            dkLogContext.setDeviceId(deviceId);
            dkLogContext.setMobile(mobile);
            dkLogContext.setUserId(userId);
            return dkLogContext;
        }
    }
}
