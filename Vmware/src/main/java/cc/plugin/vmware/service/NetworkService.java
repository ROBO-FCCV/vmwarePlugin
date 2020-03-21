/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.NetworkVo;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface NetworkService {
    /**
     * Gets networks.
     *
     * @param vmwareId the vmware id
     * @param datacenterName the datacenter name
     * @param hostName the host name
     * @return the networks
     * @throws CustomException the custom exception
     */
    List<NetworkVo> getNetworks(String vmwareId, String datacenterName, String hostName) throws CustomException;

    /**
     * Gets esxi networks.
     *
     * @param vmwareId the vmware id
     * @param hostName the host name
     * @return the esxi networks
     * @throws CustomException the custom exception
     */
    List<String> getEsxiNetworks(String vmwareId, String hostName) throws CustomException;
}
