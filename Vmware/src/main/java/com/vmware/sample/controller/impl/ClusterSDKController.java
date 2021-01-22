/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.ClusterController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.cluster.ClusterBasic;
import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.model.host.HostVM;
import com.vmware.sample.service.ClusterService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Cluster sdk controller
 *
 * @since 2020-09-15
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/sdk")
public class ClusterSDKController implements ClusterController {
    private final ClusterService clusterService;

    @Autowired
    public ClusterSDKController(@Qualifier("cluster-sdk-service") ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @Override
    public RestResult<ClusterBasic> get(String vmwareId, String clusterId) {
        return RestResult.success(clusterService.get(vmwareId, clusterId));
    }

    @Override
    public RestResult<List<HostVM>> hostsAndVms(String vmwareId, String clusterId) {
        return RestResult.success(clusterService.queryHostsAndVms(vmwareId, clusterId));
    }

    @Override
    public RestResult<List<ClusterInfo>> queryAllClusters(String vmwareId) {
        return RestResult.success(clusterService.list(vmwareId));
    }
}
