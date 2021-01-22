/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.cluster.ClusterBasic;
import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.model.host.HostVM;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Cluster Controller
 *
 * @since 2020-09-18
 */
@Validated
public interface ClusterController {
    /**
     * Get cluster basic information
     *
     * @param vmwareId vmwareId
     * @param clusterId clusterId
     * @return cluster basic information
     */
    @GetMapping("/{vmwareId}/clusters/{clusterId}")
    RestResult<ClusterBasic> get(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.CID_REGEXP) String clusterId);

    /**
     * Get the cluster info and vms below it
     *
     * @param vmwareId vmware id
     * @param clusterId cluster id
     * @return cluster info and vms below it
     */
    @GetMapping("/{vmwareId}/clusters/{clusterId}/vms")
    RestResult<List<HostVM>> hostsAndVms(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.CID_REGEXP) String clusterId);

    /**
     * Get clusters by vmware id
     *
     * @param vmwareId vmware id
     * @return clusters
     */
    @GetMapping("/{vmwareId}/clusters")
    RestResult<List<ClusterInfo>> queryAllClusters(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);
}
