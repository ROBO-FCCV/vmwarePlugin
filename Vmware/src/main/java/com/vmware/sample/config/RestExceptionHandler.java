/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.util.SensitiveExceptionUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler
 *
 * @since 2020-10-27
 */
@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
    /**
     * Handle plugin exception
     *
     * @param pluginException plugin exception
     * @return result code
     */
    @ExceptionHandler(PluginException.class)
    public ResponseEntity<RestResult<String>> handlePluginException(PluginException pluginException) {
        RestResult<String> result = RestResult.fail(RestCodeEnum.fromValue(pluginException.getCode()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Handle exception
     *
     * @param exception exception
     * @return rest code
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResult<String>> handleNormalException(Exception exception) {
        log.error("Exception occur.", SensitiveExceptionUtils.hideSensitiveInfo(exception));
        RestResult<String> restResult = RestResult.fail(RestCodeEnum.SYSTEM_ERROR);
        return new ResponseEntity<>(restResult, HttpStatus.OK);
    }
}
