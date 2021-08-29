/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.enums;

import lombok.Getter;

/**
 * Rest code enum
 *
 * @since 2020-09-14
 */
@Getter
public enum RestCodeEnum {
    SUCCESS("0", "Success."),
    URL_NOT_FOUND("0000", "The url does not found."),
    SYSTEM_ERROR("0001", "System Error."),
    UNAUTHORIZED("0002", "Unauthorized."),
    MAXIMUM_CONNECTION_CODE("0003", "Tokens of the ip exceed the maximum."),
    VMWARE_NOT_EXISTED("0004", "The vmware does not exist"),
    CONNECTION_EXCEPTION("0005", "Connection Exception."),
    ILLEGAL_PARAMS("0006", "Illegal params."),
    VMWARE_LOGIN_FAILED("0007", "VMware login failed."),
    IP_EXIST("0008", "VMware login failed."),
    CREATE_DATASTORE_ERROR("0009", "Create datastore error."),
    DELETE_DATASTORE_ERROR("0010", "Delete datastore error."),
    API_NONE_IMPLEMENT("0011", "Api hasn't implement."),
    SDK_NONE_IMPLEMENT("0012", "Sdk hasn't implement."),
    PLUGIN_INITIALIZED("0013", "The plugin was initialized."),
    PLUGIN_NEED_INITIALIZE("0014", "The plugin need initialize."),
    CREATE_VM_ERROR("0016", "Create VirtualMachine error."),
    DELETE_VM_ERROR("0017", "Delete VirtualMachine error."),
    POWER_ON_VM_ERROR("0018", "Power on VirtualMachine error."),
    POWER_OFF_VM_ERROR("0019", "Power off VirtualMachine error."),
    RESET_VM_ERROR("0020", "Reset VirtualMachine error."),
    GET_VM_ERROR("0021", "Get VirtualMachine error."),
    ILLEGAL_INPUT_PARAMS_ERROR("0022", " Illegal input params."),
    KMC_ERROR("0015", "The com.huawei.kmc error.");

    /**
     * 错误代码
     */
    private final String code;
    /**
     * 错误信息
     */
    private final String msg;

    RestCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * Get rest enum from code
     *
     * @param code code
     * @return enum
     */
    public static RestCodeEnum fromValue(String code) {
        RestCodeEnum[] var1 = values();
        for (RestCodeEnum var4 : var1) {
            if (var4.code.equals(code)) {
                return var4;
            }
        }
        throw new IllegalArgumentException(code);
    }
}
