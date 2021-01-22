/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * Virtual machine cpu information
 *
 * @since 2020-09-21
 */
@Getter
@Setter
public class CpuInfo {
    /**
     * cpu count
     */
    @Min(1)
    private Integer count;

    /**
     * cores per socket
     */
    @Min(1)
    private Integer coreSockets;
}
