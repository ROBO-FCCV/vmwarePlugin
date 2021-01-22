/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Virtual machine VNC information
 *
 * @since 2020-09-25
 */
@Setter
@Getter
public class VmVNCInfo extends VmVNCBasicInfo {
    /**
     * virtual machine status
     */
    private String status;

    /**
     * ip
     */
    private String ip;
}
