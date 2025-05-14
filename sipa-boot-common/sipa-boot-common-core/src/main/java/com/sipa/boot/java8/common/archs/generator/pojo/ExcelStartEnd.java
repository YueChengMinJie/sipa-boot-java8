package com.sipa.boot.java8.common.archs.generator.pojo;

/**
 * @author zhouxiajie
 * @date 2020/8/6
 */
public class ExcelStartEnd {
    private int start;

    private int end;

    private int index;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static final class ExcelStartEndBuilder {
        private int start;

        private int end;

        private int index;

        private ExcelStartEndBuilder() {}

        public static ExcelStartEndBuilder anExcelStartEnd() {
            return new ExcelStartEndBuilder();
        }

        public ExcelStartEndBuilder withStart(int start) {
            this.start = start;
            return this;
        }

        public ExcelStartEndBuilder withEnd(int end) {
            this.end = end;
            return this;
        }

        public ExcelStartEndBuilder withIndex(int index) {
            this.index = index;
            return this;
        }

        public ExcelStartEnd build() {
            ExcelStartEnd excelStartEnd = new ExcelStartEnd();
            excelStartEnd.setStart(start);
            excelStartEnd.setEnd(end);
            excelStartEnd.setIndex(index);
            return excelStartEnd;
        }
    }
}
