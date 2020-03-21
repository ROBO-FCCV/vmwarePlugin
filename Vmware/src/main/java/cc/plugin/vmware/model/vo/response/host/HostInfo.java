/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.host;

import io.swagger.annotations.ApiModelProperty;

/**
 * The type Host info.
 *
 * @since 2019 -09-19
 */
public class HostInfo {

    @ApiModelProperty(value = "总内存容量（MB）", example = "194611", required = true)
    private int totalMemory;
    @ApiModelProperty(value = "已使用内存容量（MB）", example = "13560", required = true)
    private int usedMemory;
    @ApiModelProperty(value = "总数据存储容量（MB）", example = "220160", required = true)
    private int totalDatastore;
    @ApiModelProperty(value = "已使用数据存储容量（MB）", example = "190509", required = true)
    private int usedDatastore;
    @ApiModelProperty(value = "总CPU核数", example = "32", required = true)
    private int totalCpu;
    @ApiModelProperty(value = "已使用CPU核数（MB）", example = "4", required = true)
    private int useCpu;
    @ApiModelProperty(value = "空闲数据存储容量（MB）", example = "29651", required = true)
    private int freeDatastore;
    @ApiModelProperty(value = "空闲内存容量（MB）", example = "181051", required = true)
    private int freeMemory;
    @ApiModelProperty(value = "空闲CPU核数", example = "28", required = true)
    private int freeCpu;
    @ApiModelProperty(value = "虚拟机总数", example = "3", required = true)
    private int vmNum;

    /**
     * Gets total memory.
     *
     * @return the total memory
     */
    public int getTotalMemory() {
        return totalMemory;
    }

    /**
     * Sets total memory.
     *
     * @param totalMemory the total memory
     * @return the total memory
     */
    public HostInfo setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
        return this;
    }

    /**
     * Gets used memory.
     *
     * @return the used memory
     */
    public int getUsedMemory() {
        return usedMemory;
    }

    /**
     * Sets used memory.
     *
     * @param usedMemory the used memory
     * @return the used memory
     */
    public HostInfo setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
        return this;
    }

    /**
     * Gets total datastore.
     *
     * @return the total datastore
     */
    public int getTotalDatastore() {
        return totalDatastore;
    }

    /**
     * Sets total datastore.
     *
     * @param totalDatastore the total datastore
     * @return the total datastore
     */
    public HostInfo setTotalDatastore(int totalDatastore) {
        this.totalDatastore = totalDatastore;
        return this;
    }

    /**
     * Gets used datastore.
     *
     * @return the used datastore
     */
    public int getUsedDatastore() {
        return usedDatastore;
    }

    /**
     * Sets used datastore.
     *
     * @param usedDatastore the used datastore
     * @return the used datastore
     */
    public HostInfo setUsedDatastore(int usedDatastore) {
        this.usedDatastore = usedDatastore;
        return this;
    }

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
     * @return the total cpu
     */
    public HostInfo setTotalCpu(int totalCpu) {
        this.totalCpu = totalCpu;
        return this;
    }

    /**
     * Gets use cpu.
     *
     * @return the use cpu
     */
    public int getUseCpu() {
        return useCpu;
    }

    /**
     * Sets use cpu.
     *
     * @param useCpu the use cpu
     * @return the use cpu
     */
    public HostInfo setUseCpu(int useCpu) {
        this.useCpu = useCpu;
        return this;
    }

    /**
     * Gets free datastore.
     *
     * @return the free datastore
     */
    public int getFreeDatastore() {
        return freeDatastore;
    }

    /**
     * Sets free datastore.
     *
     * @param freeDatastore the free datastore
     * @return the free datastore
     */
    public HostInfo setFreeDatastore(int freeDatastore) {
        this.freeDatastore = freeDatastore;
        return this;
    }

    /**
     * Gets free memory.
     *
     * @return the free memory
     */
    public int getFreeMemory() {
        return freeMemory;
    }

    /**
     * Sets free memory.
     *
     * @param freeMemory the free memory
     * @return the free memory
     */
    public HostInfo setFreeMemory(int freeMemory) {
        this.freeMemory = freeMemory;
        return this;
    }

    /**
     * Gets free cpu.
     *
     * @return the free cpu
     */
    public int getFreeCpu() {
        return freeCpu;
    }

    /**
     * Sets free cpu.
     *
     * @param freeCpu the free cpu
     * @return the free cpu
     */
    public HostInfo setFreeCpu(int freeCpu) {
        this.freeCpu = freeCpu;
        return this;
    }

    /**
     * Gets vm num.
     *
     * @return the vm num
     */
    public int getVmNum() {
        return vmNum;
    }

    /**
     * Sets vm num.
     *
     * @param vmNum the vm num
     * @return the vm num
     */
    public HostInfo setVmNum(int vmNum) {
        this.vmNum = vmNum;
        return this;
    }
}
