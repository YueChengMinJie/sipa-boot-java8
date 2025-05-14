package com.sipa.boot.java8.iot.core.bean;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.springframework.util.ClassUtils;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.*;

/**
 * @author caszhou
 * @date 2021/9/24
 */
public class Proxy<I> {
    private static final AtomicLong COUNTER = new AtomicLong(1);

    private final CtClass ctClass;

    private final Class<I> superClass;

    private final String className;

    private Class<I> targetClass;

    public static <I> Proxy<I> create(Class<I> superClass, String... classPathString) {
        return new Proxy<>(superClass, classPathString);
    }

    public Proxy(Class<I> superClass, String... classPathString) {
        try {
            if (superClass == null) {
                throw new NullPointerException("superClass can not be null");
            }
            this.superClass = superClass;
            ClassPool classPool = ClassPool.getDefault();

            classPool.insertClassPath(new ClassClassPath(this.getClass()));
            classPool.insertClassPath(new LoaderClassPath(ClassUtils.getDefaultClassLoader()));

            if (classPathString != null) {
                for (String path : classPathString) {
                    classPool.insertClassPath(path);
                }
            }
            className = superClass.getSimpleName() + "FastBeanCopier" + COUNTER.getAndAdd(1);
            String classFullName = superClass.getPackage() + "." + className;

            ctClass = classPool.makeClass(classFullName);
            if (superClass != Object.class) {
                if (superClass.isInterface()) {
                    ctClass.setInterfaces(new CtClass[] {classPool.get(superClass.getName())});
                } else {
                    ctClass.setSuperclass(classPool.get(superClass.getName()));
                }
            }
            addConstructor("public " + className + "(){}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Proxy<I> addMethod(String code) {
        return handleException(() -> ctClass.addMethod(CtNewMethod.make(code, ctClass)));
    }

    public Proxy<I> addConstructor(String code) {
        return handleException(() -> ctClass.addConstructor(CtNewConstructor.make(code, ctClass)));
    }

    public Proxy<I> addField(String code) {
        return addField(code, null);
    }

    public Proxy<I> addField(String code, Class<? extends java.lang.annotation.Annotation> annotation) {
        return addField(code, annotation, null);
    }

    public static MemberValue createMemberValue(Object value, ConstPool constPool) {
        MemberValue memberValue = null;
        if (value instanceof Integer) {
            memberValue = new IntegerMemberValue(constPool, ((Integer)value));
        } else if (value instanceof Boolean) {
            memberValue = new BooleanMemberValue((Boolean)value, constPool);
        } else if (value instanceof Long) {
            memberValue = new LongMemberValue((Long)value, constPool);
        } else if (value instanceof String) {
            memberValue = new StringMemberValue((String)value, constPool);
        } else if (value instanceof Class) {
            memberValue = new ClassMemberValue(((Class)value).getName(), constPool);
        } else if (value instanceof Object[]) {
            Object[] arr = ((Object[])value);
            ArrayMemberValue arrayMemberValue =
                new ArrayMemberValue(new ClassMemberValue(arr[0].getClass().getName(), constPool), constPool);
            arrayMemberValue
                .setValue(Arrays.stream(arr).map(o -> createMemberValue(o, constPool)).toArray(MemberValue[]::new));
            memberValue = arrayMemberValue;
        }
        return memberValue;
    }

    public Proxy<I> custom(Consumer<CtClass> ctClassConsumer) {
        ctClassConsumer.accept(ctClass);
        return this;
    }

    public Proxy<I> addField(String code, Class<? extends java.lang.annotation.Annotation> annotation,
        Map<String, Object> annotationProperties) {
        return handleException(() -> {
            CtField ctField = CtField.make(code, ctClass);
            if (null != annotation) {
                ConstPool constPool = ctClass.getClassFile().getConstPool();
                AnnotationsAttribute attributeInfo =
                    new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                Annotation ann = new javassist.bytecode.annotation.Annotation(annotation.getName(), constPool);
                if (null != annotationProperties) {
                    annotationProperties.forEach((key, value) -> {
                        MemberValue memberValue = createMemberValue(value, constPool);
                        if (memberValue != null) {
                            ann.addMemberValue(key, memberValue);
                        }
                    });
                }
                attributeInfo.addAnnotation(ann);
                ctField.getFieldInfo().addAttribute(attributeInfo);
            }
            ctClass.addField(ctField);
        });
    }

    private Proxy<I> handleException(Task task) {
        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public I newInstance() {
        try {
            return getTargetClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<I> getTargetClass() {
        if (targetClass == null) {
            try {
                targetClass = ctClass.toClass(ClassUtils.getDefaultClassLoader(), null);
            } catch (CannotCompileException e) {
                throw new RuntimeException(e);
            }
        }
        return targetClass;
    }

    interface Task {
        void run() throws Exception;
    }

    public CtClass getCtClass() {
        return ctClass;
    }

    public Class<I> getSuperClass() {
        return superClass;
    }

    public String getClassName() {
        return className;
    }
}
