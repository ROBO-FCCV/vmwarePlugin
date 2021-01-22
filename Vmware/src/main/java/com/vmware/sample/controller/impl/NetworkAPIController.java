/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.NetworkController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.network.NetworkInfo;
import com.vmware.sample.service.NetworkService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Network api controller
 *
 * @since 2020-09-22
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class NetworkAPIController implements NetworkController {
    private final NetworkService networkService;

    public NetworkAPIController(@Qualifier("network-api-service") NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public RestResult<List<NetworkInfo>> list(String vmwareId, String dataCenterId) {
        return RestResult.success(networkService.list(vmwareId, dataCenterId));
    }

    @Override
    public RestResult<List<NetworkInfo>> listByDomain(String vmwareId, String domainId) {
        return RestResult.success(networkService.listByDomain(vmwareId, domainId));
    }

    @Override
    public RestResult<List<NetworkInfo>> listByHost(String vmwareId, String hostId) {
        return RestResult.success(networkService.listByHost(vmwareId, hostId));
    }
}
