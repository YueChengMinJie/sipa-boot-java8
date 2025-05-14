package com.sipa.boot.java8.common.swagger.plugin.enums;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Joiner;
import com.sipa.boot.java8.common.annotations.SwaggerEnum;

import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

/**
 * @author zhouxiajie
 * @date 2020/11/3
 */
@Component
public class EnumParameterBuilderPlugin implements ParameterBuilderPlugin, OperationBuilderPlugin {
    private static final Joiner JOINER = Joiner.on(",");

    @Override
    public void apply(ParameterContext context) {
        Class<?> type = context.resolvedMethodParameter().getParameterType().getErasedType();
        if (Enum.class.isAssignableFrom(type)) {
            SwaggerEnum annotation = AnnotationUtils.findAnnotation(type, SwaggerEnum.class);
            if (annotation != null) {
                String code = annotation.code();
                String de = annotation.desc();
                Object[] enumConstants = type.getEnumConstants();
                List<String> displayValues = Arrays.stream(enumConstants).filter(Objects::nonNull).map(item -> {
                    Class<?> currentClass = item.getClass();

                    Field indexField = ReflectionUtils.findField(currentClass, code);
                    ReflectionUtils.makeAccessible(Objects.requireNonNull(indexField));
                    Object value = ReflectionUtils.getField(indexField, item);

                    Field descField = ReflectionUtils.findField(currentClass, de);
                    ReflectionUtils.makeAccessible(Objects.requireNonNull(descField));
                    return Objects.requireNonNull(value).toString();
                }).collect(Collectors.toList());

                ParameterBuilder parameterBuilder = context.parameterBuilder();
                AllowableListValues values = new AllowableListValues(displayValues, "LIST");
                parameterBuilder.allowableValues(values);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    @Override
    public void apply(OperationContext context) {
        Map<String, List<String>> map = new HashMap<>();
        List<ResolvedMethodParameter> parameters = context.getParameters();
        parameters.forEach(parameter -> {
            ResolvedType parameterType = parameter.getParameterType();
            Class<?> clazz = parameterType.getErasedType();
            if (Enum.class.isAssignableFrom(clazz)) {
                SwaggerEnum annotation = AnnotationUtils.findAnnotation(clazz, SwaggerEnum.class);
                if (annotation != null) {
                    String code = annotation.code();
                    String de = annotation.desc();
                    Object[] enumConstants = clazz.getEnumConstants();

                    List<String> displayValues = Arrays.stream(enumConstants).filter(Objects::nonNull).map(item -> {
                        Class<?> currentClass = item.getClass();

                        Field indexField = ReflectionUtils.findField(currentClass, code);
                        ReflectionUtils.makeAccessible(Objects.requireNonNull(indexField));
                        Object value = ReflectionUtils.getField(indexField, item);

                        Field descField = ReflectionUtils.findField(currentClass, de);
                        ReflectionUtils.makeAccessible(Objects.requireNonNull(descField));
                        Object desc = ReflectionUtils.getField(descField, item);
                        return value + ":" + desc;
                    }).collect(Collectors.toList());

                    map.put(parameter.defaultName().orElse(StringUtils.EMPTY), displayValues);

                    OperationBuilder operationBuilder = context.operationBuilder();
                    Field parametersField = ReflectionUtils.findField(operationBuilder.getClass(), "parameters");
                    ReflectionUtils.makeAccessible(Objects.requireNonNull(parametersField));
                    List<Parameter> list = (List<Parameter>)ReflectionUtils.getField(parametersField, operationBuilder);

                    map.forEach((k, v) -> {
                        for (Parameter currentParameter : list) {
                            if (StringUtils.equals(currentParameter.getName(), k)) {
                                Field description =
                                    ReflectionUtils.findField(currentParameter.getClass(), "description");
                                ReflectionUtils.makeAccessible(Objects.requireNonNull(description));
                                Object field = ReflectionUtils.getField(description, currentParameter);
                                ReflectionUtils.setField(description, currentParameter, field + " , " + JOINER.join(v));
                                break;
                            }
                        }
                    });
                }
            }
        });
    }
}
