/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.datastore.DatastoreCreate;

import com.vmware.vim25.ManagedObjectReference;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Datastore controller
 *
 * @since 2020-09-19
 */
@Validated
public interface DatastoreController {
    /**
     * create datastore
     *
     * @param vmwareId VMware id
     * @param hostId host id
     * @param datastoreCreate datastore create
     * @return task
     */
    @PostMapping("/{vmwareId}/hosts/{hostId}/datastore")
    RestResult<ManagedObjectReference> createDatastore(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.HID_REGEXP) String hostId,
        @RequestBody @Valid DatastoreCreate datastoreCreate);

    /**
     * List datastore below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return datastore
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/datastore")
    RestResult<List<DatastoreBasic>> dataStore(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.HID_REGEXP) String hostId);

    /**
     * List datastore below cluster
     *
     * @param vmwareId vmware id
     * @param domainId domain id
     * @return datastore
     */
    @GetMapping("/{vmwareId}/domains/{domainId}/datastore")
    RestResult<List<DatastoreBasic>> clusterDataStore(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.CID_REGEXP) String domainId);

    /**
     * List datastore
     *
     * @param vmwareId vmware id
     * @return datastore
     */
    @GetMapping("/{vmwareId}/datastore")
    RestResult<List<DatastoreBasic>> dataStore(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);

    /**
     * delete datastore
     *
     * @param vmwareId vmware id
     * @param datastoreId datastore id
     * @param hostId host id
     * @return delete result
     */
    @DeleteMapping("/{vmwareId}/hosts/{hostId}/datastore/{datastoreId}")
    RestResult<String> delDatastore(@NotNull @PathVariable @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.HID_REGEXP) String hostId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.DID_REGEXP) String datastoreId);
}
