package com.sipa.boot.java8.data.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.sipa.boot.java8.data.mysql.config.MysqlTestConfiguration;
import com.sipa.boot.java8.data.mysql.config.SipaBootDynamicDataSourceAutoConfiguration;
import com.sipa.boot.java8.data.mysql.config.SipaBootMybatisPlusAutoConfiguration;
import com.sipa.boot.java8.data.mysql.entity.TestTime;
import com.sipa.boot.java8.data.mysql.service.TestTimeServiceImpl;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
@ActiveProfiles("mysql")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DynamicDataSourceAutoConfiguration.class, SipaBootDynamicDataSourceAutoConfiguration.class,
    SipaBootMybatisPlusAutoConfiguration.class, MybatisPlusAutoConfiguration.class, MysqlTestConfiguration.class})
public class MysqlTest {
    @Autowired
    private TestTimeServiceImpl testTimeService;

    @Test
    @Ignore
    public void testFieldOfTs() {
        assertThat(testTimeService).isNotNull();

        List<TestTime> list = testTimeService.list();
        assertThat(list).isNotEmpty();

        TestTime testTime = list.get(0);
        assertThat(testTimeService.updateById(TestTime.TestTimeBuilder.aTestTime()
            .withId(testTime.getId())
            .withVersion(testTime.getVersion())
            .withSn(testTime.getSn())
            .build())).isTrue();

        list = testTimeService.list();
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getExpireTime()).isNotNull();
    }
}
