/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.host.HostBasic;
import com.vmware.sample.model.host.HostBusAdapterVo;

import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.ManagedObjectReference;

import java.util.List;

/**
 * Host service
 *
 * @since 2020-09-25
 */
public interface HostService {
    /**
     * Obtain basic annotation information.
     *
     * @param vmwareId VMware id
     * @param hostId host id
     * @return basic info
     */
    HostBasic getHost(String vmwareId, String hostId);

    /**
     * Querying all VMware hosts
     *
     * @param vmwareId vmware id
     * @return all independent hosts
     */
    List<HostBasic> list(String vmwareId);

    /**
     * Query available disks below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return hostScsiDisks
     */
    List<HostScsiDisk> availableDisks(String vmwareId, String hostId);

    /**
     * Query wwn below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return wwn
     */
    List<String> queryStorageScsiLun(String vmwareId, String hostId);

    /**
     * Rescan hba
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return rescan result
     */
    String rescanHba(String vmwareId, String hostId);

    /**
     * Query bus adapter below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return bus adapter
     */
    HostBusAdapterVo busAdapters(String vmwareId, String hostId);

    /**
     * Query resource pool
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return resource pool
     */
    ManagedObjectReference resourcePool(String vmwareId, String hostId);

    /**
     * Query given esxi host serial number
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return serial number
     */
    String querySerialNumber(String vmwareId, String hostId);
}
