/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.datastore.DatastoreCreate;
import com.vmware.sample.service.DatastoreService;

import com.vmware.vcenter.Datastore;
import com.vmware.vcenter.DatastoreTypes;
import com.vmware.vim25.ManagedObjectReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Datastore api service implement
 *
 * @since 2020-09-21
 */
@Slf4j
@Service("datastore-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DatastoreAPIServiceImpl implements DatastoreService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public ManagedObjectReference createDatastore(String vmwareId, String hostId, DatastoreCreate datastoreCreate) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String delDatastore(String vmwareId, String hostId, String datastoreId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public List<DatastoreBasic> datastore(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public List<DatastoreBasic> datastore(String vmwareId) {
        Datastore stubConfiguration = vmwareAPIClient.getStubConfiguration(vmwareId, Datastore.class);
        DatastoreTypes.FilterSpec filterSpec = new DatastoreTypes.FilterSpec.Builder().build();
        List<DatastoreTypes.Summary> list = stubConfiguration.list(filterSpec);
        List<DatastoreBasic> datastoreBasics = new ArrayList<>();
        for (DatastoreTypes.Summary summary : list) {
            DatastoreBasic datastoreBasic = new DatastoreBasic();
            datastoreBasic.setCapacity(DataSize.ofBytes(summary.getCapacity()));
            datastoreBasic.setFreeSpace((DataSize.ofBytes(summary.getFreeSpace())));
            datastoreBasic.setUsedSpace((DataSize.ofBytes(summary.getCapacity() - summary.getFreeSpace())));
            datastoreBasic.setModId(summary.getDatastore());
            datastoreBasic.setName(summary.getName());
            datastoreBasic.setType(summary.getType().name());
            datastoreBasics.add(datastoreBasic);
        }
        return datastoreBasics;
    }

    @Override
    public List<DatastoreBasic> clusterDataStore(String vmwareId, String domainId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
