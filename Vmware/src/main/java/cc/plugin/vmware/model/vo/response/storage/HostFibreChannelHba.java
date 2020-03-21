/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

import io.swagger.annotations.ApiModelProperty;

/**
 * 主机总线适配器
 *
 * @since 2019 -09-16
 */
public class HostFibreChannelHba extends HostBusAdapter {

    @ApiModelProperty(value = "端口wwn", example = "1152922127292175832", required = true)
    private long portWorldWideName;
    @ApiModelProperty(value = "节点wwn", example = "5101b5442bcc7000", required = true)
    private long nodeWorldWideName;
    @ApiModelProperty(value = "端口类型:fabric,loop,pointToPoint,unknown", example = "unknown", required = true)
    private String portType;
    @ApiModelProperty(value = "速度(bits/s)", example = "0", required = true)
    private long speed;

    /**
     * Instantiates a new Host fibre channel hba.
     */
    public HostFibreChannelHba() {
    }

    /**
     * Gets port world wide name.
     *
     * @return the port world wide name
     */
    public long getPortWorldWideName() {
        return portWorldWideName;
    }

    /**
     * Sets port world wide name.
     *
     * @param portWorldWideName the port world wide name
     * @return the port world wide name
     */
    public HostFibreChannelHba setPortWorldWideName(long portWorldWideName) {
        this.portWorldWideName = portWorldWideName;
        return this;
    }

    /**
     * Gets node world wide name.
     *
     * @return the node world wide name
     */
    public long getNodeWorldWideName() {
        return nodeWorldWideName;
    }

    /**
     * Sets node world wide name.
     *
     * @param nodeWorldWideName the node world wide name
     * @return the node world wide name
     */
    public HostFibreChannelHba setNodeWorldWideName(long nodeWorldWideName) {
        this.nodeWorldWideName = nodeWorldWideName;
        return this;
    }

    /**
     * Gets port type.
     *
     * @return the port type
     */
    public String getPortType() {
        return portType;
    }

    /**
     * Sets port type.
     *
     * @param portType the port type
     * @return the port type
     */
    public HostFibreChannelHba setPortType(String portType) {
        this.portType = portType;
        return this;
    }

    /**
     * Gets speed.
     *
     * @return the speed
     */
    public long getSpeed() {
        return speed;
    }

    /**
     * Sets speed.
     *
     * @param speed the speed
     * @return the speed
     */
    public HostFibreChannelHba setSpeed(long speed) {
        this.speed = speed;
        return this;
    }
}
