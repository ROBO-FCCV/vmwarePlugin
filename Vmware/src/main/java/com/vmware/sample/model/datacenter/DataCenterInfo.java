/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.datacenter;

import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.model.host.SingleHostInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DataCenter basic info
 *
 * @since 2020-09-16
 */
@Getter
@Setter
public class DataCenterInfo {
    private String name;
    private String id;
    private String status;
    private List<ClusterInfo> clusterInfos;
    private List<SingleHostInfo> hostInfos;
}
