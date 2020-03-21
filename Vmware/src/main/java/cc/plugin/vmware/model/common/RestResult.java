/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.common;

import io.swagger.annotations.ApiModelProperty;

/**
 * rest接口返回结构
 *
 * @param <T> the type parameter
 * @since 2019 -09-06
 */
public class RestResult<T> {

    @ApiModelProperty(value = "返回码", example = "0")
    private String code;

    @ApiModelProperty(value = "返回信息", example = "Success")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private T data;

    /**
     * 构造器
     */
    public RestResult() {
        super();
    }

    /**
     * Instantiates a new Rest result.
     *
     * @param code code
     * @param msg msg
     */
    public RestResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * code ,msg, data
     *
     * @param code resultCode
     * @param msg restMsg
     * @param data 数据
     */
    public RestResult(String code, String msg, T data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets msg.
     *
     * @param msg the msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(T data) {
        this.data = data;
    }
}
