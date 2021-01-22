/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.cluster.ClusterBasic;
import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.model.host.HostVM;

import java.util.Collections;
import java.util.List;

/**
 * Cluster interface
 *
 * @since 2020-09-15
 */
public interface ClusterService {
    /**
     * Get cluster info
     *
     * @param vmwareId vmware id
     * @param clusterId cluster id
     * @return cluster info
     */
    ClusterBasic get(String vmwareId, String clusterId);

    /**
     * Get cluster info
     *
     * @param vmwareId vmware id
     * @param clusterId cluster id
     * @return clusters
     */
    ClusterInfo getClusterInfo(String vmwareId, String clusterId);

    /**
     * Query clusters
     *
     * @param vmwareId vmware id
     * @return clusters
     */
    List<ClusterInfo> list(String vmwareId);

    /**
     * Query hosts and vms below cluster
     *
     * @param vmwareId vmware id
     * @param clusterId cluster id
     * @return hosts and vms
     */
    default List<HostVM> queryHostsAndVms(String vmwareId, String clusterId) {
        return Collections.emptyList();
    }
}
