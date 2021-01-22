/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.model.cluster.ClusterBasic;
import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.service.ClusterService;

import com.vmware.vcenter.Cluster;
import com.vmware.vcenter.ClusterTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Cluster api service implement
 *
 * @since 2020-09-22
 */
@Slf4j
@Service("cluster-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ClusterAPIServiceImpl implements ClusterService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public ClusterBasic get(String vmwareId, String clusterId) {
        Cluster cluster = vmwareAPIClient.getStubConfiguration(vmwareId, Cluster.class);
        ClusterTypes.Info info = cluster.get(clusterId);
        ClusterBasic clusterBasic = new ClusterBasic();
        BeanUtils.copyProperties(info, clusterBasic);
        return clusterBasic;
    }

    @Override
    public ClusterInfo getClusterInfo(String vmwareId, String clusterId) {
        Cluster cluster = vmwareAPIClient.getStubConfiguration(vmwareId, Cluster.class);
        ClusterTypes.Info info = cluster.get(clusterId);
        ClusterInfo clusterBasic = new ClusterInfo();
        BeanUtils.copyProperties(info, clusterBasic);
        return clusterBasic;
    }

    @Override
    public List<ClusterInfo> list(String vmwareId) {
        Cluster cluster = vmwareAPIClient.getStubConfiguration(vmwareId, Cluster.class);
        ClusterTypes.FilterSpec.Builder builder = new ClusterTypes.FilterSpec.Builder();
        List<ClusterTypes.Summary> list = cluster.list(builder.build());
        List<ClusterInfo> clusterInfos = new ArrayList<>();
        for (ClusterTypes.Summary summary : list) {
            ClusterInfo clusterInfo = new ClusterInfo();
            clusterInfo.setMoId(summary.getCluster());
            clusterInfo.setName(summary.getName());
            clusterInfo.setDrsEnabled(summary.getDrsEnabled());
            clusterInfo.setHaEnabled(summary.getHaEnabled());
            clusterInfos.add(clusterInfo);
        }
        return clusterInfos;
    }
}
