package com.sipa.boot.java8.data.es.entity;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author caszhou
 * @date 2021/10/19
 */
@Document(indexName = "demo")
public class Demo {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String user;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime postDate;

    @Field(type = FieldType.Text)
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
            .append("user", user)
            .append("postDate", postDate)
            .append("message", message)
            .toString();
    }

    public static final class DemoBuilder {
        private String id;

        private String user;

        private LocalDateTime postDate;

        private String message;

        private DemoBuilder() {}

        public static DemoBuilder aDemo() {
            return new DemoBuilder();
        }

        public DemoBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DemoBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public DemoBuilder withPostDate(LocalDateTime postDate) {
            this.postDate = postDate;
            return this;
        }

        public DemoBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Demo build() {
            Demo demo = new Demo();
            demo.setId(id);
            demo.setUser(user);
            demo.setPostDate(postDate);
            demo.setMessage(message);
            return demo;
        }
    }
}
