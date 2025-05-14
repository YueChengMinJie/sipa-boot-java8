package com.sipa.boot.java8.data.es.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author caszhou
 * @date 2021/10/19
 */
@Document(indexName = "platform-log")
public class DkLog {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Nested)
    private DkLogContext context;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DkLogContext getContext() {
        return context;
    }

    public void setContext(DkLogContext context) {
        this.context = context;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static final class DkLogBuilder {
        private String id;

        private String source;

        private String type;

        private DkLogContext context;

        private String content;

        private LocalDateTime createTime;

        private DkLogBuilder() {}

        public static DkLogBuilder aDkLog() {
            return new DkLogBuilder();
        }

        public DkLogBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DkLogBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public DkLogBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public DkLogBuilder withContext(DkLogContext context) {
            this.context = context;
            return this;
        }

        public DkLogBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public DkLogBuilder withCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public DkLog build() {
            DkLog dkLog = new DkLog();
            dkLog.setId(id);
            dkLog.setSource(source);
            dkLog.setType(type);
            dkLog.setContext(context);
            dkLog.setContent(content);
            dkLog.setCreateTime(createTime);
            return dkLog;
        }
    }
}
