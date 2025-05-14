package com.sipa.boot.java8.common.aop.aspect;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhouxiajie
 */
@Aspect
@Component
public class PrintTimeAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintTimeAspect.class);

    @Pointcut("@annotation(com.sipa.boot.java8.common.aop.annotation.PrintTime)")
    public void pointcut() {}

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object object = joinPoint.proceed();

        long end = System.currentTimeMillis();

        LOGGER.info("class [{}], method [{}], runTime [{}]", getClassName(joinPoint), getMethodName(joinPoint),
            (end - start) / 1000.0);

        return object;
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        return Objects.requireNonNull(joinPoint).getSignature().getName();
    }

    private String getClassName(ProceedingJoinPoint joinPoint) {
        return Objects.requireNonNull(joinPoint).getTarget().getClass().getName();
    }
}
