/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Virtual machine's  disk info
 *
 * @since 2020-09-15
 */
@Setter
@Getter
public class DiskToInfo {
    /**
     * disk name
     */
    private String diskName;

    /**
     * disk size
     */
    private Long diskSize;

    /**
     * yes or no thin provisioning
     */
    private boolean thinProvisioned;
}
