package com.sipa.boot.java8.jpush;

import com.sipa.boot.java8.common.common.jpush.component.JPushApi;
import com.sipa.boot.java8.common.common.jpush.vo.PushMessage;

/**
 * @author sjm
 * @date 2021年11月30日 14点43分
 */
// @ActiveProfiles("jpush")
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = {JPushAutoConfiguration.class})
public class JpushTest {
    // @Autowired
    private JPushApi jPushApi;

    // @Test
    public void test() {
        PushMessage pushMessage = new PushMessage();
        pushMessage.setContent("sjmTest");
        jPushApi.pushToAll(pushMessage);
    }
}
