package com.sipa.boot.java8.common.auth.model;

/**
 * @author zhouxiajie
 * @date 2018/2/6
 */
public class LoginRequest {
    private String username;

    private String password;

    private Integer type;

    public LoginRequest() {
    }

    private LoginRequest(Builder builder) {
        setUsername(builder.username);
        setPassword(builder.password);
        setType(builder.type);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static final class Builder {
        private String username;

        private String password;

        private Integer type;

        private Builder() {}

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder type(Integer val) {
            type = val;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(this);
        }
    }
}
