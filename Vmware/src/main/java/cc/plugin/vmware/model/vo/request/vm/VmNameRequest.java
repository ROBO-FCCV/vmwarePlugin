/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * 查询设置的虚拟机实体类
 *
 * @since 2019 -09-16
 */
public class VmNameRequest {
    /**
     * 虚拟机名称
     */
    @ApiModelProperty(value = "虚拟机名称", example = "vm-new", required = true)
    @NotEmpty
    private String vmName;

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
     * @return the vm name
     */
    public VmNameRequest setVmName(String vmName) {
        this.vmName = vmName;
        return this;
    }
}
