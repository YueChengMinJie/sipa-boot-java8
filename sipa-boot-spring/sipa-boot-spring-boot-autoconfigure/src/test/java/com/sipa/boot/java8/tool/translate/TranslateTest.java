package com.sipa.boot.java8.tool.translate;

import com.sipa.boot.java8.tool.translate.context.TranslateContext;
import com.sipa.boot.java8.tool.translate.enumerate.EYoudaoTranslateCode;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
// @ActiveProfiles("translate")
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = {TranslateAutoConfiguration.class, RestTemplateAutoConfiguration.class})
public class TranslateTest {
    // @Test
    public void testTranslate() {
        System.out.println(TranslateContext.translate(EYoudaoTranslateCode.ZH, EYoudaoTranslateCode.EN, "人员组织管理"));
    }
}
