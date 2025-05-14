package com.sipa.boot.java8.data.iotdb.value.tsdb;

import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.data.iotdb.sql.SelectBuilder;
import com.sipa.boot.java8.data.iotdb.sql.base.ISqlBuilder;

/**
 * @author caszhou
 * @date 2021/6/25
 */
public enum ETsdbDownSample implements ISqlBuilder {
    NONE() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().column(query.getMetrics())
                .table(query.getCollectionId())
                .where(timeStart(query.getStart()))
                .and(timeEnd(query.getEnd()));
        }
    },
    NEWEST() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().last().column(query.getMetrics()).table(query.getCollectionId());
        }
    },
    FIRST() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().column(query.getMetrics())
                .table(query.getCollectionId())
                .where(timeStart(query.getStart()))
                .and(timeEnd(query.getEnd()))
                .orderBy(true)
                .limit(SipaBootCommonConstants.Number.INT_1);
        }
    },
    LAST() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().column(query.getMetrics())
                .table(query.getCollectionId())
                .where(timeStart(query.getStart()))
                .and(timeEnd(query.getEnd()))
                .orderBy(false)
                .limit(SipaBootCommonConstants.Number.INT_1);
        }
    },
    M_MEDIAN() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().column(firstAgg(query.getMetrics()))
                .table(query.getCollectionId())
                .groupBy(dateRangeGroup(query.getStart(), query.getEnd(), query.getDownSample()))
                .withoutNullAll();
        }
    },
    S_MEDIAN() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().column(firstAgg(query.getMetrics()))
                .table(query.getCollectionId())
                .groupBy(dateRangeGroup(query.getStart(), query.getEnd(), query.getDownSample()))
                .withoutNullAll();
        }
    },
    D_FIRST() {
        public SelectBuilder doGetSelectBuilder(TsdbQuery query) {
            return new SelectBuilder().column(firstAgg(query.getMetrics()))
                .table(query.getCollectionId())
                .groupBy(dateRangeGroup(query.getStart(), query.getEnd(), query.getDownSample()))
                .withoutNullAll();
        }
    };
}
