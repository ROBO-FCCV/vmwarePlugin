
/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.host;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -10-09
 */
public class EsxInfo {
    @ApiModelProperty(value = "总CPU-单位是GB", example = "2", required = true)
    private int totalCpu;

    @ApiModelProperty(value = "主机IP", example = "192.0.2.0", required = true)
    private String esxIp;

    @ApiModelProperty(value = "被使用的cpu-单位是GB", example = "1", required = true)
    private int useCpu;

    @ApiModelProperty(value = "空闲的cpu-单位是GB", example = "1", required = true)
    private int freeCpu;

    @ApiModelProperty(value = "总内存-单位是GB", example = "3", required = true)
    private int totalMemory;

    @ApiModelProperty(value = "被使用的内存-单位是GB", example = "3", required = true)
    private int usedMemory;

    @ApiModelProperty(value = "空闲的内存-单位是GB", example = "2", required = true)
    private int freeMemory;

    @ApiModelProperty(value = "总的存储-单位是GB", example = "56", required = true)
    private int totalDatastore;

    @ApiModelProperty(value = "空闲的存储-单位是GB", example = "12", required = true)
    private int freeDatastore;

    @ApiModelProperty(value = "被使用的存储-单位是GB", example = "24", required = true)
    private int usedDatastore;

    @ApiModelProperty(value = "主机ID", example = "host-8", required = true)
    private String hostId;

    @ApiModelProperty(value = "虚拟机数量", example = "1", required = true)
    private int vmNum;

    @ApiModelProperty(value = "", example = "true", required = true)
    private boolean inMaintenanceMode;

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
     * Gets esx ip.
     *
     * @return the esx ip
     */
    public String getEsxIp() {
        return esxIp;
    }

    /**
     * Sets esx ip.
     *
     * @param esxIp the esx ip
     */
    public void setEsxIp(String esxIp) {
        this.esxIp = esxIp;
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
     */
    public void setUseCpu(int useCpu) {
        this.useCpu = useCpu;
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
     */
    public void setFreeCpu(int freeCpu) {
        this.freeCpu = freeCpu;
    }

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
     */
    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
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
     */
    public void setUsedMemory(int usedMemory) {
        this.usedMemory = usedMemory;
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
     */
    public void setFreeMemory(int freeMemory) {
        this.freeMemory = freeMemory;
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
     */
    public void setTotalDatastore(int totalDatastore) {
        this.totalDatastore = totalDatastore;
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
     */
    public void setFreeDatastore(int freeDatastore) {
        this.freeDatastore = freeDatastore;
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
     */
    public void setUsedDatastore(int usedDatastore) {
        this.usedDatastore = usedDatastore;
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
     */
    public void setVmNum(int vmNum) {
        this.vmNum = vmNum;
    }

    /**
     * Gets in maintenance mode.
     *
     * @return the in maintenance mode
     */
    public boolean getInMaintenanceMode() {
        return inMaintenanceMode;
    }

    /**
     * Sets in maintenance mode.
     *
     * @param inMaintenanceMode the in maintenance mode
     */
    public void setInMaintenanceMode(boolean inMaintenanceMode) {
        this.inMaintenanceMode = inMaintenanceMode;
    }
}
