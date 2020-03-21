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
public class Disk {

    /**
     * diskName : Hard disk 1
     * disksize : 500
     * thinProvisioned : true
     */

    @ApiModelProperty(value = "硬盘名称", example = "Hard disk 1", required = true)
    private String diskName;
    @ApiModelProperty(value = "硬盘大小", example = "500", required = true)
    private int diskSize;
    @ApiModelProperty(value = "是否为精简置备:true,false", example = "true", required = true)
    private boolean thinProvisioned;

    /**
     * Gets disk name.
     *
     * @return the disk name
     */
    public String getDiskName() {
        return diskName;
    }

    /**
     * Sets disk name.
     *
     * @param diskName the disk name
     */
    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    /**
     * Gets disk size.
     *
     * @return the disk size
     */
    public int getDiskSize() {
        return diskSize;
    }

    /**
     * Sets disk size.
     *
     * @param diskSize the disk size
     */
    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    /**
     * Is thin provisioned boolean.
     *
     * @return the boolean
     */
    public boolean isThinProvisioned() {
        return thinProvisioned;
    }

    /**
     * Sets thin provisioned.
     *
     * @param thinProvisioned the thin provisioned
     */
    public void setThinProvisioned(boolean thinProvisioned) {
        this.thinProvisioned = thinProvisioned;
    }
}
