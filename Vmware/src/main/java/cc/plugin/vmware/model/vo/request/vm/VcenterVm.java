/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * 请求vcenter虚拟机性能数据model
 *
 * @since 2019 -09-19
 */
public class VcenterVm {
    /**
     * 虚拟机id
     */
    @ApiModelProperty(value = "虚拟机id", example = "6c0a814", required = true)
    private String id;
    /**
     * 虚拟机名称
     */
    @ApiModelProperty(value = "虚拟机名称", example = "test", required = true)
    private String vmName;
    /**
     * 虚拟机ip
     */
    @ApiModelProperty(value = "虚拟机ip", example = "192.0.2.0", required = true)
    private String vmIp;
    /**
     * 所属主机id
     */
    @ApiModelProperty(value = "所属主机id", example = "55d17486211cf6", required = true)
    private String hostId;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
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

    /**
     * Gets vm ip.
     *
     * @return the vm ip
     */
    public String getVmIp() {
        return vmIp;
    }

    /**
     * Sets vm ip.
     *
     * @param vmIp the vm ip
     */
    public void setVmIp(String vmIp) {
        this.vmIp = vmIp;
    }

    /**
     * Gets host id.
     *
     * @return the host id
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Sets host id.
     *
     * @param hostId the host id
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
}
