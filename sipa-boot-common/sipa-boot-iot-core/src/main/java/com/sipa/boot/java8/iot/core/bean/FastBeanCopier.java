package com.sipa.boot.java8.iot.core.bean;

import static com.sipa.boot.java8.common.constants.SipaBootCommonConstants.Number.INT_16;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.bean.base.IBeanFactory;
import com.sipa.boot.java8.iot.core.bean.base.IConverter;
import com.sipa.boot.java8.iot.core.bean.base.ICopier;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;
import com.sipa.boot.java8.iot.core.time.base.IDateFormatter;

/**
 * @author caszhou
 * @date 2021/9/24
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class FastBeanCopier {
    private static final Log log = LogFactory.get(FastBeanCopier.class);

    private static final Map<CacheKey, ICopier> CACHE = new ConcurrentHashMap<>();

    private static final PropertyUtilsBean PROPERTY_UTILS = BeanUtilsBean.getInstance().getPropertyUtils();

    private static final Map<Class<?>, Class<?>> WRAPPER_CLASS_MAPPING = new HashMap<>();

    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    public static final DefaultConverter DEFAULT_CONVERT;

    private static IBeanFactory BEAN_FACTORY;

    static {
        WRAPPER_CLASS_MAPPING.put(char.class, Character.class);
        WRAPPER_CLASS_MAPPING.put(boolean.class, Boolean.class);
        WRAPPER_CLASS_MAPPING.put(byte.class, Byte.class);
        WRAPPER_CLASS_MAPPING.put(short.class, Short.class);
        WRAPPER_CLASS_MAPPING.put(int.class, Integer.class);
        WRAPPER_CLASS_MAPPING.put(long.class, Long.class);
        WRAPPER_CLASS_MAPPING.put(float.class, Float.class);
        WRAPPER_CLASS_MAPPING.put(double.class, Double.class);

        BEAN_FACTORY = new IBeanFactory() {
            @Override
            public <T> T newInstance(Class<T> beanType) {
                try {
                    return beanType == Map.class ? (T)new HashMap<>(INT_16) : beanType.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        DEFAULT_CONVERT = new DefaultConverter();
        DEFAULT_CONVERT.setBeanFactory(BEAN_FACTORY);
    }

    public static <T, S> T copy(S source, T target, String... ignore) {
        return copy(source, target, DEFAULT_CONVERT, ignore);
    }

    public static <T, S> T copy(S source, Supplier<T> target, String... ignore) {
        return copy(source, target.get(), DEFAULT_CONVERT, ignore);
    }

    public static <T, S> T copy(S source, Class<T> target, String... ignore) {
        try {
            return copy(source, target.newInstance(), DEFAULT_CONVERT, ignore);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, S> T copy(S source, T target, IConverter converter, String... ignore) {
        return copy(source, target, converter,
            (ignore == null || ignore.length == 0) ? Collections.emptySet() : new HashSet<>(Arrays.asList(ignore)));
    }

    public static <T, S> T copy(S source, T target, Set<String> ignore) {
        return copy(source, target, DEFAULT_CONVERT, ignore);
    }

    public static <T, S> T copy(S source, T target, IConverter converter, Set<String> ignore) {
        if (source instanceof Map && target instanceof Map) {
            ((Map)target).putAll(((Map)source));
            return target;
        }

        getCopier(source, target, true).copy(source, target, ignore, converter);
        return target;
    }

    public static ICopier getCopier(Object source, Object target, boolean autoCreate) {
        Class<?> sourceType = getUserClass(source);
        Class<?> targetType = getUserClass(target);
        CacheKey key = createCacheKey(sourceType, targetType);
        if (autoCreate) {
            return CACHE.computeIfAbsent(key, k -> createCopier(sourceType, targetType));
        } else {
            return CACHE.get(key);
        }
    }

    private static Class<?> getUserClass(Object object) {
        if (object instanceof Map) {
            return Map.class;
        }
        Class<?> type = ClassUtils.getUserClass(object);
        if (java.lang.reflect.Proxy.isProxyClass(type)) {
            Class<?>[] interfaces = type.getInterfaces();
            return interfaces[0];
        }
        return type;
    }

    private static CacheKey createCacheKey(Class<?> source, Class<?> target) {
        return new CacheKey(source, target);
    }

    public static ICopier createCopier(Class<?> source, Class<?> target) {
        String sourceName = source.getName();
        String tartName = target.getName();
        if (sourceName.startsWith("package ")) {
            sourceName = sourceName.substring("package ".length());
        }
        if (tartName.startsWith("package ")) {
            tartName = tartName.substring("package ".length());
        }
        String method = "public void copy(Object s, Object t, java.util.Set ignore, "
            + "com.sipa.boot.java8.iot.core.bean.base.IConverter converter){\n" + "try{\n\t" + sourceName
            + " $$__source=(" + sourceName + ")s;\n\t" + tartName + " $$__target=(" + tartName + ")t;\n\t"
            + createCopierCode(source, target) + "}catch(Exception e){\n"
            + "\tthrow new RuntimeException(e.getMessage(),e);" + "\n}\n" + "\n}";
        try {
            return Proxy.create(ICopier.class).addMethod(method).newInstance();
        } catch (Exception e) {
            log.error("创建 bean copy 代理对象失败\n{}", method, e);
            throw new UnsupportedOperationException(e.getMessage(), e);
        }
    }

    private static String createCopierCode(Class<?> source, Class<?> target) {
        Map<String, ClassProperty> sourceProperties = null;
        Map<String, ClassProperty> targetProperties = null;

        if (Map.class.isAssignableFrom(source)) {
            if (!Map.class.isAssignableFrom(target)) {
                targetProperties = createProperty(target);
                sourceProperties = createMapProperty(targetProperties);
            }
        } else if (Map.class.isAssignableFrom(target)) {
            if (!Map.class.isAssignableFrom(source)) {
                sourceProperties = createProperty(source);
                targetProperties = createMapProperty(sourceProperties);
            }
        } else {
            targetProperties = createProperty(target);
            sourceProperties = createProperty(source);
        }

        if (sourceProperties == null || targetProperties == null) {
            throw new UnsupportedOperationException("不支持的类型, source [" + source + "] target [" + target + "]");
        }

        StringBuilder code = new StringBuilder();

        for (ClassProperty sourceProperty : sourceProperties.values()) {
            ClassProperty targetProperty = targetProperties.get(sourceProperty.getName());
            if (targetProperty == null) {
                continue;
            }
            code.append("if(!ignore.contains(\"").append(sourceProperty.getName()).append("\")){\n\t");
            if (!sourceProperty.isPrimitive()) {
                code.append("if($$__source.").append(sourceProperty.getReadMethod()).append("!=null){\n");
            }
            code.append(targetProperty.generateVar(targetProperty.getName()))
                .append("=")
                .append(sourceProperty.generateGetter(target, targetProperty.getType()))
                .append(";\n");
            if (!targetProperty.isPrimitive()) {
                code.append("\tif(").append(sourceProperty.getName()).append("!=null){\n");
            }
            code.append("\t$$__target.")
                .append(targetProperty.generateSetter(targetProperty.getType(), sourceProperty.getName()))
                .append(";\n");
            if (!targetProperty.isPrimitive()) {
                code.append("\t}\n");
            }
            if (!sourceProperty.isPrimitive()) {
                code.append("\t}\n");
            }
            code.append("}\n");
        }
        return code.toString();
    }

    private static Map<String, ClassProperty> createProperty(Class<?> type) {
        List<String> fieldNames =
            Arrays.stream(type.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        return Stream.of(PROPERTY_UTILS.getPropertyDescriptors(type))
            .filter(property -> !"class".equals(property.getName()) && property.getReadMethod() != null
                && property.getWriteMethod() != null)
            .map(BeanClassProperty::new)
            .sorted(Comparator.comparing(property -> fieldNames.indexOf(property.name)))
            .collect(Collectors.toMap(ClassProperty::getName, Function.identity(), (k, k2) -> k, LinkedHashMap::new));
    }

    private static Map<String, ClassProperty> createMapProperty(Map<String, ClassProperty> template) {
        return template.values()
            .stream()
            .map(classProperty -> new MapClassProperty(classProperty.name))
            .collect(Collectors.toMap(ClassProperty::getName, Function.identity(), (k, k2) -> k, LinkedHashMap::new));
    }

    public static Set<String> include(String... includeProperties) {
        return new HashSet<String>(Arrays.asList(includeProperties)) {
            @Override
            public boolean contains(Object o) {
                return !super.contains(o);
            }
        };
    }

    public static void setBeanFactory(IBeanFactory beanFactory) {
        BEAN_FACTORY = beanFactory;
        DEFAULT_CONVERT.setBeanFactory(beanFactory);
    }

    public static IBeanFactory getBeanFactory() {
        return BEAN_FACTORY;
    }

    static abstract class ClassProperty {
        protected String name;

        protected String readMethodName;

        protected String writeMethodName;

        protected BiFunction<Class<?>, Class<?>, String> getter;

        protected BiFunction<Class<?>, String, String> setter;

        protected Class<?> type;

        protected Class<?> beanType;

        public String getReadMethod() {
            return readMethodName + "()";
        }

        public String generateVar(String name) {
            return getTypeName().concat(" ").concat(name);
        }

        public String getTypeName() {
            return getTypeName(type);
        }

        public String getTypeName(Class<?> type) {
            String targetTypeName = type.getName();
            if (type.isArray()) {
                targetTypeName = type.getComponentType().getName() + "[]";
            }
            return targetTypeName;
        }

        public boolean isPrimitive() {
            return isPrimitive(getType());
        }

        public boolean isPrimitive(Class<?> type) {
            return type.isPrimitive();
        }

        public boolean isWrapper() {
            return isWrapper(getType());
        }

        public boolean isWrapper(Class<?> type) {
            return WRAPPER_CLASS_MAPPING.containsValue(type);
        }

        protected Class<?> getPrimitiveType(Class<?> type) {
            return WRAPPER_CLASS_MAPPING.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == type)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        }

        protected Class<?> getWrapperType() {
            return WRAPPER_CLASS_MAPPING.get(type);
        }

        protected String castWrapper(String getter) {
            return getWrapperType().getSimpleName().concat(".valueOf(").concat(getter).concat(")");
        }

        public BiFunction<Class<?>, Class<?>, String> createGetterFunction() {
            return (targetBeanType, targetType) -> {
                String getterCode = "$$__source." + getReadMethod();

                String generic = "com.sipa.boot.java8.iot.core.bean.FastBeanCopier.EMPTY_CLASS_ARRAY";
                Field field = ReflectionUtils.findField(targetBeanType, name);
                boolean hasGeneric = false;
                if (field != null) {
                    String[] arr = Arrays.stream(ResolvableType.forField(field).getGenerics())
                        .map(ResolvableType::getRawClass)
                        .filter(Objects::nonNull)
                        .map(t -> t.getName().concat(".class"))
                        .toArray(String[]::new);
                    if (arr.length > 0) {
                        generic = "new Class[]{" + String.join(",", arr) + "}";
                        hasGeneric = true;
                    }
                }
                String convert = "converter.convert((Object)(" + (isPrimitive() ? castWrapper(getterCode) : getterCode)
                    + ")," + getTypeName(targetType) + ".class," + generic + ")";
                StringBuilder convertCode = new StringBuilder();

                if (targetType != getType()) {
                    if (isPrimitive(targetType)) {
                        boolean sourceIsWrapper = isWrapper();
                        Class<?> targetWrapperClass = WRAPPER_CLASS_MAPPING.get(targetType);
                        Class<?> sourcePrimitive = getPrimitiveType(getType());
                        // 目标字段是基本数据类型,源字段是包装器类型
                        if (sourceIsWrapper) {
                            convertCode.append(getterCode)
                                .append(".")
                                .append(sourcePrimitive.getName())
                                .append("Value()");
                        } else {
                            // 类型不一致，调用convert转换
                            convertCode.append("((")
                                .append(targetWrapperClass.getName())
                                .append(")")
                                .append(convert)
                                .append(").")
                                .append(targetType.getName())
                                .append("Value()");
                        }
                    } else if (isPrimitive()) {
                        boolean targetIsWrapper = isWrapper(targetType);
                        // 源字段类型为基本数据类型，目标字段为包装器类型
                        if (targetIsWrapper) {
                            convertCode.append(targetType.getName()).append(".valueOf(").append(getterCode).append(")");
                        } else {
                            convertCode.append("(")
                                .append(targetType.getName())
                                .append(")(")
                                .append(convert)
                                .append(")");
                        }
                    } else {
                        convertCode.append("(")
                            .append(getTypeName(targetType))
                            .append(")(")
                            .append(convert)
                            .append(")");
                    }
                } else {
                    if (Cloneable.class.isAssignableFrom(targetType)) {
                        try {
                            convertCode.append("(")
                                .append(getTypeName())
                                .append(")")
                                .append(getterCode)
                                .append(".clone()");
                        } catch (Exception e) {
                            convertCode.append(getterCode);
                        }
                    } else {
                        if ((Map.class.isAssignableFrom(targetType) || Collection.class.isAssignableFrom(type))
                            && hasGeneric) {
                            convertCode.append("(").append(getTypeName()).append(")").append(convert);
                        } else {
                            convertCode.append("(").append(getTypeName()).append(")").append(getterCode);
                        }
                    }
                }
                return convertCode.toString();
            };
        }

        public BiFunction<Class<?>, String, String> createSetterFunction(Function<String, String> settingNameSupplier) {
            return (sourceType, paramGetter) -> settingNameSupplier.apply(paramGetter);
        }

        public String generateGetter(Class<?> targetBeanType, Class<?> targetType) {
            return getGetter().apply(targetBeanType, targetType);
        }

        public String generateSetter(Class<?> targetType, String getter) {
            return getSetter().apply(targetType, getter);
        }

        public String getName() {
            return name;
        }

        public String getReadMethodName() {
            return readMethodName;
        }

        public String getWriteMethodName() {
            return writeMethodName;
        }

        public BiFunction<Class<?>, Class<?>, String> getGetter() {
            return getter;
        }

        public BiFunction<Class<?>, String, String> getSetter() {
            return setter;
        }

        public Class<?> getType() {
            return type;
        }

        public Class<?> getBeanType() {
            return beanType;
        }
    }

    static class BeanClassProperty extends ClassProperty {
        public BeanClassProperty(PropertyDescriptor descriptor) {
            type = descriptor.getPropertyType();
            readMethodName = descriptor.getReadMethod().getName();
            writeMethodName = descriptor.getWriteMethod().getName();

            getter = createGetterFunction();
            setter = createSetterFunction(paramGetter -> writeMethodName + "(" + paramGetter + ")");
            name = descriptor.getName();
            beanType = descriptor.getReadMethod().getDeclaringClass();
        }
    }

    static class MapClassProperty extends ClassProperty {
        public MapClassProperty(String name) {
            type = Object.class;
            this.name = name;
            this.readMethodName = "get";
            this.writeMethodName = "put";
            this.getter = createGetterFunction();
            this.setter = createSetterFunction(paramGetter -> "put(\"" + name + "\"," + paramGetter + ")");
            beanType = Map.class;
        }

        @Override
        public String getReadMethod() {
            return "get(\"" + name + "\")";
        }

        @Override
        public String getReadMethodName() {
            return "get(\"" + name + "\")";
        }
    }

    public static final class DefaultConverter implements IConverter {
        private IBeanFactory beanFactory = BEAN_FACTORY;

        public void setBeanFactory(IBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        public Collection<?> newCollection(Class<?> targetClass) {
            if (targetClass == List.class) {
                return new ArrayList<>();
            } else if (targetClass == Set.class) {
                return new HashSet<>();
            } else if (targetClass == Queue.class) {
                return new LinkedList<>();
            } else {
                try {
                    return (Collection<?>)targetClass.newInstance();
                } catch (Exception e) {
                    throw new UnsupportedOperationException("不支持的类型:" + targetClass, e);
                }
            }
        }

        @Override
        public <T> T convert(Object source, Class<T> targetClass, Class<?>[] genericType) {
            if (source == null) {
                return null;
            }
            if (source.getClass().isEnum()) {
                if (source instanceof IEnumDict) {
                    Object val = ((IEnumDict)source).getValue();
                    if (targetClass.isInstance(val)) {
                        return ((T)val);
                    }
                    return convert(val, targetClass, genericType);
                }
            }
            if (targetClass == String.class) {
                if (source instanceof Date) {
                    return (T)IDateFormatter.toString(((Date)source), "yyyy-MM-dd HH:mm:ss");
                }
                return (T)String.valueOf(source);
            }
            if (targetClass == Object.class) {
                return (T)source;
            }
            if (targetClass == Date.class) {
                if (source instanceof String) {
                    return (T)IDateFormatter.fromString((String)source);
                }
                if (source instanceof Number) {
                    return (T)new Date(((Number)source).longValue());
                }
                if (source instanceof Date) {
                    return (T)new Date(((Date)source).getTime());
                }
            }
            if (Collection.class.isAssignableFrom(targetClass)) {
                Collection collection = newCollection(targetClass);
                Collection sourceCollection;
                if (source instanceof Collection) {
                    sourceCollection = (Collection)source;
                } else if (source instanceof Object[]) {
                    sourceCollection = Arrays.asList((Object[])source);
                } else {
                    if (source instanceof String) {
                        String stringValue = ((String)source);
                        sourceCollection = Arrays.asList(stringValue.split("[,]"));
                    } else {
                        sourceCollection = Collections.singletonList(source);
                    }
                }
                // 转换泛型
                if (genericType != null && genericType.length > 0 && genericType[0] != Object.class) {
                    for (Object sourceObj : sourceCollection) {
                        collection.add(convert(sourceObj, genericType[0], null));
                    }
                } else {
                    collection.addAll(sourceCollection);
                }
                return (T)collection;
            }
            if (targetClass.isEnum()) {
                if (IEnumDict.class.isAssignableFrom(targetClass)) {
                    String strVal = String.valueOf(source);

                    Object val =
                        IEnumDict.find((Class)targetClass, e -> e.eq(source) || e.name().equalsIgnoreCase(strVal))
                            .orElse(null);
                    if (targetClass.isInstance(val)) {
                        return ((T)val);
                    }
                    return convert(val, targetClass, genericType);
                }
                String strSource = String.valueOf(source);
                for (T t : targetClass.getEnumConstants()) {
                    if (((Enum)t).name().equalsIgnoreCase(strSource)
                        || Objects.equals(String.valueOf(((Enum<?>)t).ordinal()), strSource)) {
                        return t;
                    }
                }
                log.warn("无法将[{}]转为枚举[{}]", source, targetClass);
                return null;
            }
            if (targetClass.isArray()) {
                Class<?> componentType = targetClass.getComponentType();
                List<?> val = convert(source, List.class, new Class[] {componentType});
                return (T)val.toArray((Object[])Array.newInstance(componentType, val.size()));
            }
            try {
                org.apache.commons.beanutils.Converter converter =
                    BeanUtilsBean.getInstance().getConvertUtils().lookup(targetClass);
                if (null != converter) {
                    return converter.convert(targetClass, source);
                }

                return copy(source, beanFactory.newInstance(targetClass), this);
            } catch (Exception e) {
                log.warn("复制类型[{}]->[{}]失败", source, targetClass, e);
                throw new UnsupportedOperationException(e.getMessage(), e);
            }
        }
    }

    static class CacheKey {
        private final Class<?> targetType;

        private final Class<?> sourceType;

        public CacheKey(Class<?> targetType, Class<?> sourceType) {
            this.targetType = targetType;
            this.sourceType = sourceType;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            CacheKey target = ((CacheKey)obj);
            return target.targetType == targetType && target.sourceType == sourceType;
        }

        @Override
        public int hashCode() {
            int result = this.targetType != null ? this.targetType.hashCode() : 0;
            result = 31 * result + (this.sourceType != null ? this.sourceType.hashCode() : 0);
            return result;
        }
    }
}
