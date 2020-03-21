/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.to;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * The type Cluster to.
 *
 * @since 2019 -12-18
 */
public class ClusterTo {
    @ApiModelProperty(value = "集群名称", example = "test_data", required = true)
    private String name;
    @ApiModelProperty(value = "集群唯一标识", example = "692ebfc7ff3549ff98a91ab0ffb73842", required = true)
    private String moId;
    @ApiModelProperty(value = "主机列表")
    private List<HostTo> hosts;
    @ApiModelProperty(value = "描述", example = "description", required = true)
    private String description;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public ClusterTo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets mo id.
     *
     * @return the mo id
     */
    public String getMoId() {
        return moId;
    }

    /**
     * Sets mo id.
     *
     * @param moId the mo id
     * @return the mo id
     */
    public ClusterTo setMoId(String moId) {
        this.moId = moId;
        return this;
    }

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
    public ClusterTo setHosts(List<HostTo> hosts) {
        this.hosts = hosts;
        return this;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     * @return the description
     */
    public ClusterTo setDescription(String description) {
        this.description = description;
        return this;
    }
}
