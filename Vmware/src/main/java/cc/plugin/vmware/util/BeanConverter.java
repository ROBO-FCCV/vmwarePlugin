/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO对象转换器
 *
 * @since 2019 -10-16
 */
public final class BeanConverter {
    private static final Logger logger = LoggerFactory.getLogger(BeanConverter.class);

    /**
     * 批量转换
     *
     * @param <S> 原对象的类
     * @param <T> 目标对象的类
     * @param sourceObjects 原对象集合
     * @param targetCls 目标对象的Class
     * @param ignoreProperties 转换时忽略的原对象属性
     * @param afterConvert 转换后置处理器
     * @return 目标对象集合 list
     */
    public static <S, T> List<T> convertListWithClass(
        List<S> sourceObjects,
        Class<T> targetCls,
        String[] ignoreProperties,
        AfterConvert<S, T> afterConvert) {
        List<T> targetObjects = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceObjects)) {
            return targetObjects;
        }
        for (S sourceObject : sourceObjects) {
            T targetObject = convertWithClass(sourceObject, targetCls, ignoreProperties, afterConvert);
            if (targetObject != null) {
                targetObjects.add(targetObject);
            } else {
                logger.warn("[op:convertListWithClass] null object in list");
            }
        }
        return targetObjects;
    }

    /**
     * 单对象转换
     *
     * @param <S> 原对象的类
     * @param <T> 目标对象的类
     * @param sourceObject 原对象
     * @param targetCls 目标对象的类
     * @param ignoreProperties 转换时忽略的原对象属性
     * @param afterConvert 转换后置处理器
     * @return 目标对象 t
     */
    public static <S, T> T convertWithClass(
        S sourceObject,
        Class<T> targetCls,
        String[] ignoreProperties,
        AfterConvert<S, T> afterConvert) {
        T targetObject = null;
        try {
            targetObject = targetCls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("[op:convert] {} new instance exception", targetCls.getSimpleName());
        }
        convert(sourceObject, targetObject, ignoreProperties, afterConvert);
        return targetObject;
    }

    /**
     * 单对象转换
     *
     * @param <S> 原对象的类
     * @param <T> 目标对象的类
     * @param sourceObject 原对象
     * @param targetObject 目标对象
     * @param ignoreProperties 转换时忽略的原对象属性
     * @param afterConvert 转换后置处理器
     */
    public static <S, T> void convert(
        S sourceObject,
        T targetObject,
        String[] ignoreProperties,
        AfterConvert<S, T> afterConvert) {
        if (sourceObject == null || targetObject == null) {
            return;
        }
        BeanUtils.copyProperties(sourceObject, targetObject, ignoreProperties);
        if (afterConvert != null) {
            afterConvert.afterConvert(sourceObject, targetObject);
        }
    }

    /**
     * 转换器后置处理闭包
     *
     * @param <S> 原对象的类
     * @param <T> 目标对象的类
     * @since 2019 -10-16
     */
    @FunctionalInterface
    public interface AfterConvert<S, T> {
        /**
         * 定制的处理器
         *
         * @param sourceObject 原对象
         * @param targetObject 目标对象
         */
        void afterConvert(S sourceObject, T targetObject);
    }
}
