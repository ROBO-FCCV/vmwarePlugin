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
public class HostSerialAttachedHba extends HostBusAdapter {

    @ApiModelProperty(value = "节点wwn", example = "5101b5442bcc7000", required = true)
    private String nodeWorldWideName;

    /**
     * Instantiates a new Host serial attached hba.
     */
    public HostSerialAttachedHba() {
    }

    /**
     * Gets node world wide name.
     *
     * @return the node world wide name
     */
    public String getNodeWorldWideName() {
        return nodeWorldWideName;
    }

    /**
     * Sets node world wide name.
     *
     * @param nodeWorldWideName the node world wide name
     * @return the node world wide name
     */
    public HostSerialAttachedHba setNodeWorldWideName(String nodeWorldWideName) {
        this.nodeWorldWideName = nodeWorldWideName;
        return this;
    }
}
