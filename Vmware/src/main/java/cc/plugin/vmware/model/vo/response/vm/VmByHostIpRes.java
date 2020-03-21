/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-30
 */
public class VmByHostIpRes {
    @ApiModelProperty(value = "主机名称", example = "127.0.0.1", required = true)
    private String hostName;

    @ApiModelProperty(value = "虚拟机列表", required = true)
    private List<VmResponseVo> vms;

    /**
     * Gets host name.
     *
     * @return the host name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets host name.
     *
     * @param hostName the host name
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Gets vms.
     *
     * @return the vms
     */
    public List<VmResponseVo> getVms() {
        return vms;
    }

    /**
     * Sets vms.
     *
     * @param vms the vms
     */
    public void setVms(List<VmResponseVo> vms) {
        this.vms = vms;
    }
}
