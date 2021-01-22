/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.host;

import com.vmware.vim25.HostSystemConnectionState;

import lombok.Getter;
import lombok.Setter;

/**
 * Host information
 *
 * @since 2019-09-18
 */
@Getter
@Setter
public class HostInfo {
    private String name;
    private String ipAddress;
    private String moId;
    private HostSystemConnectionState status;
    private boolean inMaintenanceMode;
}
