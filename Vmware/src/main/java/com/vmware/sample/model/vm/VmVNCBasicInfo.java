/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Vm basic info
 *
 * @since 2020-09-27
 */
@Setter
@Getter
public class VmVNCBasicInfo {
    /**
     * port
     */
    private String vncPort;

    /**
     * host
     */
    private String vncHost;

    /**
     * password
     */
    private String vncPassword;
    /**
     * virtual machine name
     */
    private String vmName;

    /**
     * os type
     */
    private String osType;
}
