/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 功能描述
 *
 * @since 2019 -09-30
 */
public class RenameVmRequest {

    /**
     * 虚拟机id
     */
    @ApiModelProperty(value = "虚拟机id", example = "6c0a814", required = true)
    private String vmId;

    /**
     * 虚拟机名称
     */
    @ApiModelProperty(value = "虚拟机新名称", example = "test", required = true)
    private String newName;

    @ApiModelProperty(value = "虚拟机旧名称", example = "test", required = true)
    private String vmName;

    /**
     * Gets vm id.
     *
     * @return the vm id
     */
    public String getVmId() {
        return vmId;
    }

    /**
     * Sets vm id.
     *
     * @param vmId the vm id
     */
    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    /**
     * Gets new name.
     *
     * @return the new name
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Sets new name.
     *
     * @param newName the new name
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * Gets vm name.
     *
     * @return the vm name
     */
    public String getVmName() {
        return vmName;
    }

    /**
     * Sets vm name.
     *
     * @param vmName the vm name
     */
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }
}
