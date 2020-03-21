/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware;

import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.model.common.RestResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * Exception的统一处理类
 *
 * @since 2019 -09-20
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Json error handler rest result.
     *
     * @param ex the ex
     * @param httpServletResponse the http servlet response
     * @return the rest result
     */
    @ExceptionHandler(value = Exception.class)
    public RestResult jsonErrorHandler(Exception ex, HttpServletResponse httpServletResponse) {
        logger.error("", ex);
        if (ex instanceof MethodArgumentNotValidException || ex instanceof ConstraintViolationException) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return new RestResult(ErrorCode.ILLEGAL_INPUT_PARAMS_CODE, ErrorCode.ILLEGAL_INPUT_PARAMS_MSG);
        } else {
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new RestResult(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG);
        }
    }

}
