package com.sipa.boot.java8.data.iotdb.sql.base;

import java.util.List;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public abstract class AbstractSqlBuilder {
    /**
     * Constructs a list of items with given separators.
     *
     * @param sql
     *            StringBuilder to which the constructed string will be appended.
     * @param list
     *            List of objects (usually strings) to join.
     * @param init
     *            String to be added to the start of the list, before any of the items.
     * @param sep
     *            Separator string to be added between items in the list.
     */
    protected void appendList(StringBuilder sql, List<?> list, String init, String sep) {
        boolean first = true;

        for (Object s : list) {
            if (first) {
                sql.append(init);
            } else {
                sql.append(sep);
            }
            sql.append(s);
            first = false;
        }
    }
}
