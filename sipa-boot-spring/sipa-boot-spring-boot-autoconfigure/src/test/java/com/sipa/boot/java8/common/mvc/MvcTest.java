package com.sipa.boot.java8.common.mvc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.java8.common.mvc.config.OrikaMapperAutoConfiguration;

import ma.glasnost.orika.MapperFacade;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OrikaMapperAutoConfiguration.class})
public class MvcTest {
    @Autowired
    private MapperFacade mapperFacade;

    @Test
    @Ignore
    public void testMapperFacade() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("test", "2");
        assertThat(mapperFacade.map(map, MapToType.class).getTest()).isEqualTo("2");
    }

    public static class MapToType {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }
}
