/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.constant;

/**
 * 功能描述
 *
 * @since 2019 -09-20
 */
public interface ErrorCode {
    /**
     * The constant SUCCESS_CODE.
     */
    String SUCCESS_CODE = "0";

    /**
     * The constant SUCCESS_MSG.
     */
    String SUCCESS_MSG = "Success";

    /**
     * The constant URL_NOT_FOUND_CODE.
     */
    String URL_NOT_FOUND_CODE = "0000";

    /**
     * The constant URL_NOT_FOUND_MSG.
     */
    String URL_NOT_FOUND_MSG = "The url does not found.";

    /**
     * The constant SYSTEM_ERROR_CODE.
     */
    String SYSTEM_ERROR_CODE = "0001";

    /**
     * The constant SYSTEM_ERROR_MSG.
     */
    String SYSTEM_ERROR_MSG = "System Error.";

    /**
     * The constant UNAUTHORIZED_CODE.
     */
    String UNAUTHORIZED_CODE = "0002";

    /**
     * The constant UNAUTHORIZED_MSG.
     */
    String UNAUTHORIZED_MSG = "Unauthorized.";

    /**
     * The constant MAXIMUM_CONNECTION_CODE.
     */
    String MAXIMUM_CONNECTION_CODE = "0003";

    /**
     * The constant MAXIMUM_CONNECTION_MSG.
     */
    String MAXIMUM_CONNECTION_MSG = "Tokens of the ip exceed the maximum.";

    /**
     * The constant USERNAME_PASSWORD_WRONG_CODE.
     */
    String USERNAME_PASSWORD_WRONG_CODE = "0004";

    /**
     * The constant USERNAME_PASSWORD_WRONG_MSG.
     */
    String USERNAME_PASSWORD_WRONG_MSG = "The username or password is wrong.";

    /**
     * The constant VMWAREID_NOT_EXISTED_CODE.
     */
    String VMWAREID_NOT_EXISTED_CODE = "0005";

    /**
     * The constant VMWAREID_NOT_EXISTED_MSG.
     */
    String VMWAREID_NOT_EXISTED_MSG = "The vmwareId does not exist in vmware.yml.";

    /**
     * The constant GET_VMWARE_YML_FAILED_CODE.
     */
    String GET_VMWARE_YML_FAILED_CODE = "0006";

    /**
     * The constant GET_VMWARE_YML_FAILED_MSG.
     */
    String GET_VMWARE_YML_FAILED_MSG = "Get vmware yml failed.";

    /**
     * The constant VMWARE_YML_EMPTY_CODE.
     */
    String VMWARE_YML_EMPTY_CODE = "0007";

    /**
     * The constant VMWARE_YML_EMPTY_MSG.
     */
    String VMWARE_YML_EMPTY_MSG = "Vmware map yaml is empty.";

    /**
     * The constant VMWARE_INFO_ILLEGAL_CODE.
     */
    String VMWARE_INFO_ILLEGAL_CODE = "0008";

    /**
     * The constant VMWARE_INFO_ILLEGAL_MSG.
     */
    String VMWARE_INFO_ILLEGAL_MSG = "Vmware info is empty.";

    /**
     * The constant ILLEGAL_INPUT_PARAMS_CODE.
     */
    String ILLEGAL_INPUT_PARAMS_CODE = "0009";

    /**
     * The constant ILLEGAL_INPUT_PARAMS_MSG.
     */
    String ILLEGAL_INPUT_PARAMS_MSG = "Illegal input params.";

    /**
     * The constant CONNECTION_EXCEPTION_CODE.
     */
    String CONNECTION_EXCEPTION_CODE = "0010";

    /**
     * The constant CONNECTION_EXCEPTION_MSG.
     */
    String CONNECTION_EXCEPTION_MSG = "Connection Exception.";

    /**
     * The constant FAILED_CODE.
     */
    String FAILED_CODE = "0011";

    /**
     * The constant PARAMETER_ERROR_CODE.
     */
    String PARAMETER_ERROR_CODE = "0014";

    /**
     * The constant CREATE_DATASTORE_EXCEPTION_MSG.
     */
    String CREATE_DATASTORE_EXCEPTION_MSG = "Create Datastore Exception.";

    /**
     * The constant RESCAN_ALL_HBA_EXCEPTION_MSG.
     */
    String RESCAN_ALL_HBA_EXCEPTION_MSG = "Rescan all hba Exception.";

    /**
     * The constant GET_DISK_EXCEPTION_MSG.
     */
    String GET_DISK_EXCEPTION_MSG = "Get disks Exception.";

    /**
     * The constant GET_TASK_STATUS_EXCEPTION_MSG.
     */
    String GET_TASK_STATUS_EXCEPTION_MSG = "Get task status exception.";

    /**
     * The constant PARAMETER_ERROR_MSG.
     */
    String PARAMETER_ERROR_MSG = "Parameter Error.";

    /**
     * The constant IP_UNREACHABLE_ERROR_MSG.
     */
    String IP_UNREACHABLE_ERROR_MSG = "The IP is unreachable.";

    /**
     * The constant VMWARE_LOGIN_FAILED_ERROR_MSG.
     */
    String VMWARE_LOGIN_FAILED_ERROR_MSG = "Login the vCenter failed.";

    /**
     * The constant VMWARE_INFO_HAS_BEEN_DELETED_ERROR_CODE.
     */
    String VMWARE_INFO_HAS_BEEN_DELETED_ERROR_CODE = "0012";

    /**
     * The constant VMWARE_INFO_HAS_BEEN_DELETED_ERROR_MSG.
     */
    String VMWARE_INFO_HAS_BEEN_DELETED_ERROR_MSG = "The vmware info has been deleted in vmware.yml.";

    /**
     * The constant CONNECT_VCENTER_ERROR_CODE.
     */
    String CONNECT_VCENTER_ERROR_CODE = "0013";

    /**
     * The constant CONNECT_VCENTER_ERROR_MSG.
     */
    String CONNECT_VCENTER_ERROR_MSG = "Failed to connect the vCenter.";
}
