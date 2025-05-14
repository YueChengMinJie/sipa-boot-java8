package com.sipa.boot.java8.iot.core.dict.base;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.exception.ValidationException;
import com.sipa.boot.java8.iot.core.i18n.LocaleUtils;

/**
 * 枚举字典,使用枚举来实现数据字典,可通过集成此接口来实现一些有趣的功能.<br>
 * ⚠️:如果使用了位运算来判断枚举,枚举数量不要超过64个,且顺序不要随意变动!<br>
 * ⚠️:如果要开启在反序列化json的时候,支持将对象反序列化枚举,由于fastJson目前的版本还不支持从父类获取注解,<br>
 * 所以需要在实现类上注解:<code>@JSONType(deserializer = EnumDict.EnumDictJSONDeserializer.class)</code>.
 *
 * @author caszhou
 * @date 2021/9/24
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@JSONType(deserializer = IEnumDict.EnumDictJsonDeserializer.class)
@JsonDeserialize(contentUsing = IEnumDict.EnumDictJsonDeserializer.class)
public interface IEnumDict<V> extends JSONSerializable {
    V getValue();

    String getText();

    int ordinal();

    boolean DEFAULT_WRITE_JSON_OBJECT = !Boolean.getBoolean("sipa.boot.enum.dict.disableWriteJsonNObject");

    default long index() {
        return ordinal();
    }

    default long getMask() {
        return 1L << index();
    }

    default boolean eq(Object v) {
        if (v == null) {
            return false;
        }
        if (v instanceof Object[]) {
            v = Arrays.stream(((Object[])v)).collect(Collectors.toList());
        }
        if (v instanceof Collection) {
            return ((Collection)v).stream().anyMatch(this::eq);
        }
        if (v instanceof Map) {
            v = ((Map)v).getOrDefault("value", ((Map)v).get("text"));
        }
        return this == v || getValue() == v || getValue().equals(v)
            || String.valueOf(getValue()).equalsIgnoreCase(String.valueOf(v))
            || getText().equalsIgnoreCase(String.valueOf(v));
    }

    default boolean in(long mask) {
        return (mask & getMask()) != 0;
    }

    default boolean in(IEnumDict<V>... dict) {
        return in(toMask(dict));
    }

    default String getComments() {
        return getText();
    }

    default boolean isWriteJsonObjectEnabled() {
        return DEFAULT_WRITE_JSON_OBJECT;
    }

    default String getI18nCode() {
        return getText();
    }

    default String getI18nMessage(Locale locale) {
        return LocaleUtils.resolveMessage(getI18nCode(), locale, getText());
    }

    @JsonValue
    default Object getWriteJsonObject() {
        if (isWriteJsonObjectEnabled()) {
            Map<String, Object> jsonObject = new HashMap<>(4);
            jsonObject.put("value", getValue());
            jsonObject.put("text", getI18nMessage(LocaleUtils.current()));
            return jsonObject;
        }
        return this.getValue();
    }

    @Override
    default void write(JSONSerializer jsonSerializer, Object o, Type type, int i) {
        if (isWriteJsonObjectEnabled()) {
            jsonSerializer.write(getWriteJsonObject());
        } else {
            jsonSerializer.write(getValue());
        }
    }

    static <T extends Enum & IEnumDict> Optional<T> find(Class<T> type, Predicate<T> predicate) {
        if (type.isEnum()) {
            for (T enumDict : type.getEnumConstants()) {
                if (predicate.test(enumDict)) {
                    return Optional.of(enumDict);
                }
            }
        }
        return Optional.empty();
    }

    static <T extends Enum & IEnumDict> List<T> findList(Class<T> type, Predicate<T> predicate) {
        if (type.isEnum()) {
            return Arrays.stream(type.getEnumConstants()).filter(predicate).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    static <T extends Enum & IEnumDict<?>> Optional<T> findByValue(Class<T> type, Object value) {
        return find(type, e -> e.getValue() == value || e.getValue().equals(value)
            || String.valueOf(e.getValue()).equalsIgnoreCase(String.valueOf(value)));
    }

    static <T extends Enum & IEnumDict> Optional<T> findByText(Class<T> type, String text) {
        return find(type, e -> e.getText().equalsIgnoreCase(text));
    }

    static <T extends Enum & IEnumDict> Optional<T> find(Class<T> type, Object target) {
        return find(type, v -> v.eq(target));
    }

    @SafeVarargs
    static <T extends IEnumDict> long toMask(T... t) {
        if (t == null) {
            return 0L;
        }
        long value = 0L;
        for (T t1 : t) {
            value |= t1.getMask();
        }
        return value;
    }

    @SafeVarargs
    static <T extends Enum & IEnumDict> boolean in(T target, T... t) {
        Enum[] all = target.getClass().getEnumConstants();
        if (all.length >= 64) {
            List<T> list = Arrays.asList(t);
            return Arrays.stream(all).map(IEnumDict.class::cast).anyMatch(list::contains);
        }
        return maskIn(toMask(t), target);
    }

    @SafeVarargs
    static <T extends IEnumDict> boolean maskIn(long mask, T... t) {
        long value = toMask(t);
        return (mask & value) == value;
    }

    @SafeVarargs
    static <T extends IEnumDict> boolean maskInAny(long mask, T... t) {
        long value = toMask(t);
        return (mask & value) != 0;
    }

    static <T extends IEnumDict> List<T> getByMask(List<T> allOptions, long mask) {
        if (allOptions.size() >= 64) {
            throw new UnsupportedOperationException("不支持选项超过64个数据字典!");
        }
        List<T> arr = new ArrayList<>();
        for (T t : allOptions) {
            if (t.in(mask)) {
                arr.add(t);
            }
        }
        return arr;
    }

    static <T extends IEnumDict> List<T> getByMask(Supplier<List<T>> allOptionsSupplier, long mask) {
        return getByMask(allOptionsSupplier.get(), mask);
    }

    static <T extends Enum & IEnumDict> List<T> getByMask(Class<T> tClass, long mask) {
        return getByMask(Arrays.asList(tClass.getEnumConstants()), mask);
    }

    class EnumDictJsonDeserializer extends JsonDeserializer implements ObjectDeserializer {
        private static final Log log = LogFactory.get(EnumDictJsonDeserializer.class);

        private Function<Object, Object> mapper;

        public EnumDictJsonDeserializer() {}

        public EnumDictJsonDeserializer(Function<Object, Object> mapper) {
            this.mapper = mapper;
        }

        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            try {
                Object value;
                final JSONLexer lexer = parser.lexer;
                final int token = lexer.token();
                if (token == JSONToken.LITERAL_INT) {
                    int intValue = lexer.intValue();
                    lexer.nextToken(JSONToken.COMMA);
                    return (T)IEnumDict.find((Class)type, intValue);
                } else if (token == JSONToken.LITERAL_STRING) {
                    String name = lexer.stringVal();
                    lexer.nextToken(JSONToken.COMMA);
                    if (name.length() == 0) {
                        return null;
                    }
                    return (T)IEnumDict.find((Class)type, name).orElse(null);
                } else if (token == JSONToken.NULL) {
                    lexer.nextToken(JSONToken.COMMA);
                    return null;
                } else {
                    value = parser.parse();
                    if (value instanceof Map) {
                        return (T)IEnumDict.find(((Class)type), ((Map)value).get("value"))
                            .orElseGet(() -> IEnumDict.find(((Class)type), ((Map)value).get("text")).orElse(null));
                    }
                }
                throw new JSONException("parse enum " + type + " error, value : " + value);
            } catch (JSONException e) {
                throw e;
            } catch (Exception e) {
                throw new JSONException(e.getMessage(), e);
            }
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_STRING;
        }

        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            if (mapper != null) {
                if (node.isTextual()) {
                    return mapper.apply(node.asText());
                }
                if (node.isNumber()) {
                    return mapper.apply(node.asLong());
                }
            }
            String currentName = jp.currentName();
            Object currentValue = jp.getCurrentValue();
            Class findPropertyType;
            if (StringUtils.isEmpty(currentName) || StringUtils.isEmpty(currentValue)) {
                return null;
            } else {
                findPropertyType = BeanUtils.findPropertyType(currentName, currentValue.getClass());
            }
            Supplier<ValidationException> exceptionSupplier = () -> {
                Stream.of(findPropertyType.getEnumConstants()).map(Enum.class::cast).map(e -> {
                    if (e instanceof IEnumDict) {
                        return ((IEnumDict)e).getValue();
                    }
                    return e.name();
                }).collect(Collectors.toList());
                return new ValidationException(currentName, "validation.parameter_does_not_exist_in_enums",
                    currentName);
            };
            if (IEnumDict.class.isAssignableFrom(findPropertyType) && findPropertyType.isEnum()) {
                if (node.isObject()) {
                    try {
                        return IEnumDict.findByValue(findPropertyType, node.get("value").textValue())
                            .orElseThrow(exceptionSupplier);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }
                if (node.isNumber()) {
                    try {
                        return IEnumDict.find(findPropertyType, node.numberValue()).orElseThrow(exceptionSupplier);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }
                if (node.isTextual()) {
                    try {
                        return IEnumDict.find(findPropertyType, node.textValue()).orElseThrow(exceptionSupplier);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }
                return exceptionSupplier.get();
            }
            if (findPropertyType.isEnum()) {
                return Stream.of(findPropertyType.getEnumConstants()).filter(o -> {
                    if (node.isTextual()) {
                        return node.textValue().equalsIgnoreCase(((Enum)o).name());
                    }
                    if (node.isNumber()) {
                        return node.intValue() == ((Enum)o).ordinal();
                    }
                    return false;
                }).findAny().orElseThrow(exceptionSupplier);
            }
            log.warn("unsupported deserialize enum json [{}]", node);
            return null;
        }
    }
}
