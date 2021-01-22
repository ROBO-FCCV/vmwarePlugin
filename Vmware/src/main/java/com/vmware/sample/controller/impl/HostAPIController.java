/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.HostController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.host.HostBasic;
import com.vmware.sample.model.host.HostBusAdapterVo;
import com.vmware.sample.service.HostService;

import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.ManagedObjectReference;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Host API Controller
 *
 * @since 2020-09-14
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class HostAPIController implements HostController {
    private final HostService hostService;

    @Autowired
    public HostAPIController(@Qualifier("host-api-service") HostService hostService) {
        this.hostService = hostService;
    }

    @Override
    public RestResult<HostBasic> getHostBasicInfo(String vmwareId, String hostId) {
        return RestResult.success(hostService.getHost(vmwareId, hostId));
    }

    @Override
    public RestResult<List<HostBasic>> list(String vmwareId) {
        return RestResult.success(hostService.list(vmwareId));
    }

    @Override
    public RestResult<List<HostScsiDisk>> availableDisks(String vmwareId, String hostId) {
        return RestResult.success(hostService.availableDisks(vmwareId, hostId));
    }

    @Override
    public RestResult<List<String>> queryStorageScsiLun(String vmwareId, String hostId) {
        return RestResult.success(hostService.queryStorageScsiLun(vmwareId, hostId));
    }

    @Override
    public RestResult<HostBusAdapterVo> busAdapters(String vmwareId, String hostId) {
        return RestResult.success(hostService.busAdapters(vmwareId, hostId));
    }

    @Override
    public RestResult<String> rescanHba(String vmwareId, String hostId) {
        return RestResult.success(hostService.rescanHba(vmwareId, hostId));
    }

    @Override
    public RestResult<ManagedObjectReference> resourcePool(String vmwareId, String hostId) {
        return RestResult.success(hostService.resourcePool(vmwareId, hostId));
    }

    @Override
    public RestResult<String> querySerialNumber(String vmwareId, String hostId) {
        return RestResult.success(hostService.querySerialNumber(vmwareId, hostId));
    }
}
