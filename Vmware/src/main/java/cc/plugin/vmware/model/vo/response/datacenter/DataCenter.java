/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.datacenter;

import cc.plugin.vmware.model.vo.response.cluster.ClusterVO;
import cc.plugin.vmware.model.vo.response.host.Host;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -10-28
 */
public class DataCenter {
    /**
     * The Exsi host list.
     */
    @ApiModelProperty(value = "主机列表", required = true)
    List<Host> exsiHostList;
    /**
     * The Cluster list.
     */
    @ApiModelProperty(value = "集群列表", required = true)
    List<ClusterVO> clusterList;
    private String dataCenterName;

    /**
     * Gets data center name.
     *
     * @return the data center name
     */
    public String getDataCenterName() {
        return dataCenterName;
    }

    /**
     * Sets data center name.
     *
     * @param dataCenterName the data center name
     */
    public void setDataCenterName(String dataCenterName) {
        this.dataCenterName = dataCenterName;
    }

    /**
     * Gets exsi host list.
     *
     * @return the exsi host list
     */
    public List<Host> getExsiHostList() {
        return exsiHostList;
    }

    /**
     * Sets exsi host list.
     *
     * @param exsiHostList the exsi host list
     */
    public void setExsiHostList(List<Host> exsiHostList) {
        this.exsiHostList = exsiHostList;
    }

    /**
     * Gets cluster list.
     *
     * @return the cluster list
     */
    public List<ClusterVO> getClusterList() {
        return clusterList;
    }

    /**
     * Sets cluster list.
     *
     * @param clusterList the cluster list
     */
    public void setClusterList(List<ClusterVO> clusterList) {
        this.clusterList = clusterList;
    }

}
