package com.sipa.boot.java8.data.mysql.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.sipa.boot.java8.data.mysql.service.TestTimeServiceImpl;

/**
 * @author zhouxiajie
 * @date 2021/4/27
 */
@TestConfiguration
public class MysqlTestConfiguration {
    @Bean
    public TestTimeServiceImpl testTimeService() {
        return new TestTimeServiceImpl();
    }
}
