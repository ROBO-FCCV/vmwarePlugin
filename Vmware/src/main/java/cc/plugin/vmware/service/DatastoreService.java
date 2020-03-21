/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.datastore.DatastoreInfo;
import cc.plugin.vmware.model.vo.response.datastore.Datastore;
import cc.plugin.vmware.model.vo.response.datastore.DatastoreResponse;
import cc.plugin.vmware.model.vo.response.datastore.DatastoreVo;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface DatastoreService {
    /**
     * Delete datastore string.
     *
     * @param vmwareId the vmware id
     * @param datastoreId the datastore id
     * @return the string
     * @throws CustomException the custom exception
     */
    String deleteDatastore(String vmwareId, String datastoreId) throws CustomException;

    /**
     * Create datastore string.
     *
     * @param vmwareId the vmware id
     * @param datastoreInfo the datastore info
     * @return the string
     * @throws CustomException the custom exception
     */
    String createDatastore(String vmwareId, DatastoreInfo datastoreInfo) throws CustomException;

    /**
     * Gets vmware shared storage.
     *
     * @param vmwareId the vmware id
     * @return the vmware shared storage
     * @throws CustomException the custom exception
     */
    List<DatastoreVo> getVmwareSharedStorage(String vmwareId) throws CustomException;

    /**
     * Gets datastores by datacenter or host name.
     *
     * @param vmwareId the vmware id
     * @param datacenterName the datacenter name
     * @param hostName the host name
     * @return the datastores by datacenter or host name
     * @throws CustomException the custom exception
     */
    List<DatastoreResponse> getDatastoresByDatacenterOrHostName(String vmwareId, String datacenterName, String hostName)
        throws CustomException;

    /**
     * Gets datastore by filter type.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @param filterType the filter type
     * @return the datastore by filter type
     * @throws CustomException the custom exception
     */
    List<Datastore> getDatastoreByFilterType(String vmwareId, String hostId, String filterType) throws CustomException;
}
