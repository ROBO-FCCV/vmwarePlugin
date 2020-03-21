/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.to.ClusterAndHostTo;
import cc.plugin.vmware.model.vo.response.VcenterEnvironment;
import cc.plugin.vmware.model.vo.response.datacenter.DataCenter;

import java.util.List;

/**
 * The interface Data center service.
 *
 * @since 2019 -09-23
 */
public interface DataCenterService {

    /**
     * Gets data center basic info.
     *
     * @param vmwareId the vmware id
     * @param isActiveHost the is active host
     * @return the data center basic info
     * @throws CustomException the custom exception
     * @throws FileFaultFaultMsg the file fault fault msg
     * @throws InvalidDatastoreFaultMsg the invalid datastore fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     */
    List<DataCenter> getDataCenterBasicInfo(String vmwareId, boolean isActiveHost)
        throws CustomException, FileFaultFaultMsg, InvalidDatastoreFaultMsg, RuntimeFaultFaultMsg;

    /**
     * Gets clusters and hosts.
     *
     * @param vmwareId the vmware id
     * @return the clusters and hosts
     * @throws CustomException the custom exception
     */
    List<DataCenter> getClustersAndHosts(String vmwareId) throws CustomException;

    /**
     * Gets vcenter basic info.
     *
     * @param vmwareId the vmware id
     * @return the vcenter basic info
     * @throws CustomException the custom exception
     */
    VcenterEnvironment getVcenterBasicInfo(String vmwareId) throws CustomException;

    /**
     * Gets hosts.
     *
     * @param vmwareId the vmware id
     * @return the hosts
     * @throws CustomException the custom exception
     */
    List<ClusterAndHostTo> getHosts(String vmwareId) throws CustomException;
}
