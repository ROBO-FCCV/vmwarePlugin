/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.storage.HostDisk;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface StorageService {
    /**
     * Gets disks.
     *
     * @param vmwareId the vmware id
     * @param datacenterName the datacenter name
     * @param hostName the host name
     * @return the disks
     * @throws CustomException the custom exception
     */
    List<HostDisk> getDisks(String vmwareId, String datacenterName, String hostName) throws CustomException;

    /**
     * Gets storage wwn.
     *
     * @param vmwareId the vmware id
     * @param hostName the host name
     * @return the storage wwn
     * @throws CustomException the custom exception
     */
    List<String> getStorageWwn(String vmwareId, String hostName) throws CustomException;
}
