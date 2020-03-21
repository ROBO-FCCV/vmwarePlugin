/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.cluster;

import io.swagger.annotations.ApiModelProperty;

/**
 * 集群资源实体
 *
 * @since 2019 -09-10
 */
public class ClusterResourceVo {

    @ApiModelProperty(value = "集群总数(mHz)", example = "51600", required = true)
    private int totalCpu;
    @ApiModelProperty(value = "集群内存总容量(MB)", example = "1046135", required = true)
    private double totalMemory;
    @ApiModelProperty(value = "集群空闲存储容量(GB)", example = "2849", required = true)
    private double freeDatastoreSpace;
    @ApiModelProperty(value = "集群空闲内存容量(MB)", example = "386660", required = true)
    private double freeMemory;
    @ApiModelProperty(value = "集群总存储容量(GB)", example = "17091", required = true)
    private double totalDatastoreSpace;
    @ApiModelProperty(value = "集群已使用CPU数量(mHz)", example = "12000", required = true)
    private int usedCpu;

    /**
     * Gets total cpu.
     *
     * @return the total cpu
     */
    public int getTotalCpu() {
        return totalCpu;
    }

    /**
     * Sets total cpu.
     *
     * @param totalCpu the total cpu
     */
    public void setTotalCpu(int totalCpu) {
        this.totalCpu = totalCpu;
    }

    /**
     * Gets total memory.
     *
     * @return the total memory
     */
    public double getTotalMemory() {
        return totalMemory;
    }

    /**
     * Sets total memory.
     *
     * @param totalMemory the total memory
     */
    public void setTotalMemory(double totalMemory) {
        this.totalMemory = totalMemory;
    }

    /**
     * Gets free datastore space.
     *
     * @return the free datastore space
     */
    public double getFreeDatastoreSpace() {
        return freeDatastoreSpace;
    }

    /**
     * Sets free datastore space.
     *
     * @param freeDatastoreSpace the free datastore space
     */
    public void setFreeDatastoreSpace(double freeDatastoreSpace) {
        this.freeDatastoreSpace = freeDatastoreSpace;
    }

    /**
     * Gets free memory.
     *
     * @return the free memory
     */
    public double getFreeMemory() {
        return freeMemory;
    }

    /**
     * Sets free memory.
     *
     * @param freeMemory the free memory
     */
    public void setFreeMemory(double freeMemory) {
        this.freeMemory = freeMemory;
    }

    /**
     * Gets total datastore space.
     *
     * @return the total datastore space
     */
    public double getTotalDatastoreSpace() {
        return totalDatastoreSpace;
    }

    /**
     * Sets total datastore space.
     *
     * @param totalDatastoreSpace the total datastore space
     */
    public void setTotalDatastoreSpace(double totalDatastoreSpace) {
        this.totalDatastoreSpace = totalDatastoreSpace;
    }

    /**
     * Gets used cpu.
     *
     * @return the used cpu
     */
    public int getUsedCpu() {
        return usedCpu;
    }

    /**
     * Sets used cpu.
     *
     * @param usedCpu the used cpu
     */
    public void setUsedCpu(int usedCpu) {
        this.usedCpu = usedCpu;
    }
}
