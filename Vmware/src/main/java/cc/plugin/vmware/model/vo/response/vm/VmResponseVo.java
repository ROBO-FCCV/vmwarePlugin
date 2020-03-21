/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * vm实体
 *
 * @since 2019 -09-16
 */
public class VmResponseVo {

    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    private String hostName;

    /**
     * 虚拟机id
     */
    @ApiModelProperty(value = "虚拟机ID", example = "vm-456", required = true)
    private String vmId;

    /**
     * 虚拟机名称
     */
    @ApiModelProperty(value = "虚拟机名称", example = "vm-new", required = true)
    private String vmName;

    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称", example = "redhat", required = true)
    private String osName;

    /**
     * uuid
     */
    @ApiModelProperty(value = "uuid", example = "nmegm965e6dw5s", required = true)
    private String uuid;

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
     * Gets os name.
     *
     * @return the os name
     */
    public String getOsName() {
        return osName;
    }

    /**
     * Sets os name.
     *
     * @param osName the os name
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets uuid.
     *
     * @param uuid the uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
