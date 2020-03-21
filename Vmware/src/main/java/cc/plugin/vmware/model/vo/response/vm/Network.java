/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * Vm查询返回结果
 *
 * @since 2019 -09-10
 */
public class Network {
    @ApiModelProperty(value = "网络名称", example = "VM Network", required = true)
    private String networkName;
    @ApiModelProperty(value = "网络类型", example = "VirtualVmxnet3", required = true)
    private String type;
    @ApiModelProperty(value = "状态", example = "true", required = true)
    private boolean status;

    /**
     * Gets network name.
     *
     * @return the network name
     */
    public String getNetworkName() {
        return networkName;
    }

    /**
     * Sets network name.
     *
     * @param networkName the network name
     */
    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Is status boolean.
     *
     * @return the boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
}
