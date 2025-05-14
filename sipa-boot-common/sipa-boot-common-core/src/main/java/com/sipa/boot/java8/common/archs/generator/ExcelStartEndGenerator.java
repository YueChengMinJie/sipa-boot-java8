package com.sipa.boot.java8.common.archs.generator;

import com.sipa.boot.java8.common.archs.generator.base.Generator;
import com.sipa.boot.java8.common.archs.generator.pojo.ExcelStartEnd;

/**
 * @author zhouxiajie
 * @date 2020/8/6
 */
public class ExcelStartEndGenerator implements Generator<ExcelStartEnd> {
    private Integer size;

    private Integer batch;

    private Integer count;

    private Integer current;

    public ExcelStartEndGenerator(Integer size, Integer batch) {
        this.size = size;
        this.batch = batch;
        this.current = 0;
        this.count = 0;
    }

    @Override
    public ExcelStartEnd next() {
        int next = current + batch;

        ExcelStartEnd.ExcelStartEndBuilder builder =
            ExcelStartEnd.ExcelStartEndBuilder.anExcelStartEnd().withStart(current).withIndex(++count);
        if (next > size) {
            builder.withEnd(size);

            current = size;
        } else {
            builder.withEnd(next);

            current = next;
        }

        return builder.build();
    }

    public boolean hasNext() {
        return size > 0 && current > -1 && current < size;
    }
}
