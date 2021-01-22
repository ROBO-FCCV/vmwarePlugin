/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.datastore.DatastoreCreate;

import com.vmware.vim25.ManagedObjectReference;

import java.util.List;

/**
 * DatastoreCreate Service
 *
 * @since 2020-09-16
 */
public interface DatastoreService {
    /**
     * Create datastore
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @param datastoreCreate datastore info
     * @return managedObjectReference
     */
    ManagedObjectReference createDatastore(String vmwareId, String hostId, DatastoreCreate datastoreCreate);

    /**
     * Delete datastore
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @param datastoreId datastore id
     * @return delete result
     */
    String delDatastore(String vmwareId, String hostId, String datastoreId);

    /**
     * Query datastore below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return datastore basic info
     */
    List<DatastoreBasic> datastore(String vmwareId, String hostId);

    /**
     * Query datastore
     *
     * @param vmwareId vmware id
     * @return datastore basic info
     */
    List<DatastoreBasic> datastore(String vmwareId);

    /**
     * Query datastore
     *
     * @param vmwareId vmware id
     * @param domainId domain id
     * @return datastore
     */
    List<DatastoreBasic> clusterDataStore(String vmwareId, String domainId);
}
