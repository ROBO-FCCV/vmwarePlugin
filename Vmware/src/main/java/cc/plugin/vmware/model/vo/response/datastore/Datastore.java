/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.datastore;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -10-28
 */
public class Datastore {
    @ApiModelProperty(value = "数据存储标识", example = "datastore-9", required = true)
    private String modId;

    @ApiModelProperty(value = "数据存储名称", example = "ds1", required = true)
    private String name;

    @ApiModelProperty(value = "可用容量", example = "249", required = true)
    private double freeSizeGB;

    @ApiModelProperty(value = "总容量", example = "2249", required = true)
    private double capacityGB;

    @ApiModelProperty(value = "已使用容量", example = "2000", required = true)
    private double usedSizeGB;

    /**
     * Gets mod id.
     *
     * @return the mod id
     */
    public String getModId() {
        return modId;
    }

    /**
     * Sets mod id.
     *
     * @param modId the mod id
     */
    public void setModId(String modId) {
        this.modId = modId;
    }

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
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets free size gb.
     *
     * @return the free size gb
     */
    public double getFreeSizeGB() {
        return freeSizeGB;
    }

    /**
     * Sets free size gb.
     *
     * @param freeSizeGB the free size gb
     */
    public void setFreeSizeGB(double freeSizeGB) {
        this.freeSizeGB = freeSizeGB;
    }

    /**
     * Gets capacity gb.
     *
     * @return the capacity gb
     */
    public double getCapacityGB() {
        return capacityGB;
    }

    /**
     * Sets capacity gb.
     *
     * @param capacityGB the capacity gb
     */
    public void setCapacityGB(double capacityGB) {
        this.capacityGB = capacityGB;
    }

    /**
     * Gets used size gb.
     *
     * @return the used size gb
     */
    public double getUsedSizeGB() {
        return usedSizeGB;
    }

    /**
     * Sets used size gb.
     *
     * @param usedSizeGB the used size gb
     */
    public void setUsedSizeGB(double usedSizeGB) {
        this.usedSizeGB = usedSizeGB;
    }
}
