/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.to;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -10-28
 */
public class ClusterAndHostTo {
    /**
     * The Hosts.
     */
    @ApiModelProperty(value = "主机列表", required = true)
    List<HostTo> hosts;
    /**
     * The Clusters.
     */
    @ApiModelProperty(value = "集群列表", required = true)
    List<ClusterTo> clusters;
    @ApiModelProperty(value = "数据中心名称", example = "DC1", required = true)
    private String datacenterName;

    /**
     * Gets hosts.
     *
     * @return the hosts
     */
    public List<HostTo> getHosts() {
        return hosts;
    }

    /**
     * Sets hosts.
     *
     * @param hosts the hosts
     * @return the hosts
     */
    public ClusterAndHostTo setHosts(List<HostTo> hosts) {
        this.hosts = hosts;
        return this;
    }

    /**
     * Gets clusters.
     *
     * @return the clusters
     */
    public List<ClusterTo> getClusters() {
        return clusters;
    }

    /**
     * Sets clusters.
     *
     * @param clusters the clusters
     * @return the clusters
     */
    public ClusterAndHostTo setClusters(List<ClusterTo> clusters) {
        this.clusters = clusters;
        return this;
    }

    /**
     * Gets data center name.
     *
     * @return the data center name
     */
    public String getDatacenterName() {
        return datacenterName;
    }

    /**
     * Sets data center name.
     *
     * @param datacenterName the data center name
     * @return the data center name
     */
    public ClusterAndHostTo setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
        return this;
    }
}
