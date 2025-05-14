package com.sipa.boot.java8.iot.core.metadata.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sipa.boot.java8.iot.core.metadata.ValidateResult;
import com.sipa.boot.java8.iot.core.metadata.base.IDataType;
import com.sipa.boot.java8.iot.core.metadata.type.base.AbstractType;

/**
 * @author caszhou
 * @date 2021/10/4
 */
public class EnumType extends AbstractType<EnumType> implements IDataType {
    public static final String ID = "enum";

    private volatile List<Element> elements;

    private boolean multi;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "枚举";
    }

    public EnumType multi(boolean multi) {
        this.multi = multi;
        return this;
    }

    @Override
    public ValidateResult validate(Object value) {
        if (elements == null) {
            return ValidateResult.fail("值[" + value + "]不在枚举中");
        }
        return elements.stream()
            .filter(ele -> match(value, ele))
            .findFirst()
            .map(e -> ValidateResult.success(e.value))
            .orElseGet(() -> ValidateResult.fail("值[" + value + "]不在枚举中"));
    }

    private boolean match(Object value, Element ele) {
        if (value instanceof Map) {
            // 适配map情况下的枚举信息
            @SuppressWarnings("all")
            Map<Object, Object> mapVal = ((Map<Object, Object>)value);
            return match(mapVal.getOrDefault("value", mapVal.get("id")), ele);
        }
        return ele.value.equals(String.valueOf(value)) || ele.text.equals(String.valueOf(value));
    }

    @Override
    public String format(Object value) {
        String stringVal = String.valueOf(value);
        if (elements == null) {
            return stringVal;
        }
        return elements.stream()
            .filter(ele -> ele.value.equals(String.valueOf(value)))
            .findFirst()
            .map(Element::getText)
            .orElse(stringVal);
    }

    public EnumType addElement(Element element) {
        if (elements == null) {
            synchronized (this) {
                if (elements == null) {
                    elements = new ArrayList<>();
                }
            }
        }
        elements.add(element);
        return this;
    }

    public static String getID() {
        return ID;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public static class Element {
        private String value;

        private String text;

        private String description;

        public Element() {}

        public Element(String value, String text, String description) {
            this.value = value;
            this.text = text;
            this.description = description;
        }

        public static Element of(String value, String text) {
            return new Element(value, text, null);
        }

        public static Element of(Map<String, String> map) {
            return new Element(map.get("value"), map.get("text"), map.get("description"));
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("value", value);
            map.put("text", text);
            map.put("description", description);

            return map;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
