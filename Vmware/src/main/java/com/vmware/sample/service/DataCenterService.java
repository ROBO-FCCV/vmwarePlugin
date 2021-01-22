/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.datacenter.DataCenterInfo;

import java.util.List;

/**
 * DataCenter Service
 *
 * @since 2020-10-10
 */
public interface DataCenterService {
    /**
     * Query datacenters
     *
     * @param vmwareId vmware id
     * @return datacenters
     */
    List<DataCenterInfo> dataCenters(String vmwareId);
}
