/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.network;

import lombok.Getter;
import lombok.Setter;

/**
 * Network
 *
 * @since 2020-11-06
 */
@Getter
@Setter
public class Network {
    private String netmask;
    private String gateway;
    private String ipAddress;
    private String macAddress;
    private String switchUuid;
    private PortGroupUrn portGroupUrn;
    private String type;
}
