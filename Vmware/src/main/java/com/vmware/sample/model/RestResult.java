/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model;

import com.vmware.sample.enums.RestCodeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * 接口返回统一对象 code为0时表示接口正常相应 <br>
 * msg为提示信息 <br>
 * data为响应数据
 *
 * @since 2019-10-21
 */
@Getter
@Setter
public class RestResult<T> {
    private String code = RestCodeEnum.SUCCESS.getCode();
    private String msg;
    private T data;

    public RestResult() {
        super();
    }

    public RestResult(String code, String msg, T data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回内部错误的rest对象，并且将错误信息封装在data中
     *
     * @param error 错误信息
     * @return rest对象
     */
    public static <T> RestResult<T> fail(T error) {
        RestResult<T> restResult = new RestResult<>();
        restResult.setMsg(RestCodeEnum.SYSTEM_ERROR.getMsg());
        restResult.setCode(RestCodeEnum.SYSTEM_ERROR.getCode());
        restResult.setData(error);
        return restResult;
    }

    /**
     * 重载方法，返回错误码对象生成的rest对象
     *
     * @param errorCodeEnum 错误码
     * @return rest对象
     */
    public static <T> RestResult<T> fail(RestCodeEnum errorCodeEnum) {
        RestResult<T> restResult = new RestResult<>();
        restResult.setMsg(errorCodeEnum.getMsg());
        restResult.setCode(errorCodeEnum.getCode());
        return restResult;
    }

    /**
     * 返回一个成功的restful结果
     *
     * @param data 成功的结果
     * @param <T> 泛型对象
     * @return 成功的restful风格结果
     */
    public static <T> RestResult<T> success(T data) {
        RestResult<T> restResult = new RestResult<>();
        restResult.setMsg(RestCodeEnum.SUCCESS.getMsg());
        restResult.setCode(RestCodeEnum.SUCCESS.getCode());
        restResult.setData(data);
        return restResult;
    }

    /**
     * 检查restful是否成功
     *
     * @return 是否成功
     */
    public boolean checkSuccess() {
        return code.equals(RestCodeEnum.SUCCESS.getCode());
    }
}
