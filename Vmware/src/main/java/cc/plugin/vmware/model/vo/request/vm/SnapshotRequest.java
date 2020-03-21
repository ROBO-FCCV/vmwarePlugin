/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * desc
 *
 * @since 2019 -10-19
 */
public class SnapshotRequest {
    @ApiModelProperty(value = "虚拟机id", example = "6c0a814", required = true)
    @NotEmpty
    private String vmId;
    @ApiModelProperty(value = "虚拟机描述", example = "xxxxxx", required = false)
    private String description;
    @ApiModelProperty(value = "快照名", example = "xxxxx", required = true)
    @NotEmpty
    private String name;

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
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }
}
