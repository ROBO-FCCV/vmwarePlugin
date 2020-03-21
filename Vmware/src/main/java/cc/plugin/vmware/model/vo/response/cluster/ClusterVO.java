/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.cluster;

import cc.plugin.vmware.model.vo.response.datastore.Datastore;
import cc.plugin.vmware.model.vo.response.host.Host;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * The type Cluster vo.
 *
 * @since 2019 -10-15
 */
public class ClusterVO {
    @ApiModelProperty(value = "集群名称", example = "test_data", required = true)
    private String name;
    @ApiModelProperty(value = "modId", example = "692ebfc7ff3549ff98a91ab0ffb73842", required = true)
    private String moId;
    @ApiModelProperty(value = "集群状态", example = "Normal", required = true)
    private String clusterStatus;
    @ApiModelProperty(value = "主机列表")
    private List<Host> hostList;
    @ApiModelProperty(value = "数据存储")
    private List<Datastore> datastoreLst;
    @ApiModelProperty(value = "描述", example = "description", required = true)
    private String description;

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
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets datastore lst.
     *
     * @return the datastore lst
     */
    public List<Datastore> getDatastoreLst() {
        return datastoreLst;
    }

    /**
     * Sets datastore lst.
     *
     * @param datastoreLst the datastore lst
     */
    public void setDatastoreLst(List<Datastore> datastoreLst) {
        this.datastoreLst = datastoreLst;
    }

    /**
     * Gets cluster status.
     *
     * @return the cluster status
     */
    public String getClusterStatus() {
        return clusterStatus;
    }

    /**
     * Sets cluster status.
     *
     * @param clusterStatus the cluster status
     */
    public void setClusterStatus(String clusterStatus) {
        this.clusterStatus = clusterStatus;
    }

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
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets host list.
     *
     * @return the host list
     */
    public List<Host> getHostList() {
        return hostList;
    }

    /**
     * Sets host list.
     *
     * @param hostList the host list
     */
    public void setHostList(List<Host> hostList) {
        this.hostList = hostList;
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
     */
    public void setMoId(String moId) {
        this.moId = moId;
    }

}
