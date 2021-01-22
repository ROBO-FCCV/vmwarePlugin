/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.DatastoreController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.datastore.DatastoreCreate;
import com.vmware.sample.service.DatastoreService;

import com.vmware.vim25.ManagedObjectReference;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Datastore api controller
 *
 * @since 2020-09-21
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class DatastoreAPIController implements DatastoreController {
    private final DatastoreService datastoreService;

    public DatastoreAPIController(@Qualifier("datastore-api-service") DatastoreService datastoreService) {
        this.datastoreService = datastoreService;
    }

    @Override
    public RestResult<ManagedObjectReference> createDatastore(String vmwareId, String hostId,
        DatastoreCreate datastoreCreate) {
        return RestResult.success(datastoreService.createDatastore(vmwareId, hostId, datastoreCreate));
    }

    @Override
    public RestResult<List<DatastoreBasic>> dataStore(String vmwareId, String hostId) {
        return RestResult.success(datastoreService.datastore(vmwareId, hostId));
    }

    @Override
    public RestResult<List<DatastoreBasic>> clusterDataStore(String vmwareId, String domainId) {
        return RestResult.success(datastoreService.clusterDataStore(vmwareId, domainId));
    }

    @Override
    public RestResult<List<DatastoreBasic>> dataStore(String vmwareId) {
        return RestResult.success(datastoreService.datastore(vmwareId));
    }

    @Override
    public RestResult<String> delDatastore(String vmwareId, String hostId, String datastoreId) {
        return RestResult.success(datastoreService.delDatastore(vmwareId, hostId, datastoreId));
    }
}
