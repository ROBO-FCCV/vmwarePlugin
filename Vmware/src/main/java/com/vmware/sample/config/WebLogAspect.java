/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Aspect for log
 *
 * @since 2020-09-27
 */
@Slf4j
@Aspect
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebLogAspect {
    /**
     * Log the request
     *
     * @param joinPoint joinPoint
     * @return result
     * @throws Throwable method throwable
     */
    @Around(value = "execution(* com.vmware.sample.controller.*.*(..))"
        + "&& !execution(* com.vmware.sample.controller.impl.SystemController.login(..))"
        + "&& !execution(* com.vmware.sample.controller.impl.VMwareController.add(..))"
        + "&& !execution(* com.vmware.sample.controller.VMController.getVmVNCByVmId(..))", argNames = "joinPoint")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Request method {}",
            joinPoint.getSignature().getDeclaringType() + "#" + joinPoint.getSignature().getName());
        LocalDateTime startDateTime = LocalDateTime.now();
        Object proceed = joinPoint.proceed();
        LocalDateTime endDateTime = LocalDateTime.now();
        log.info("Request cost {}", Duration.between(startDateTime, endDateTime));
        return proceed;
    }
}

