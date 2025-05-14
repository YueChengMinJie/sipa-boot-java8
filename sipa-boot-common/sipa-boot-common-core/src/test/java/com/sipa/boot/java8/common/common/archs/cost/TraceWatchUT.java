package com.sipa.boot.java8.common.common.archs.cost;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.assertj.core.util.Maps;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipa.boot.java8.common.archs.cost.TraceHolder;
import com.sipa.boot.java8.common.archs.cost.TraceWatch;
import com.sipa.boot.java8.common.archs.cost.TraceWatchFactory;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.util.Console;
import com.sipa.boot.java8.common.utils.JsonUtils;
import com.sipa.boot.java8.common.utils.ThreadUtils;

/**
 * @author zhouxiajie
 * @date 2021/4/12
 */
public class TraceWatchUT {
    @Test
    public void testCost() {
        TraceWatch traceWatch = TraceWatchFactory.newTraceWatch();
        String taskName = "hbaseQueryCost";

        TraceHolder.run(traceWatch, taskName, () -> {
            ThreadUtils.sleepQuitly(TimeUnit.SECONDS.toMillis(SipaBootCommonConstants.Number.INT_1));
            return Maps.newHashMap("test", "test");
        });

        assertThat(traceWatch.getTaskMap()).hasSize(1);
        TraceWatch.TaskInfo taskInfo = traceWatch.getTaskMap().get(taskName).get(0);
        assertThat(taskInfo.getCost()).isGreaterThanOrEqualTo(1000L);
        assertThat(taskInfo.getMetadata().get("test")).isEqualTo("test");

        Console.log(JsonUtils.writeValueAsString(new ObjectMapper(), traceWatch.getTaskMap()));
    }
}
