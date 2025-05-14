package com.sipa.boot.java8.common.mail.property;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 * @date 2020/12/14
 */
@ConfigurationProperties(prefix = "sipa.boot.mail")
@Component
public class MailProperties {
    private int from;

    private String host;

    private Integer port;

    private String username;

    private String password;

    private boolean auth = true;

    private int encrypt;

    private boolean debug;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        MailProperties that = (MailProperties)o;

        return new EqualsBuilder().append(from, that.from)
            .append(auth, that.auth)
            .append(encrypt, that.encrypt)
            .append(debug, that.debug)
            .append(host, that.host)
            .append(port, that.port)
            .append(username, that.username)
            .append(password, that.password)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(from)
            .append(host)
            .append(port)
            .append(username)
            .append(password)
            .append(auth)
            .append(encrypt)
            .append(debug)
            .toHashCode();
    }

    public static final class MailPropertiesBuilder {
        private int from;

        private String host;

        private Integer port;

        private String username;

        private String password;

        private boolean auth = true;

        private int encrypt;

        private boolean debug;

        private MailPropertiesBuilder() {}

        public static MailPropertiesBuilder aMailProperties() {
            return new MailPropertiesBuilder();
        }

        public MailPropertiesBuilder withFrom(int from) {
            this.from = from;
            return this;
        }

        public MailPropertiesBuilder withHost(String host) {
            this.host = host;
            return this;
        }

        public MailPropertiesBuilder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public MailPropertiesBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public MailPropertiesBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public MailPropertiesBuilder withAuth(boolean auth) {
            this.auth = auth;
            return this;
        }

        public MailPropertiesBuilder withEncrypt(int encrypt) {
            this.encrypt = encrypt;
            return this;
        }

        public MailPropertiesBuilder withDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public MailProperties build() {
            MailProperties mailProperties = new MailProperties();
            mailProperties.setFrom(from);
            mailProperties.setHost(host);
            mailProperties.setPort(port);
            mailProperties.setUsername(username);
            mailProperties.setPassword(password);
            mailProperties.setAuth(auth);
            mailProperties.setEncrypt(encrypt);
            mailProperties.setDebug(debug);
            return mailProperties;
        }
    }
}
