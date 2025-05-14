package com.sipa.boot.java8.iot.core.dict;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.sipa.boot.java8.iot.core.dict.base.IDictDefine;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;

/**
 * @author caszhou
 * @date 2021/10/5
 */
public class DefaultDictDefine implements IDictDefine {
    private static final long serialVersionUID = 20094004707177152L;

    private String id;

    private String alias;

    private String comments;

    private List<? extends IEnumDict<?>> items;

    public DefaultDictDefine() {
    }

    public DefaultDictDefine(String id, String alias, String comments, List<? extends IEnumDict<?>> items) {
        this.id = id;
        this.alias = alias;
        this.comments = comments;
        this.items = items;
    }

    public static long getSerialVersionUid() {
        return serialVersionUID;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public List<? extends IEnumDict<?>> getItems() {
        return items;
    }

    public void setItems(List<? extends IEnumDict<?>> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefaultDictDefine that = (DefaultDictDefine)o;

        return new EqualsBuilder().append(id, that.id)
            .append(alias, that.alias)
            .append(comments, that.comments)
            .append(items, that.items)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(alias).append(comments).append(items).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
            .append("alias", alias)
            .append("comments", comments)
            .append("items", items)
            .toString();
    }
}
