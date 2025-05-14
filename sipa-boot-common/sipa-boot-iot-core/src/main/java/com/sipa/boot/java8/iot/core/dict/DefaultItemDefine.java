package com.sipa.boot.java8.iot.core.dict;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.sipa.boot.java8.iot.core.dict.base.IItemDefine;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public class DefaultItemDefine implements IItemDefine {
    private String text;

    private String value;

    private String comments;

    private int ordinal;

    public DefaultItemDefine() {
    }

    public DefaultItemDefine(String text, String value, String comments, int ordinal) {
        this.text = text;
        this.value = value;
        this.comments = comments;
        this.ordinal = ordinal;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultItemDefine that = (DefaultItemDefine)o;

        return new EqualsBuilder().append(ordinal, that.ordinal)
            .append(text, that.text)
            .append(value, that.value)
            .append(comments, that.comments)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(text).append(value).append(comments).append(ordinal).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("text", text)
            .append("value", value)
            .append("comments", comments)
            .append("ordinal", ordinal)
            .toString();
    }

    public static final class DefaultItemDefineBuilder {
        private String text;

        private String value;

        private String comments;

        private int ordinal;

        private DefaultItemDefineBuilder() {}

        public static DefaultItemDefineBuilder aDefaultItemDefine() {
            return new DefaultItemDefineBuilder();
        }

        public DefaultItemDefineBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public DefaultItemDefineBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public DefaultItemDefineBuilder withComments(String comments) {
            this.comments = comments;
            return this;
        }

        public DefaultItemDefineBuilder withOrdinal(int ordinal) {
            this.ordinal = ordinal;
            return this;
        }

        public DefaultItemDefine build() {
            DefaultItemDefine defaultItemDefine = new DefaultItemDefine();
            defaultItemDefine.setText(text);
            defaultItemDefine.setValue(value);
            defaultItemDefine.setComments(comments);
            defaultItemDefine.setOrdinal(ordinal);
            return defaultItemDefine;
        }
    }
}
