package com.sipa.boot.java8.data.iotdb.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.MeasurementUtils;
import com.sipa.boot.java8.data.iotdb.sql.base.AbstractSqlBuilder;
import com.sipa.boot.java8.data.iotdb.util.IotdbUtils;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
public class SelectBuilder extends AbstractSqlBuilder {
    private static final Log LOGGER = LogFactory.get(SelectBuilder.class);

    private boolean last;

    private final List<String> columns = new ArrayList<>();

    private final List<String> tables = new ArrayList<>();

    private final List<String> wheres = new ArrayList<>();

    private final List<String> groupBys = new ArrayList<>();

    private final List<String> fills = new ArrayList<>();

    private String orderBy = null;

    private int limit = 0;

    private int offset = 0;

    private int slimit = 0;

    private int soffset = 0;

    private boolean alignByDevice = false;

    private boolean withoutNullAny = false;

    private boolean withoutNullAll = false;

    public SelectBuilder() {
    }

    public SelectBuilder(String table) {
        tables.add(table);
    }

    public static String withSg(String name) {
        return IotdbUtils.getIotdbProperties().getOther().getStorageGroups().get(0) + SipaBootCommonConstants.POINT
            + name;
    }

    public SelectBuilder last() {
        this.last = true;
        return this;
    }

    public SelectBuilder column(String name) {
        columns.add(MeasurementUtils.transform(name));
        return this;
    }

    public SelectBuilder column(List<String> names) {
        for (String name : names) {
            columns.add(MeasurementUtils.transform(name));
        }
        return this;
    }

    public SelectBuilder column(String name, boolean groupBy) {
        columns.add(MeasurementUtils.transform(name));
        if (groupBy) {
            groupBys.add(name);
        }
        return this;
    }

    public SelectBuilder table(String table) {
        return table(table, true);
    }

    public SelectBuilder table(String table, boolean appendSg) {
        if (appendSg) {
            tables.add(SelectBuilder.withSg(table));
        } else {
            tables.add(table);
        }
        return this;
    }

    public SelectBuilder where(String expr) {
        wheres.add(expr);
        return this;
    }

    /**
     * Alias for {@link #where(String)}.
     */
    public SelectBuilder and(String expr) {
        return where(expr);
    }

    public SelectBuilder groupBy(String expr) {
        groupBys.add(expr);
        return this;
    }

    public SelectBuilder fill(String expr) {
        fills.add(expr);
        return this;
    }

    public SelectBuilder orderBy(boolean ascending) {
        if (ascending) {
            orderBy = "time asc";
        } else {
            orderBy = "time desc";
        }
        return this;
    }

    public SelectBuilder limit(int limit) {
        return limitOffset(limit, 0);
    }

    public SelectBuilder limitOffset(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
        return this;
    }

    public SelectBuilder slimit(int slimit) {
        return slimitSoffset(slimit, 0);
    }

    public SelectBuilder slimitSoffset(int slimit, int soffset) {
        this.slimit = slimit;
        this.soffset = soffset;
        return this;
    }

    public SelectBuilder alignByDevice() {
        this.alignByDevice = true;
        return this;
    }

    public SelectBuilder withoutNullAny() {
        this.withoutNullAny = true;
        this.withoutNullAll = false;
        return this;
    }

    public SelectBuilder withoutNullAll() {
        this.withoutNullAny = false;
        this.withoutNullAll = true;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("select ");

        if (last) {
            sb.append("last ");
        }

        if (columns.size() == 0) {
            sb.append("*");
        } else {
            appendList(sb, columns, "", ", ");
        }

        appendList(sb, tables, " from ", ", ");
        appendList(sb, wheres, " where ", " and ");
        appendList(sb, groupBys, " group by ", ", ");
        appendList(sb, fills, " fill ", ", ");

        if (StringUtils.isNotBlank(orderBy)) {
            sb.append(" order by ").append(orderBy);
        }

        if (limit > 0) {
            sb.append(" limit ").append(limit);
        }
        if (offset > 0) {
            sb.append(", ").append(offset);
        }

        if (slimit > 0) {
            sb.append(" limit ").append(slimit);
        }
        if (soffset > 0) {
            sb.append(", ").append(soffset);
        }

        // todo rd-239 https://github.com/apache/iotdb/issues/3449
        // if (alignByDevice) {
        // sb.append(" align by device");
        // } else {
        // sb.append(" disable align");
        // }

        if (withoutNullAny) {
            sb.append(" without null any");
        }

        if (withoutNullAll) {
            sb.append(" without null all");
        }

        String sql = sb.toString();
        LOGGER.info(sql);
        return sql;
    }
}
