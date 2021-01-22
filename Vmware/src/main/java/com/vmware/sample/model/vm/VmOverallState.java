/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Virtual machine overallstate
 *
 * @since 2020-09-19
 */
@Getter
@Setter
public class VmOverallState {
    /**
     * virtual machine id
     */
    private String vmId;

    /**
     * virtual machine status
     */
    private String state;
}
