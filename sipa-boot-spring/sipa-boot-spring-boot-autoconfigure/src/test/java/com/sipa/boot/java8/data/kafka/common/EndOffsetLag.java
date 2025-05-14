package com.sipa.boot.java8.data.kafka.common;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
public class EndOffsetLag {
    private Long end;

    private Long offset;

    private Long lag;

    public EndOffsetLag(Long end, Long offset) {
        this.end = end;

        this.offset = offset;

        long lag = end - offset;
        if (lag < 0) {
            lag = 0;
        }
        this.lag = lag;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getLag() {
        return lag;
    }

    public void setLag(Long lag) {
        this.lag = lag;
    }
}
