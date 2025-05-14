package com.sipa.boot.java8.data.iotdb.transfer;

import com.sipa.boot.java8.data.iotdb.sql.SelectBuilder;
import com.sipa.boot.java8.data.iotdb.value.tsdb.ETsdbDownSample;
import com.sipa.boot.java8.data.iotdb.value.tsdb.TsdbQuery;

/**
 * @author caszhou
 * @date 2021/6/26
 */
public class IotdbSqlBuilderTransfer {
    public static SelectBuilder transfer(TsdbQuery query) {
        ETsdbDownSample.NONE.check(query);
        return query.getDownSample().getSelectBuilder(query);
    }
}
