/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.Constants;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.to.HostTo;
import cc.plugin.vmware.model.vo.request.DeployOvfRequest;
import cc.plugin.vmware.model.vo.request.ImportOvfRequest;
import cc.plugin.vmware.model.vo.request.host.HostRequest;
import cc.plugin.vmware.model.vo.response.OvfVo;
import cc.plugin.vmware.model.vo.response.host.EsxInfo;
import cc.plugin.vmware.model.vo.response.host.HostInfo;
import cc.plugin.vmware.model.vo.response.host.ImportResourceVo;
import cc.plugin.vmware.model.vo.response.storage.HostBlockHba;
import cc.plugin.vmware.model.vo.response.storage.HostBusAdapterVo;
import cc.plugin.vmware.model.vo.response.storage.HostFibreChannelHba;
import cc.plugin.vmware.model.vo.response.storage.HostInternetScsiHba;
import cc.plugin.vmware.model.vo.response.storage.HostSerialAttachedHba;
import cc.plugin.vmware.service.HostService;
import cc.plugin.vmware.token.LibraryCache;
import cc.plugin.vmware.util.BeanConverter;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.vsphere.ClsApiClient;
import cc.plugin.vmware.vsphere.DeployOvf;
import cc.plugin.vmware.vsphere.ImportOvf;

import com.alibaba.fastjson.JSONObject;
import com.vmware.content.library.item.UpdateSessionModel;
import com.vmware.vim25.ComputeResourceSummary;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.ElementDescription;
import com.vmware.vim25.HostConfigFaultFaultMsg;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostHostBusAdapter;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostListSummaryQuickStats;
import com.vmware.vim25.HostRuntimeInfo;
import com.vmware.vim25.HostSystemIdentificationInfo;
import com.vmware.vim25.HostSystemInfo;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSummary;
import com.vmware.vim25.VirtualMachineSummary;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
@Service
public class HostServiceImpl implements HostService {
    private static final Logger logger = LoggerFactory.getLogger(HostServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    private static final String VMWARE_CLOUD_OS_NAME = "vmware";

    @Override
    public HostBusAdapterVo getHostBusAdapters(String vmwareId, String hostId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc;
        svc = ecb.getServiceUtil();
        if (svc.connection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }

        ManagedObjectReference hostMO = new ManagedObjectReference();
        hostMO.setType("HostSystem");
        hostMO.setValue(hostId);
        HostConfigInfo hostConfig = (HostConfigInfo) svc.getDynamicProperty(hostMO, "config");
        List<HostHostBusAdapter> hostHostBusAdapters = Optional
            .ofNullable(hostConfig.getStorageDevice().getHostBusAdapter())
            .orElse(new ArrayList<>());
        return convertVmwareHostHostBusAdaptersToHostBusAdapters(hostHostBusAdapters);
    }

    private HostBusAdapterVo convertVmwareHostHostBusAdaptersToHostBusAdapters(
        List<HostHostBusAdapter> hostHostBusAdapters) {
        List<HostBlockHba> hostBlockHbas = hostHostBusAdapters
            .stream()
            .filter(hba -> StringUtils.equals(hba.getClass().getSimpleName(), "HostBlockHba"))
            .map(hba -> BeanConverter.convertWithClass(hba, HostBlockHba.class, null, null))
            .collect(Collectors.toList());
        List<HostFibreChannelHba> hostFibreChannelHbas = hostHostBusAdapters
            .stream()
            .filter(hba -> StringUtils.equals(hba.getClass().getSimpleName(), "HostFibreChannelHba"))
            .map(hba -> BeanConverter.convertWithClass(hba, HostFibreChannelHba.class, null, null))
            .collect(Collectors.toList());
        List<HostSerialAttachedHba> hostSerialAttachedHbas = hostHostBusAdapters
            .stream()
            .filter(hba -> StringUtils.equals(hba.getClass().getSimpleName(), "HostSerialAttachedHba"))
            .map(hba -> BeanConverter.convertWithClass(hba, HostSerialAttachedHba.class, null, null))
            .collect(Collectors.toList());
        List<HostInternetScsiHba> hostInternetScsiHbas = hostHostBusAdapters
            .stream()
            .filter(hba -> StringUtils.equals(hba.getClass().getSimpleName(), "HostInternetScsiHba"))
            .map(hba -> BeanConverter.convertWithClass(hba, HostInternetScsiHba.class, null, null))
            .collect(Collectors.toList());
        return new HostBusAdapterVo()
            .setHostBlockHbas(hostBlockHbas)
            .setHostFibreChannelHbas(hostFibreChannelHbas)
            .setHostSerialAttachedHbas(hostSerialAttachedHbas)
            .setHostInternetScsiHbas(hostInternetScsiHbas);
    }

    @Override
    public ImportResourceVo getImportResource(String vmwareId, String hostId) throws CustomException {
        ImportResourceVo result = new ImportResourceVo();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference hostRef = new ManagedObjectReference();
        hostRef.setType("HostSystem");
        hostRef.setValue(hostId);
        List<ManagedObjectReference> datastores = new ArrayList<>();
        if (svc.getDynamicProperty(hostRef, "datastore") instanceof List) {
            datastores = (List<ManagedObjectReference>) svc.getDynamicProperty(hostRef, "datastore");
        }

        if (CollectionUtils.isNotEmpty(datastores)) {
            String dataStoreId = datastores.get(0).getValue();
            result.setDataStoreId(dataStoreId);
        }

        ManagedObjectReference computeResource = (ManagedObjectReference) svc.getDynamicProperty(hostRef, "parent");
        ManagedObjectReference resourcePool = new ManagedObjectReference();
        if (svc.getDynamicProperty(computeResource, "resourcePool") instanceof ManagedObjectReference) {
            resourcePool = (ManagedObjectReference) svc.getDynamicProperty(computeResource, "resourcePool");
        }
        String resourcePoolId = resourcePool.getValue();
        result.setResourcePoolId(resourcePoolId);
        return result;
    }

    @Override
    public boolean rescanAllHba(String vmwareId, String hostId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }

        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference hostMO = new ManagedObjectReference();
        hostMO.setType("HostSystem");
        hostMO.setValue(hostId);
        HostConfigManager hostConfigManager = (HostConfigManager) svc.getDynamicProperty(hostMO, "configManager");

        try {
            service.rescanAllHba(hostConfigManager.getStorageSystem());
        } catch (HostConfigFaultFaultMsg | RuntimeFaultFaultMsg e) {
            logger.error("failed to rescan host {} All Hba.", hostId, e);
            throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.RESCAN_ALL_HBA_EXCEPTION_MSG);
        }
        return true;
    }

    @Override
    public OvfVo importOvf(ImportOvfRequest ovfRequest, String vmwareId) throws RuntimeException, CustomException {
        JSONObject verify = getVerify(vmwareId);
        ImportOvf importOvf = new ImportOvf();
        String[] args = new String[] {JSONObject.toJSONString(verify), JSONObject.toJSONString(ovfRequest)};
        return importOvf.run(args);
    }

    @Override
    public OvfVo deployOvf(DeployOvfRequest ovfRequest, String vmwareId) throws RuntimeException, CustomException {
        JSONObject verify = getVerify(vmwareId);
        DeployOvf deployOvf = new DeployOvf();
        String[] args = new String[] {JSONObject.toJSONString(verify), JSONObject.toJSONString(ovfRequest)};
        return deployOvf.run(args);
    }

    @Override
    public String getImportingOvfStatus(String vmwareId, String sessionId) {
        ClsApiClient clsApiClient = LibraryCache.getClient(vmwareId);
        UpdateSessionModel updateSession;
        try {
            updateSession = clsApiClient.updateSession().get(sessionId);
        } catch (Exception e) {
            logger.warn("get import ovf status: ", e);
            clsApiClient.login();
            logger.info("Logged in to Content Library API successfully.");
            updateSession = clsApiClient.updateSession().get(sessionId);
        }
        return updateSession.getState().name();
    }

    private JSONObject getVerify(String vmwareId) throws CustomException {
        JSONObject verify = new JSONObject();
        ExtendedAppUtil utils = extendedAppUtil.getExtendedAppUtil(vmwareId);
        verify.put("ip", utils.getIp());
        verify.put("serviceAuthUser", utils.getUsername());
        verify.put("serviceAuthKey", utils.getPassword());
        verify.put("vmwareId", vmwareId);
        return verify;
    }

    @Override
    public List<EsxInfo> queryEsxInfoList(String vmwareId, HostRequest hostRequest) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        List<EsxInfo> esxInfos = new ArrayList<>();
        List<String> hostIps = hostRequest.getHostNames();
        for (String hostIp : hostIps) {
            Map<String, Object> hostMap = new HashMap<String, Object>();
            ManagedObjectReference hostSystem = svc.getDecendentMoRef(null, "HostSystem", hostIp);

            if (hostSystem == null) {
                logger.error("getVmByHost get host:{} not found!!", hostIp);
                continue;
            }

            List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
                "vm");
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

            ManagedObjectReference hostInfo = (ManagedObjectReference) svc.getDynamicProperty(hostSystem, "parent");

            ComputeResourceSummary computeResourceInfo = (ComputeResourceSummary) svc.getDynamicProperty(hostInfo,
                "summary");
            // 加入维护模式的判断字段
            boolean inMaintenanceMode = false;
            Object runtimeInfo = svc.getDynamicProperty(hostSystem, "runtime");
            if (runtimeInfo instanceof HostRuntimeInfo) {
                HostRuntimeInfo hostRuntimeInfo = (HostRuntimeInfo) runtimeInfo;
                inMaintenanceMode = hostRuntimeInfo.isInMaintenanceMode();
            }
            // 总数量
            int numCpuCores = 0;
            numCpuCores = getComputerResource(computeResourceInfo, numCpuCores);

            int useCpu = 0;

            useCpu = getUseCpu(svc, vmList, useCpu);

            int effectiveCpu = numCpuCores - useCpu;
            int totaleffectiveMemory = 0;
            totaleffectiveMemory = getEffectiveMemory(computeResourceInfo, totaleffectiveMemory);
            // 主机使用Memory
            HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(hostSystem, "summary");
            HostListSummaryQuickStats quickStats = hostSystemInfo.getQuickStats();
            int effectiveMemory = 0;
            if (quickStats.getOverallMemoryUsage() != null) {
                effectiveMemory = (int) Math.floor((totaleffectiveMemory - quickStats.getOverallMemoryUsage()));
            }

            // datastore
            List<ManagedObjectReference> malist = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
                "datastore");
            ManagedObjectReference dataStoreRef = getDataStorRefObj(svc, malist);
            DatastoreSummary datastoreSummary = null;
            if (dataStoreRef != null) {
                datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(dataStoreRef, "summary");
            }

            int totalSpace = 0;
            int freeSpace = 0;
            int usedSpace = 0;

            if (datastoreSummary != null) {
                freeSpace = (int) Math.floor((datastoreSummary.getFreeSpace() / 1024 / 1024));
                totalSpace = (int) Math.floor((datastoreSummary.getCapacity() / 1024 / 1024));
                usedSpace = totalSpace - freeSpace;
            }
            EsxInfo esxInfo = new EsxInfo();
            esxInfo.setTotalCpu(numCpuCores);
            esxInfo.setFreeCpu(effectiveCpu);
            esxInfo.setUseCpu(useCpu);
            esxInfo.setEsxIp(hostIp);
            esxInfo.setTotalMemory(totaleffectiveMemory);
            esxInfo.setFreeMemory(effectiveMemory);
            esxInfo.setUsedMemory(quickStats.getOverallMemoryUsage());

            esxInfo.setTotalDatastore(totalSpace);
            esxInfo.setUsedDatastore(usedSpace);
            esxInfo.setFreeDatastore(freeSpace);
            esxInfo.setHostId(hostSystem.getValue());
            esxInfo.setVmNum(resultList.size());
            esxInfo.setInMaintenanceMode(inMaintenanceMode);

            esxInfos.add(esxInfo);
        }
        return esxInfos;
    }

    @Override
    public String getSerialNumber(String vmwareId, String hostName) throws CustomException {
        String serialNumber = "";
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        if (svc != null && svc.connection != null) {
            ManagedObjectReference hostRef = svc.getDecendentMoRef(null, "HostSystem", hostName);
            HostHardwareInfo hardware = (HostHardwareInfo) svc.getDynamicProperty(hostRef, "hardware");

            HostSystemInfo systemInfo = hardware.getSystemInfo();
            List<HostSystemIdentificationInfo> info = systemInfo.getOtherIdentifyingInfo();
            serialNumber = getSerialNumber(info);
        }
        return serialNumber;
    }

    @Override
    public HostInfo getHostInfo(String vmwareId, String hostId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference hostSystem = new ManagedObjectReference();
        hostSystem.setValue(hostId);
        hostSystem.setType("HostSystem");
        List<ManagedObjectReference> vmList = new ArrayList<>();
        if (svc.getDynamicProperty(hostSystem, "vm") instanceof List) {
            vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem, "vm");
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        HostInfo result = new HostInfo();
        if (vmList == null) {
            return result;
        }
        getResultList(svc, vmList, resultList);

        ManagedObjectReference hostInfo = (ManagedObjectReference) svc.getDynamicProperty(hostSystem, "parent");

        ComputeResourceSummary computeResourceInfo = (ComputeResourceSummary) svc.getDynamicProperty(hostInfo,
            "summary");
        esxInfo(result, svc, hostSystem, vmList, resultList, computeResourceInfo);
        return result;
    }

    @Override
    public HostTo getHostBasicInfo(String vmwareId, String hostId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference hostMO = new ManagedObjectReference();
        hostMO.setType("HostSystem");
        hostMO.setValue(hostId);
        HostListSummary summary = (HostListSummary) svc.getDynamicProperty(hostMO, "summary");
        String hostName = (String) svc.getDynamicProperty(hostMO, "name");
        HostTo ht = new HostTo();
        HostRuntimeInfo hostRuntimeInfo = summary.getRuntime();
        ht
            .setStatus(hostRuntimeInfo.getConnectionState().toString())
            .setName(hostName)
            .setIpAddress(hostName)
            .setMoId(hostMO.getValue())
            .setInMaintenanceMode(hostRuntimeInfo.isInMaintenanceMode());
        return ht;
    }

    private void esxInfo(HostInfo result, ServiceUtil svc, ManagedObjectReference hostSystem,
        List<ManagedObjectReference> vmList, List<Map<String, Object>> resultList,
        ComputeResourceSummary computeResourceInfo) throws CustomException {
        // 总数量
        int numCpuCores = 0;
        numCpuCores = getComputerResource(computeResourceInfo, numCpuCores);

        int useCpu = 0;

        useCpu = getUseCpu(svc, vmList, useCpu);

        int effectiveCpu = numCpuCores - useCpu;
        int TotaleffectiveMemory = 0;
        TotaleffectiveMemory = getEffectiveMemory(computeResourceInfo, TotaleffectiveMemory);
        // 主机使用Memory
        HostListSummary hostSystemInfos = (HostListSummary) svc.getDynamicProperty(hostSystem, "summary");
        HostListSummaryQuickStats quickStats = hostSystemInfos.getQuickStats();
        int effectiveMemory = (int) Math.floor((TotaleffectiveMemory - quickStats.getOverallMemoryUsage()));

        // datastore
        List<ManagedObjectReference> malist = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
            "datastore");
        ManagedObjectReference dataStoreRef = getDataStorRefObj(svc, malist);

        DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(dataStoreRef, "summary");
        int totalSpace = 0;
        int freeSpace = 0;
        int usedSpace = 0;

        if (datastoreSummary != null) {
            freeSpace = (int) Math.floor((datastoreSummary.getFreeSpace() / 1024 / 1024));
            totalSpace = (int) Math.floor((datastoreSummary.getCapacity() / 1024 / 1024));
            usedSpace = totalSpace - freeSpace;
        }
        result.setTotalCpu(numCpuCores);
        result.setFreeCpu(effectiveCpu);
        result.setUseCpu(useCpu);
        result.setTotalMemory(TotaleffectiveMemory);
        result.setFreeMemory(effectiveMemory);
        result.setUsedMemory(quickStats.getOverallMemoryUsage());

        result.setTotalDatastore(totalSpace);
        result.setFreeDatastore(freeSpace);
        result.setUsedDatastore(usedSpace);

        result.setVmNum(resultList.size());
    }

    private void getResultList(ServiceUtil svc, List<ManagedObjectReference> vmList,
        List<Map<String, Object>> resultList) {
        for (ManagedObjectReference vmMof : vmList) {

            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vmMof);
            VirtualMachineConfigInfo config;
            if (vmConfigInfo == null) {
                continue;
            } else {
                config = (VirtualMachineConfigInfo) vmConfigInfo;
            }
            // 过滤掉模板
            if (configNotValid(config)) {
                continue;
            }
            Map<String, Object> vmMap = new HashMap<String, Object>();
            String vmName = (String) svc.getDynamicProperty(vmMof, "name");
            vmMap.put("vmName", vmName);
            String osFullName = config.getGuestFullName();
            vmMap.put("osName", getOSNameByFullName(osFullName));
            vmMap.put("cloudOSName", VMWARE_CLOUD_OS_NAME);
            String uuid = config.getInstanceUuid();
            vmMap.put("id", uuid);
            vmMap.put("vmId", vmMof.getValue());
            resultList.add(vmMap);
        }
    }

    private String getOSNameByFullName(String fullSystemName) {
        String name = null;
        if (fullSystemName != null) {
            name = getNames(fullSystemName);
        }
        return name;
    }

    private String getNames(String fullSystemName) {
        String name;
        if (fullSystemName.toUpperCase().contains(Constants.OS_TYPE_WINDOWS)) {
            name = Constants.OS_TYPE_WINDOWS;
        } else if (fullSystemName.toUpperCase().contains(Constants.OS_TYPE_LINUX)) {
            name = Constants.OS_TYPE_LINUX;
        } else {
            name = Constants.OS_TYPE_OTHER;
        }
        return name;
    }

    private boolean configNotValid(VirtualMachineConfigInfo config) {
        return config == null || config.isTemplate();
    }

    private String getSerialNumber(List<HostSystemIdentificationInfo> info) {
        String serialNumber = "";
        String identifierValue = "To be filled by O.E.M.";
        for (HostSystemIdentificationInfo hostsyStemInfo : info) {
            ElementDescription elem = hostsyStemInfo.getIdentifierType();
            String key = elem.getKey();
            if ("ServiceTag".equalsIgnoreCase(key)) {
                serialNumber = hostsyStemInfo.getIdentifierValue();
                if (!serialNumber.equalsIgnoreCase(identifierValue)) {
                    break;
                } else {
                    serialNumber = "";
                }
            }
        }
        return serialNumber;
    }

    private ManagedObjectReference getDataStorRefObj(ServiceUtil svc, List<ManagedObjectReference> malist)
        throws CustomException {
        ManagedObjectReference dataStoreRef = null;
        if (malist != null && malist.size() > 0) {
            ManagedObjectReference[] dataStoreArr = malist.toArray(new ManagedObjectReference[0]);
            dataStoreRef = getMaxSizeDataStore(dataStoreArr, svc);
            if (dataStoreRef == null) {
                throw new CustomException(ErrorCode.FAILED_CODE, "The dataStoreRef is not found");
            }
        }
        return dataStoreRef;
    }

    /**
     * 获取最大的dataStore
     */
    private ManagedObjectReference getMaxSizeDataStore(ManagedObjectReference[] dataStoreArr, ServiceUtil svc) {
        ManagedObjectReference dataStoreRef = null;
        if (dataStoreArr != null && dataStoreArr.length > 0) {
            for (int i = 0; i < dataStoreArr.length; i++) {
                ManagedObjectReference currentDS = dataStoreArr[i];
                if (i == 0) {
                    dataStoreRef = currentDS;
                    continue;
                }
                DatastoreSummary currentDSInfo = (DatastoreSummary) svc.getDynamicProperty(currentDS, "summary");
                DatastoreSummary lastDSInfo = (DatastoreSummary) svc.getDynamicProperty(dataStoreRef, "summary");
                if (currentDSInfo.getFreeSpace() > lastDSInfo.getFreeSpace()) {
                    dataStoreRef = currentDS;
                }
            }
        }
        return dataStoreRef;
    }

    private int getEffectiveMemory(ComputeResourceSummary computeResourceInfo, int effectiveMemory) {
        if (computeResourceInfo != null) {
            effectiveMemory = (int) Math.floor(computeResourceInfo.getTotalMemory() / 1024 / 1024);
        }
        return effectiveMemory;
    }

    private int getUseCpu(ServiceUtil svc, List<ManagedObjectReference> vmList, int useCpu) {
        for (ManagedObjectReference vmMof : vmList) {
            ManagedEntityStatus guestHeartbeatStatus = null;
            if (svc.getDynamicProperty(vmMof, "guestHeartbeatStatus") instanceof ManagedEntityStatus) {
                guestHeartbeatStatus = (ManagedEntityStatus) svc.getDynamicProperty(vmMof, "guestHeartbeatStatus");
            }

            if (guestHeartbeatStatus == null || !"GREEN".equals(ManagedEntityStatus.GREEN.toString())) {
                continue;
            }
            VirtualMachineSummary summary = (VirtualMachineSummary) svc.getDynamicProperty(vmMof, "summary");
            if (summary == null) {
                continue;
            }
            VirtualMachineConfigSummary summaryConfig = summary.getConfig();
            if (!summaryConfig.isTemplate()) {
                useCpu += summaryConfig.getNumCpu() != null ? summaryConfig.getNumCpu() : 0;
            }
        }
        return useCpu;
    }

    private int getComputerResource(ComputeResourceSummary computeResourceInfo, int numCpuCores) {
        if (computeResourceInfo != null) {
            numCpuCores = computeResourceInfo.getNumCpuCores() * 2;
        }
        return numCpuCores;
    }
}
