package com.sipa.boot.java8.common.archs.validation.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.java8.common.archs.validation.constraint.EmailPlus;

/**
 * @author caszhou
 * @date 2021/9/28
 */
public class EmailPlusValidator implements ConstraintValidator<EmailPlus, String> {
    public static final String EMAIL_BASE_PATTERN =
        "[A-Za-z0-9!#$%&'*+\\-/=?^_`{|}~]+(\\.[A-Za-z0-9!#$%&'*+\\-/=?^_`{|}~]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^" + EMAIL_BASE_PATTERN + "$");

    @Override
    public void initialize(final EmailPlus constraintAnnotation) {
    }

    @Override
    public boolean isValid(String valueToValidate, ConstraintValidatorContext constraintContext) {
        if (StringUtils.isNotBlank(valueToValidate)) {
            return true;
        }
        return EMAIL_PATTERN.matcher(valueToValidate).matches();
    }
}
