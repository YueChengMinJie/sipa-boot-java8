package com.sipa.boot.java8.common.common.exception.property;

/**
 * @author fzh
 */
public class ExceptionHandlingProperties {
    private boolean showUnknownExceptionStack = false;

    private boolean printWarnExceptionStack = true;

    public boolean isShowUnknownExceptionStack() {
        return showUnknownExceptionStack;
    }

    public void setShowUnknownExceptionStack(boolean showUnknownExceptionStack) {
        this.showUnknownExceptionStack = showUnknownExceptionStack;
    }

    public boolean isPrintWarnExceptionStack() {
        return printWarnExceptionStack;
    }

    public void setPrintWarnExceptionStack(boolean printWarnExceptionStack) {
        this.printWarnExceptionStack = printWarnExceptionStack;
    }
}
