/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Virtual machine vnc status info
 *
 * @since 2020-09-27
 */
@Getter
@Setter
public class VmVNCStatusInfo extends VmVNCBasicInfo {
    /**
     * version
     */
    private String version;

    /**
     * ticket
     */
    private String ticket;

    /**
     * whether the VNC login is allowed
     */
    private boolean vncEnabled;
}
