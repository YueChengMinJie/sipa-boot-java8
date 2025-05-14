package com.sipa.boot.java8.iot.core.dict;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;

/**
 * @author caszhou
 * @date 2022/3/11
 */
public class DictTest {
    @Test
    public void test() {
        ETest one = ETest.ONE;
        ETest two = ETest.TWO;
        ETest three = ETest.Three;
        Assertions.assertThat(one.index()).isEqualTo(0L);
        Assertions.assertThat(two.getMask()).isEqualTo(2L);
        Assertions.assertThat(one.eq(one)).isEqualTo(true);
        Assertions.assertThat(one.eq(1)).isEqualTo(true);
        Assertions.assertThat(one.eq("1")).isEqualTo(true);
        Assertions.assertThat(one.eq("one")).isEqualTo(true);
        Assertions.assertThat(one.eq("ONE")).isEqualTo(true);
        Assertions.assertThat(one.eq("OnE")).isEqualTo(true);
        Assertions.assertThat(one.eq(new String[] {"OnE", "Two"})).isEqualTo(true);
        Assertions.assertThat(one.eq(Lists.newArrayList("OnE", "Two"))).isEqualTo(true);
        Assertions.assertThat(one.eq(new HashMap<String, String>() {
            {
                put("value", "one");
            }
        })).isEqualTo(true);
        Assertions.assertThat(one.eq(new HashMap<String, String>() {
            {
                put("text", "one");
            }
        })).isEqualTo(true);
        Assertions.assertThat(one.eq(new HashMap<String, String>() {
            {
                put("test", "one");
            }
        })).isEqualTo(false);
        Assertions.assertThat(one.in(1)).isEqualTo(true);
        Assertions.assertThat(one.in(one)).isEqualTo(true);
        Assertions.assertThat(one.isWriteJsonObjectEnabled()).isEqualTo(true);
        Assertions.assertThat(one.getI18nCode()).isEqualTo("One");
        Assertions.assertThat(one.getI18nMessage(null)).isEqualTo("One");
        Assertions.assertThat(IEnumDict.find(ETest.class, e -> Objects.equals(e.getValue(), "1")))
            .isEqualTo(Optional.of(one));
        Assertions.assertThat(IEnumDict.findList(ETest.class, e -> true))
            .isEqualTo(Lists.newArrayList(one, two, three));
        Assertions.assertThat(IEnumDict.findByValue(ETest.class, "1")).isEqualTo(Optional.of(one));
        Assertions.assertThat(IEnumDict.findByText(ETest.class, "one")).isEqualTo(Optional.of(one));
        Assertions.assertThat(IEnumDict.find(ETest.class, one)).isEqualTo(Optional.of(one));
        Assertions.assertThat(IEnumDict.toMask(one)).isEqualTo(1);
        Assertions.assertThat(IEnumDict.toMask(two)).isEqualTo(2);
        Assertions.assertThat(IEnumDict.toMask(three)).isEqualTo(4);
    }

    @Test
    public void testService() {
    }

    private enum ETest implements IEnumDict<String> {
        ONE("1", "One"), TWO("2", "Two"), Three("3", "Three");

        private final String code;

        private final String desc;

        ETest(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Override
        public String getValue() {
            return this.code;
        }

        @Override
        public String getText() {
            return this.desc;
        }
    }
}
