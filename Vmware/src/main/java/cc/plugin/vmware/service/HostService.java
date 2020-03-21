/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.to.HostTo;
import cc.plugin.vmware.model.vo.request.DeployOvfRequest;
import cc.plugin.vmware.model.vo.request.ImportOvfRequest;
import cc.plugin.vmware.model.vo.request.host.HostRequest;
import cc.plugin.vmware.model.vo.response.OvfVo;
import cc.plugin.vmware.model.vo.response.host.EsxInfo;
import cc.plugin.vmware.model.vo.response.host.HostInfo;
import cc.plugin.vmware.model.vo.response.host.ImportResourceVo;
import cc.plugin.vmware.model.vo.response.storage.HostBusAdapterVo;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface HostService {
    /**
     * Gets host bus adapters.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the host bus adapters
     * @throws CustomException the custom exception
     */
    HostBusAdapterVo getHostBusAdapters(String vmwareId, String hostId) throws CustomException;

    /**
     * Gets import resource.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the import resource
     * @throws CustomException the custom exception
     */
    ImportResourceVo getImportResource(String vmwareId, String hostId) throws CustomException;

    /**
     * Rescan all hba boolean.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the boolean
     * @throws CustomException the custom exception
     */
    boolean rescanAllHba(String vmwareId, String hostId) throws CustomException;

    /**
     * Import ovf ovf vo.
     *
     * @param ovfRequest the ovf request
     * @param vmwareId the vmware id
     * @return the ovf vo
     * @throws Exception the exception
     */
    OvfVo importOvf(ImportOvfRequest ovfRequest, String vmwareId) throws Exception;

    /**
     * Deploy ovf ovf vo.
     *
     * @param ovfRequest the ovf request
     * @param vmwareId the vmware id
     * @return the ovf vo
     * @throws Exception the exception
     */
    OvfVo deployOvf(DeployOvfRequest ovfRequest, String vmwareId) throws Exception;

    /**
     * Gets importing ovf status.
     *
     * @param vmwareId the vmware id
     * @param sessionId the session id
     * @return the importing ovf status
     */
    String getImportingOvfStatus(String vmwareId, String sessionId);

    /**
     * Query esx info list list.
     *
     * @param vmwareId the vmware id
     * @param hostRequest the host request
     * @return the list
     * @throws CustomException the custom exception
     */
    List<EsxInfo> queryEsxInfoList(String vmwareId, HostRequest hostRequest) throws CustomException;

    /**
     * Gets serial number.
     *
     * @param vmwareId the vmware id
     * @param hostName the host name
     * @return the serial number
     * @throws CustomException the custom exception
     */
    String getSerialNumber(String vmwareId, String hostName) throws CustomException;

    /**
     * Gets host info.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the host info
     * @throws CustomException the custom exception
     */
    HostInfo getHostInfo(String vmwareId, String hostId) throws CustomException;

    /**
     * Gets host basic info.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the host basic info
     * @throws CustomException the custom exception
     */
    HostTo getHostBasicInfo(String vmwareId, String hostId) throws CustomException;
}
