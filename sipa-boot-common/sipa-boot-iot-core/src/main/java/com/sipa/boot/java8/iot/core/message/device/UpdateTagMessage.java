package com.sipa.boot.java8.iot.core.message.device;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sipa.boot.java8.iot.core.enumerate.EMessageType;
import com.sipa.boot.java8.iot.core.message.CommonDeviceMessage;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class UpdateTagMessage extends CommonDeviceMessage {
    private Map<String, Object> tags;

    public Map<String, Object> getTags() {
        return tags == null ? Collections.emptyMap() : tags;
    }

    public UpdateTagMessage tag(String tag, Object value) {
        if (tags == null) {
            tags = new HashMap<>(16);
        }
        tags.put(tag, value);
        return this;
    }

    public UpdateTagMessage tags(Map<String, Object> tags) {
        if (this.tags == null) {
            this.tags = tags;
            return this;
        }
        this.tags.putAll(tags);
        return this;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.UPDATE_TAG;
    }

    @Override
    public void fromJson(JSONObject jsonObject) {
        super.fromJson(jsonObject);
        this.tags = jsonObject.getJSONObject("tags");
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }
}
