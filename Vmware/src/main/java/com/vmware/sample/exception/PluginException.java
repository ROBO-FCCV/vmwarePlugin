/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.exception;

import com.vmware.sample.enums.RestCodeEnum;

import lombok.Getter;

/**
 * Custom plug-in exception.
 *
 * @since 2020-09-14
 */
@Getter
public class PluginException extends RuntimeException {
    /**
     * 异常code
     */
    private final String code;

    /**
     * 异常message
     */
    private final String message;

    public PluginException(RestCodeEnum restCodeEnum) {
        super(restCodeEnum.getMsg());
        this.message = restCodeEnum.getMsg();
        this.code = restCodeEnum.getCode();
    }
}
