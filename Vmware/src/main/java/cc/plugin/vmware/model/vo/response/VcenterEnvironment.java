/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -09-10
 */
public class VcenterEnvironment {
    /**
     * 总内存
     */
    @ApiModelProperty(value = "总内存", example = "964015.80078125", required = true)
    private double totalMemMb;

    /**
     * 空闲内存
     */
    @ApiModelProperty(value = "空闲内存", example = "517095.80078125", required = true)
    private double freeMemMb;

    /**
     * 空闲内存
     */
    @ApiModelProperty(value = "已使用内存", example = "313607", required = true)
    private double usedMemory;

    /**
     * 总cpu
     */
    @ApiModelProperty(value = "总cpu", example = "144", required = true)
    private double totalCpu;

    /**
     * 空闲cpu
     */
    @ApiModelProperty(value = "空闲cpu", example = "67", required = true)
    private double usedCpu;

    /**
     * 总数据存储
     */
    @ApiModelProperty(value = "总数据存储", example = "36641", required = true)
    private double totaldatastorespace;

    /**
     * 空闲数据存储
     */
    @ApiModelProperty(value = "空闲数据存储", example = "25766", required = true)
    private double freedatastorespace;

    /**
     * Gets total mem mb.
     *
     * @return the total mem mb
     */
    public double getTotalMemMb() {
        return totalMemMb;
    }

    /**
     * Sets total mem mb.
     *
     * @param totalMemMb the total mem mb
     * @return the total mem mb
     */
    public VcenterEnvironment setTotalMemMb(double totalMemMb) {
        this.totalMemMb = totalMemMb;
        return this;
    }

    /**
     * Gets free mem mb.
     *
     * @return the free mem mb
     */
    public double getFreeMemMb() {
        return freeMemMb;
    }

    /**
     * Sets free mem mb.
     *
     * @param freeMemMb the free mem mb
     * @return the free mem mb
     */
    public VcenterEnvironment setFreeMemMb(double freeMemMb) {
        this.freeMemMb = freeMemMb;
        return this;
    }

    /**
     * Gets used memory.
     *
     * @return the used memory
     */
    public double getUsedMemory() {
        return usedMemory;
    }

    /**
     * Sets used memory.
     *
     * @param usedMemory the used memory
     * @return the used memory
     */
    public VcenterEnvironment setUsedMemory(double usedMemory) {
        this.usedMemory = usedMemory;
        return this;
    }

    /**
     * Gets total cpu.
     *
     * @return the total cpu
     */
    public double getTotalCpu() {
        return totalCpu;
    }

    /**
     * Sets total cpu.
     *
     * @param totalCpu the total cpu
     * @return the total cpu
     */
    public VcenterEnvironment setTotalCpu(double totalCpu) {
        this.totalCpu = totalCpu;
        return this;
    }

    /**
     * Gets used cpu.
     *
     * @return the used cpu
     */
    public double getUsedCpu() {
        return usedCpu;
    }

    /**
     * Sets used cpu.
     *
     * @param usedCpu the used cpu
     * @return the used cpu
     */
    public VcenterEnvironment setUsedCpu(double usedCpu) {
        this.usedCpu = usedCpu;
        return this;
    }

    /**
     * Gets totaldatastorespace.
     *
     * @return the totaldatastorespace
     */
    public double getTotaldatastorespace() {
        return totaldatastorespace;
    }

    /**
     * Sets totaldatastorespace.
     *
     * @param totaldatastorespace the totaldatastorespace
     * @return the totaldatastorespace
     */
    public VcenterEnvironment setTotaldatastorespace(double totaldatastorespace) {
        this.totaldatastorespace = totaldatastorespace;
        return this;
    }

    /**
     * Gets freedatastorespace.
     *
     * @return the freedatastorespace
     */
    public double getFreedatastorespace() {
        return freedatastorespace;
    }

    /**
     * Sets freedatastorespace.
     *
     * @param freedatastorespace the freedatastorespace
     * @return the freedatastorespace
     */
    public VcenterEnvironment setFreedatastorespace(double freedatastorespace) {
        this.freedatastorespace = freedatastorespace;
        return this;
    }
}
