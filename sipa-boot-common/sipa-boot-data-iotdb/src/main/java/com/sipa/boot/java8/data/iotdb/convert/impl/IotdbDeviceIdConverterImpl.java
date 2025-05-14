package com.sipa.boot.java8.data.iotdb.convert.impl;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import com.aliyun.hitsdb.client.value.request.Point;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.constants.SipaBootTsdbConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.data.iotdb.convert.base.IIotdbDeviceIdConverter;
import com.sipa.boot.java8.data.iotdb.property.IotdbProperties;

/**
 * @author zhouxiajie
 * @date 2021/5/28
 */
@Component("defaultDeviceConverter")
public class IotdbDeviceIdConverterImpl implements IIotdbDeviceIdConverter {
    private static final Log LOGGER = LogFactory.get(IotdbDeviceIdConverterImpl.class);

    private final IotdbProperties iotdbProperties;

    public IotdbDeviceIdConverterImpl(IotdbProperties iotdbProperties) {
        this.iotdbProperties = iotdbProperties;
    }

    @Override
    public String covert(Point point) {
        if (Objects.nonNull(point)) {
            Map<String, String> tags = point.getTags();
            if (MapUtils.isNotEmpty(tags)) {
                return getSg() + SipaBootCommonConstants.POINT + tags.get(SipaBootTsdbConstants.TagName.COLLECTION_ID);
            } else {
                LOGGER.warn("Tags is empty.");
            }
        } else {
            LOGGER.warn("Point is null.");
        }
        return null;
    }

    private String getSg() {
        return iotdbProperties.getOther().getStorageGroups().get(0);
    }
}
