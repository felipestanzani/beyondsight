package com.felipestanzani.beyondsight.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.felipestanzani.beyondsight.service..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        // Log method entry with parameters
        if (log.isInfoEnabled()) {
            log.info("Entering method: {}.{} with parameters: {}",
                    className, methodName, Arrays.toString(args));
        }

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        // Log method exit with return value and execution time
        log.info("Exiting method: {}.{} with return value: {} (execution time: {}ms)",
                className, methodName, result, executionTime);

        return result;
    }
}
