/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * Disk info
 *
 * @since 2020-09-30
 */
@Getter
@Setter
public class DiskInfo {
    /**
     * disk memory
     */
    @Min(1)
    private Long memory;

    /**
     * is thin
     */
    private boolean thin = true;

}
