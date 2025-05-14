package com.sipa.boot.java8.common.aop.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.common.aop.annotation.SameUser;
import com.sipa.boot.java8.common.aop.base.ISameUser;
import com.sipa.boot.java8.common.exceptions.BadRequestException;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author 甘华根
 * @since 2020/7/31 14:09
 */
@Aspect
@Component
public class SameUserAspect {
    public static final Log LOGGER = LogFactory.get(SameUserAspect.class);

    private final ISameUser sameUser;

    public SameUserAspect(@Autowired(required = false) ISameUser sameUser) {
        this.sameUser = sameUser;
    }

    @Pointcut("@annotation(com.sipa.boot.java8.common.aop.annotation.SameUser)")
    public void pointcut() {}

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();

        String module = "";
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation instanceof SameUser) {
                module = ((SameUser)annotation).module();
            }
        }

        Object[] ary = joinPoint.getArgs();

        if (ary.length > 0) {
            Object moduleId = ary[0];

            if (moduleId instanceof String) {
                if (Objects.nonNull(sameUser)) {
                    if (!sameUser.checkAuth(module, (String)moduleId)) {
                        throw new BadRequestException("不是同一用户");
                    }
                } else {
                    LOGGER.info("系统无ISameUser实现类, 无法判断是否是同一用户.");
                }
            } else {
                LOGGER.info("moduleId必须为string.");
            }
        } else {
            LOGGER.info("无法判断是否是同一用户，无moduleId.");
        }

        return joinPoint.proceed();
    }
}
