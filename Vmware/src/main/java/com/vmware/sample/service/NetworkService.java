/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.network.NetworkInfo;

import java.util.List;

/**
 * Network service
 *
 * @since 2020-09-22
 */
public interface NetworkService {
    /**
     * List network by datacenter
     *
     * @param vmwareId vmware id
     * @param dataCenterId datacenter id
     * @return networks
     */
    List<NetworkInfo> list(String vmwareId, String dataCenterId);

    /**
     * List network by host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return networks
     */
    List<NetworkInfo> listByHost(String vmwareId, String hostId);

    /**
     * List network by compute resource
     *
     * @param vmwareId vmware id
     * @param domainId damain id
     * @return networks
     */
    List<NetworkInfo> listByDomain(String vmwareId, String domainId);
}
