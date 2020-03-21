/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 内存
 *
 * @since 2019 -09-16
 */
public class Memory {

    @ApiModelProperty(value = "主机内存", example = "20000", required = true)
    private long quantityMb;

    /**
     * Gets quantity mb.
     *
     * @return the quantity mb
     */
    public long getQuantityMb() {
        return quantityMb;
    }

    /**
     * Sets quantity mb.
     *
     * @param quantityMb the quantity mb
     */
    public void setQuantityMb(long quantityMb) {
        this.quantityMb = quantityMb;
    }
}
