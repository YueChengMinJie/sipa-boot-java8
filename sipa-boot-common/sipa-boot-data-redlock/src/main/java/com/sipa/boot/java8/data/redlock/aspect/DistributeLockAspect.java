package com.sipa.boot.java8.data.redlock.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sipa.boot.java8.data.redlock.annotation.DistributeLock;
import com.sipa.boot.java8.data.redlock.exception.CannotGetLockException;

/**
 * @author xuanyu
 * @date 2019-07-23 11:35
 */
@Aspect
@Component
public class DistributeLockAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeLockAspect.class);

    private static final Pattern KEY_PATTERN = Pattern.compile("\\{\\d+}");

    private final RedissonClient redissonClient;

    public DistributeLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Pointcut("@annotation(com.sipa.boot.java8.data.redlock.annotation.DistributeLock)")
    public void pointCut() {}

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature)signature).getMethod();

        String key = "lock";
        long waitTime = 100;
        long leaseTime = 10;
        TimeUnit unit = TimeUnit.SECONDS;

        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation instanceof DistributeLock) {
                key = ((DistributeLock)annotation).value();
                key = getKey(key, args);
                waitTime = ((DistributeLock)annotation).waitTime();
                leaseTime = ((DistributeLock)annotation).leaseTime();
                unit = ((DistributeLock)annotation).unit();
            }
        }

        long start = System.currentTimeMillis();
        RLock lock = redissonClient.getLock(key);
        try {
            boolean res = lock.tryLock(waitTime, leaseTime, unit);
            long get = System.currentTimeMillis();
            String cost = getSecond(start, get);
            if (res) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("get [{}] lock, cost [{}] s", key, cost);
                }
                return joinPoint.proceed();
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("cannot get [{}] lock, cost [{}] s", key, cost);
                }
                throw new CannotGetLockException();
            }
        } finally {
            long get = System.currentTimeMillis();
            String cost = getSecond(start, get);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("release [{}] lock, cost [{}] s", key, cost);
            }
            lock.unlock();
        }
    }

    private String getSecond(long start, long end) {
        return String.valueOf((end - start) / 1000.0);
    }

    private String getKey(String value, Object[] args) {
        Matcher matcher = KEY_PATTERN.matcher(value);
        List<String> keyPattern = new ArrayList<>();
        while (matcher.find()) {
            keyPattern.add(matcher.group());
        }
        String result = value;
        for (String key : keyPattern) {
            Integer index = Integer.valueOf(key.replace("{", "").replace("}", ""));
            result = result.replace(key, args[index].toString());
        }
        return result;
    }
}
