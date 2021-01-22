/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.cluster;

import com.vmware.sample.model.host.HostInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Cluster information
 *
 * @since 2020-09-18
 */
@Getter
@Setter
public class ClusterInfo {
    private String name;
    private String moId;
    private boolean haEnabled;
    private boolean drsEnabled;
    private List<HostInfo> hosts;
}
