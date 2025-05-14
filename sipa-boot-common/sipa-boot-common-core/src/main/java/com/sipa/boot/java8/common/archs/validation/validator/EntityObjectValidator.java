package com.sipa.boot.java8.common.archs.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.sipa.boot.java8.common.archs.error.base.AbstractEntityObject;
import com.sipa.boot.java8.common.archs.validation.constraint.EntityObjectValid;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class EntityObjectValidator implements ConstraintValidator<EntityObjectValid, AbstractEntityObject> {
    @Override
    public void initialize(EntityObjectValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(AbstractEntityObject entity, ConstraintValidatorContext context) {
        return entity.validate(context);
    }
}
