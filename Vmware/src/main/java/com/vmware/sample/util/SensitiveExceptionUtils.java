/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.util;

import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.acl.NotOwnerException;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.jar.JarException;

import javax.naming.InsufficientResourcesException;

/**
 * 功能描述
 *
 * @auther y00576814
 * @since 2020-12-30
 */
@Slf4j
public class SensitiveExceptionUtils {
    /**
     * 需要屏蔽的敏感信息集合
     */
    private static final Class<Throwable>[] SENSITIVE_EXCEPTIONS = new Class[] {
        SQLException.class, FileNotFoundException.class, MethodArgumentTypeMismatchException.class, BindException.class,
        ConcurrentModificationException.class, InsufficientResourcesException.class, MissingResourceException.class,
        JarException.class, NotOwnerException.class, OutOfMemoryError.class, StackOverflowError.class,
        InterruptedException.class, IOException.class, RuntimeFaultFaultMsg.class, InvalidPropertyFaultMsg.class,
        NoSuchAlgorithmException.class, KeyManagementException.class, Exception.class
    };

    private SensitiveExceptionUtils() {
    }

    /**
     * 隐藏敏感异常信息，隐藏message，将显示null代替,返回抛出敏感异常的堆栈对象
     *
     * @param throwable 异常throw对象
     * @return 异常对象
     */
    public static Throwable hideSensitiveInfo(Throwable throwable) {
        List<Throwable> throwableList = ExceptionUtils.getThrowableList(throwable);
        for (Throwable throwableItem : throwableList) {
            Throwable resultThrowable = getThrowable(throwableItem);
            if (resultThrowable != null) {
                return resultThrowable;
            }
        }
        return throwable;
    }

    private static Throwable getThrowable(Throwable throwableItem) {
        for (Class<Throwable> sensitiveException : SENSITIVE_EXCEPTIONS) {
            if (ExceptionUtils.hasCause(throwableItem, sensitiveException)) {
                try {
                    Throwable resultThrowable = sensitiveException.newInstance();
                    resultThrowable.setStackTrace(throwableItem.getStackTrace());
                    return resultThrowable;
                } catch (InstantiationException | IllegalAccessException ex) {
                    log.error("InstantiationException or IllegalAccessException.",
                        SensitiveExceptionUtils.hideSensitiveInfo(ex));
                }
            }
        }
        return null;
    }
}

