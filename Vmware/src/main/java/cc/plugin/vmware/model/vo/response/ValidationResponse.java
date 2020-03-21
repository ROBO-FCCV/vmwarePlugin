/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import io.swagger.annotations.ApiModelProperty;

/**
 * Network返回结果
 *
 * @since 2019 -09-10
 */
public class ValidationResponse {
    @ApiModelProperty(value = "校验结果", example = "true", required = true)
    private boolean validationResult;
    @ApiModelProperty(value = "Vmware标识", example = "abb60594c6804d59bc15ad6f63f9e5d7", required = false)
    private String vmwareId;

    /**
     * Is validation result boolean.
     *
     * @return the boolean
     */
    public boolean isValidationResult() {
        return validationResult;
    }

    /**
     * Sets validation result.
     *
     * @param validationResult the validation result
     * @return the validation result
     */
    public ValidationResponse setValidationResult(boolean validationResult) {
        this.validationResult = validationResult;
        return this;
    }

    /**
     * Gets vmware id.
     *
     * @return the vmware id
     */
    public String getVmwareId() {
        return vmwareId;
    }

    /**
     * Sets vmware id.
     *
     * @param vmwareId the vmware id
     * @return the vmware id
     */
    public ValidationResponse setVmwareId(String vmwareId) {
        this.vmwareId = vmwareId;
        return this;
    }
}
