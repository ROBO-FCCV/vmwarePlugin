/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 功能描述
 *
 * @since 2019 -09-16
 */
public class Cpu {

    @ApiModelProperty(value = "cpu数量", example = "2", required = true)
    private int quantity;

    @ApiModelProperty(value = "", example = "2", required = false)
    private String cpuCores;

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets quantity.
     *
     * @param quantity the quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets cpu cores.
     *
     * @return the cpu cores
     */
    public String getCpuCores() {
        return cpuCores;
    }

    /**
     * Sets cpu cores.
     *
     * @param cpuCores the cpu cores
     */
    public void setCpuCores(String cpuCores) {
        this.cpuCores = cpuCores;
    }
}
