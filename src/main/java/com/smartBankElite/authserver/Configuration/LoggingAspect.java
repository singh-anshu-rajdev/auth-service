package com.smartBankElite.authserver.Configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(com.smartBankElite.authserver.Controller..*) " +
            "|| within(com.smartBankElite.authserver.Service..*) " +
            "|| within(com.smartBankElite.authserver.ServiceImpl..*) " +
            "|| within(com.smartBankElite.authserver.Repositories..*)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Started execution: {}", methodName);
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsed = System.currentTimeMillis() - start;
        logger.info("Finished execution: {} in {} ms", methodName, elapsed);
        return result;
    }
}
