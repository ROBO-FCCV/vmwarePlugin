/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.to.ClusterInfoTo;
import cc.plugin.vmware.model.vo.response.cluster.ClusterResourceVo;
import cc.plugin.vmware.model.vo.response.vm.VMVo;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface ClusterService {

    /**
     * Gets cluster info.
     *
     * @param vmwareId the vmware id
     * @param clusterId the cluster id
     * @return the cluster info
     * @throws CustomException the custom exception
     */
    ClusterInfoTo getClusterInfo(String vmwareId, String clusterId) throws CustomException;

    /**
     * Gets cluster vms.
     *
     * @param vmwareId the vmware id
     * @param clusterId the cluster id
     * @return the cluster vms
     * @throws CustomException the custom exception
     */
    List<VMVo> getClusterVms(String vmwareId, String clusterId) throws CustomException;
}
