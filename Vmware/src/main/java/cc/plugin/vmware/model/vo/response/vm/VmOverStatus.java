
/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -10-09
 */
public class VmOverStatus {
    @ApiModelProperty(value = "虚拟机Id", example = "5901", required = true)
    private String vmId;

    @ApiModelProperty(value = "overallStatus", example = "5901", required = true)
    private String overallStatus;

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
     * Gets overall status.
     *
     * @return the overall status
     */
    public String getOverallStatus() {
        return overallStatus;
    }

    /**
     * Sets overall status.
     *
     * @param overallStatus the overall status
     */
    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }
}
