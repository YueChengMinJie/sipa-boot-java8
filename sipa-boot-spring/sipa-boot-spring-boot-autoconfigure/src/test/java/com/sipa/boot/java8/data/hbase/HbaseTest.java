package com.sipa.boot.java8.data.hbase;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.java8.data.hbase.util.HBaseUtils;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.utils.TimeUtils;
import com.sipa.boot.java8.common.utils.Utils;
import com.sipa.boot.java8.data.hbase.config.HBaseAutoConfiguration;

/**
 * @author zhouxiajie
 * @date 2021/1/27
 */
@ActiveProfiles("hbase")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HBaseAutoConfiguration.class)
public class HbaseTest {
    @Test
    public void testInit() throws Exception {
        String tableName = HBaseUtils.getTableName(SipaBootCommonConstants.HBase.TBOX_TABLE_NAME);
        String TBOX_MESSAGE = "{0}_{1}_{2}";
        LocalDate ld = LocalDateTime.now().toLocalDate();
        Long startTs = TimeUtils.ldt2ts(ld.atStartOfDay());
        Long endTs = TimeUtils.ldt2ts(ld.atTime(23, 59, 59));
        for (String iccid : IOUtils
            .readLines(new InputStreamReader(new FileInputStream("/Users/zhouxiajie/Downloads/tbox.csv")))) {
            String startRowKey = MessageFormat.format(TBOX_MESSAGE, iccid, "99", Utils.stringValueOf(startTs));
            String endRowKey = MessageFormat.format(TBOX_MESSAGE, iccid, "99", Utils.stringValueOf(endTs));
            HBaseUtils.count(tableName, startRowKey, endRowKey);
        }
    }
}
