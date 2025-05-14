package com.sipa.boot.java8.iot.core.metadata;

import java.util.function.Consumer;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class ValidateResult {
    private boolean success;

    private Object value;

    private String errorMsg;

    public ValidateResult() {}

    public ValidateResult(boolean success, Object value, String errorMsg) {
        this.success = success;
        this.value = value;
        this.errorMsg = errorMsg;
    }

    public static ValidateResult success(Object value) {
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        result.setValue(value);
        return result;
    }

    public static ValidateResult success() {
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        return result;
    }

    public static ValidateResult fail(String message) {
        ValidateResult result = new ValidateResult();
        result.setSuccess(false);
        result.setErrorMsg(message);
        return result;
    }

    public Object assertSuccess() {
        if (!success) {
            throw new IllegalArgumentException(errorMsg);
        }
        return value;
    }

    public void ifFail(Consumer<ValidateResult> resultConsumer) {
        if (!success) {
            resultConsumer.accept(this);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static final class ValidateResultBuilder {
        private boolean success;

        private Object value;

        private String errorMsg;

        private ValidateResultBuilder() {}

        public static ValidateResultBuilder aValidateResult() {
            return new ValidateResultBuilder();
        }

        public ValidateResultBuilder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public ValidateResultBuilder withValue(Object value) {
            this.value = value;
            return this;
        }

        public ValidateResultBuilder withErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public ValidateResult build() {
            ValidateResult validateResult = new ValidateResult();
            validateResult.value = this.value;
            validateResult.errorMsg = this.errorMsg;
            validateResult.success = this.success;
            return validateResult;
        }
    }
}
