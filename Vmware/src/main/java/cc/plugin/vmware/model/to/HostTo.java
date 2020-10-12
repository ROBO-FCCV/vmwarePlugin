/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.to;

import io.swagger.annotations.ApiModelProperty;

/**
 * The type Host to.
 *
 * @since 2019 -12-18
 */
public class HostTo {
    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    private String name;
    @ApiModelProperty(value = "主机IP地址", example = "192.0.2.0", required = true)
    private String ipAddress;
    @ApiModelProperty(value = "主机标识", example = "host-9", required = true)
    private String moId;
    @ApiModelProperty(value = "主机状态", example = "Normal", required = true)
    private String status;
    @ApiModelProperty(value = "是否为维护模式", example = "true", required = true)
    private boolean inMaintenanceMode;

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
     * @return the name
     */
    public HostTo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets ip address.
     *
     * @return the ip address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets ip address.
     *
     * @param ipAddress the ip address
     * @return the ip address
     */
    public HostTo setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * Gets mo id.
     *
     * @return the mo id
     */
    public String getMoId() {
        return moId;
    }

    /**
     * Sets mo id.
     *
     * @param moId the mo id
     * @return the mo id
     */
    public HostTo setMoId(String moId) {
        this.moId = moId;
        return this;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     * @return the status
     */
    public HostTo setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Is in maintenance mode boolean.
     *
     * @return the boolean
     */
    public boolean isInMaintenanceMode() {
        return inMaintenanceMode;
    }

    /**
     * Sets in maintenance mode.
     *
     * @param inMaintenanceMode the in maintenance mode
     * @return the in maintenance mode
     */
    public HostTo setInMaintenanceMode(boolean inMaintenanceMode) {
        this.inMaintenanceMode = inMaintenanceMode;
        return this;
    }
}
