package com.sipa.boot.java8.common.swagger.plugin.enums;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.sipa.boot.java8.common.annotations.SwaggerEnum;
import com.sipa.boot.java8.common.utils.Utils;

import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

/**
 * @author zhouxiajie
 * @date 2020/11/3
 */
@Component
public class EnumModelPropertyBuilderPlugin implements ModelPropertyBuilderPlugin {
    @Override
    public void apply(ModelPropertyContext context) {
        Optional<BeanPropertyDefinition> optional = context.getBeanPropertyDefinition();
        if (!optional.isPresent()) {
            return;
        }

        final Class<?> fieldType = optional.get().getField().getRawType();

        addDescForEnum(context, fieldType);
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    private void addDescForEnum(ModelPropertyContext context, Class<?> fieldType) {
        if (Enum.class.isAssignableFrom(fieldType)) {
            SwaggerEnum annotation = AnnotationUtils.findAnnotation(fieldType, SwaggerEnum.class);
            if (annotation != null) {
                String code = annotation.code();
                String de = annotation.desc();

                Object[] enumConstants = fieldType.getEnumConstants();

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

                ModelPropertyBuilder builder = context.getBuilder();
                Field descField = ReflectionUtils.findField(builder.getClass(), "description");
                ReflectionUtils.makeAccessible(Objects.requireNonNull(descField));
                String joinText =
                    StringUtils.trimToEmpty(Utils.stringValueOf(ReflectionUtils.getField(descField, builder))) + " ("
                        + String.join("; ", displayValues) + ")";

                builder.description(joinText).type(context.getResolver().resolve(Integer.class));
            }
        }
    }
}
