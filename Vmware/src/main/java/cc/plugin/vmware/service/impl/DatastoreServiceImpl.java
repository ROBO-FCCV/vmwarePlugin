/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.datastore.DatastoreInfo;
import cc.plugin.vmware.model.vo.response.datastore.Datastore;
import cc.plugin.vmware.model.vo.response.datastore.DatastoreResponse;
import cc.plugin.vmware.model.vo.response.datastore.DatastoreVo;
import cc.plugin.vmware.service.DatastoreService;

import com.alibaba.fastjson.JSON;
import com.github.dozermapper.core.Mapper;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.HostConfigFaultFaultMsg;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NotFoundFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VimFaultFaultMsg;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VmfsDatastoreCreateSpec;
import com.vmware.vim25.VmfsDatastoreOption;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
@Service
public class DatastoreServiceImpl implements DatastoreService {
    private static final Logger logger = LoggerFactory.getLogger(DatastoreServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    /**
     * The Dozer mapper.
     */
    @Autowired
    Mapper dozerMapper;

    @Override
    public String deleteDatastore(String vmwareId, String datastoreId) throws CustomException {
        String result = "";
        ManagedObjectReference datastoreEntry = new ManagedObjectReference();
        datastoreEntry.setType("Datastore");
        datastoreEntry.setValue(datastoreId);

        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ServiceUtil svc = ecb.getServiceUtil();

        try {
            ManagedObjectReference taskMor = service.destroyTask(datastoreEntry);
            TaskInfo task;
            do {
                TimeUnit.SECONDS.sleep(1);
                task = (TaskInfo) svc.getDynamicProperty(taskMor, "info");
                logger.info("destroying datastore.");
            } while (task.getState() == TaskInfoState.QUEUED || task.getState() == TaskInfoState.RUNNING);
            result = task.getState().value();
            if (StringUtils.equals(result, "error")) {
                throw new CustomException(ErrorCode.FAILED_CODE, result);
            }
        } catch (RuntimeFaultFaultMsg | VimFaultFaultMsg | InterruptedException e) {
            logger.error("VMCloneFromTemplate25.destroyDatastore FaultFaultMsg!", e);
            throw new CustomException(ErrorCode.FAILED_CODE, TaskInfoState.ERROR.value());
        }
        return result;
    }

    @Override
    public List<DatastoreVo> getVmwareSharedStorage(String vmwareId) throws CustomException {
        List<DatastoreVo> result = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceUtil svc;
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        if (content != null) {
            ManagedObjectReference rootFolder = content.getRootFolder();
            @SuppressWarnings("unchecked")
            List<ManagedObjectReference> datastoreList = svc.getDecendentMoRefs(rootFolder, "Datastore");
            for (ManagedObjectReference datastoreRef : datastoreList) {
                DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(datastoreRef, "summary");
                logger.info("DatastoreSummary name is {}", datastoreSummary.getName());
                if (datastoreSummary.isAccessible() && datastoreSummary.isMultipleHostAccess()) {
                    result.add(dozerMapper.map(datastoreSummary, DatastoreVo.class));
                }
            }
        }
        return result;
    }

    @Override
    public String createDatastore(String vmwareId, DatastoreInfo datastoreInfo) throws CustomException {
        String datacenterName = datastoreInfo.getDatacenterName();
        String hostName = datastoreInfo.getHostName();
        String datastoreName = datastoreInfo.getDatastoreName();
        String key = datastoreInfo.getKey();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        VimPortType service = serviceConnection.getVimPort();
        svc = ecb.getServiceUtil();
        return datastore(datacenterName, hostName, datastoreName, key, svc, content, service);
    }

    @Override
    public List<DatastoreResponse> getDatastoresByDatacenterOrHostName(String vmwareId, String datacenterName,
        String hostName) throws CustomException {
        List<DatastoreResponse> datastores = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        VimPortType service = serviceConnection.getVimPort();
        svc = ecb.getServiceUtil();
        return getDatastore(datacenterName, hostName, datastores, svc, content, service);
    }

    @Override
    public List<Datastore> getDatastoreByFilterType(String vmwareId, String hostId, String filterType)
        throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        List<Datastore> result = new ArrayList<>();
        if (StringUtils.equals(filterType, "0")) {
            ManagedObjectReference clusterSystem = new ManagedObjectReference();
            clusterSystem.setType("ClusterComputeResource");
            clusterSystem.setValue(hostId);
            List<ManagedObjectReference> clusterDatastores = (List<ManagedObjectReference>) svc.getDynamicProperty(
                clusterSystem, "datastore");
            setDatastores(svc, result, clusterDatastores);
        } else {
            ManagedObjectReference hostSystem = new ManagedObjectReference();
            hostSystem.setType("HostSystem");
            hostSystem.setValue(hostId);
            List<ManagedObjectReference> hostDatastores = (List<ManagedObjectReference>) svc.getDynamicProperty(
                hostSystem, "datastore");
            setDatastores(svc, result, hostDatastores);
        }
        return result;
    }

    private void setDatastores(ServiceUtil svc, List<Datastore> result, List<ManagedObjectReference> datastores) {
        for (ManagedObjectReference datastoreMo : datastores) {
            Datastore datastore = new Datastore();
            String datastoreName = (String) svc.getDynamicProperty(datastoreMo, "name");
            String modId = datastoreMo.getValue();
            datastore.setModId(modId);
            DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(datastoreMo, "summary");
            double capacitySize = (double) datastoreSummary.getCapacity() / 1024 / 1024 / 1024;
            double freeSize = (double) datastoreSummary.getFreeSpace() / 1024 / 1024 / 1024;
            datastore.setCapacityGB(capacitySize);
            datastore.setFreeSizeGB(freeSize);
            datastore.setUsedSizeGB(capacitySize - freeSize);
            datastore.setName(datastoreName);
            result.add(datastore);
        }
    }

    private List<DatastoreResponse> getDatastore(String datacenterName, String hostName,
        List<DatastoreResponse> datastores, ServiceUtil svc, ServiceContent content, VimPortType service)
        throws CustomException {
        try {
            ManagedObjectReference datacenterRef = service.findByInventoryPath(content.getSearchIndex(),
                datacenterName);
            if (svc.connection == null) {
                throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
            }
            ManagedObjectReference hostSystem = svc.getDecendentMoRef(datacenterRef, "HostSystem", hostName);
            List<ManagedObjectReference> dataStorelist = (List<ManagedObjectReference>) svc.getDynamicProperty(
                hostSystem, "datastore");
            for (ManagedObjectReference ManagedObjectReference : dataStorelist) {
                String dataStoreName = (String) svc.getDynamicProperty(ManagedObjectReference, "name");
                ManagedEntityStatus overallStatus = (ManagedEntityStatus) svc.getDynamicProperty(ManagedObjectReference,
                    "overallStatus");
                DatastoreSummary datastoreCapability = (DatastoreSummary) svc.getDynamicProperty(ManagedObjectReference,
                    "summary");
                long dataStoreSum = datastoreCapability.getCapacity();
                long freeSpace = datastoreCapability.getFreeSpace();
                DatastoreResponse datastoreResponse = new DatastoreResponse()
                    .setDatastoreId(ManagedObjectReference.getValue())
                    .setName(dataStoreName)
                    .setStatus(overallStatus.toString())
                    .setTotalSize(dataStoreSum)
                    .setFreeSize(freeSpace);
                datastores.add(datastoreResponse);
            }
        } catch (RuntimeFaultFaultMsg e) {
            logger.error("get datacenter error...", e);
        }
        return datastores;
    }

    private String datastore(String datacenterName, String hostName, String datastoreName, String key, ServiceUtil svc,
        ServiceContent content, VimPortType service) throws CustomException {
        try {
            ManagedObjectReference datacenterRef = service.findByInventoryPath(content.getSearchIndex(),
                datacenterName);
            if (svc.connection == null) {
                throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
            }
            ManagedObjectReference hostSystem = svc.getDecendentMoRef(datacenterRef, "HostSystem", hostName);

            HostConfigManager hostConfigManager = (HostConfigManager) svc.getDynamicProperty(hostSystem,
                "configManager");
            ManagedObjectReference hostDatastoreSystem = hostConfigManager.getDatastoreSystem();
            List<HostScsiDisk> scDisk = service.queryAvailableDisksForVmfs(hostDatastoreSystem, null);

            HostScsiDisk hostScsiDisk = null;
            for (HostScsiDisk hostScsiDiskObj : scDisk) {
                if (hostScsiDiskObj.getKey().equals(key)) {
                    hostScsiDisk = hostScsiDiskObj;
                }
            }
            if (hostScsiDisk == null) {
                throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.CREATE_DATASTORE_EXCEPTION_MSG);
            }
            // 创建datastore
            VmfsDatastoreCreateSpec vmfsSpec;
            List<VmfsDatastoreOption> dsOptions = service.queryVmfsDatastoreCreateOptions(hostDatastoreSystem,
                hostScsiDisk.getDevicePath(), null);
            // 格式化创建datastore的信息
            vmfsSpec = (VmfsDatastoreCreateSpec) dsOptions.get(0).getSpec();

            // 设置datastore名称，42位上限任意字符，前台校验
            vmfsSpec.getVmfs().setVolumeName(datastoreName);
            // 设置datastore的VMFS版本，分为3和5
            vmfsSpec.getVmfs().setMajorVersion(Integer.valueOf(5));
            // 创建datastore
            ManagedObjectReference result = service.createVmfsDatastore(hostDatastoreSystem, vmfsSpec);
            return JSON.toJSONString(result);
        } catch (RuntimeFaultFaultMsg | HostConfigFaultFaultMsg | NotFoundFaultMsg | DuplicateNameFaultMsg e) {
            logger.error("VMCloneFromTemplate25.destroyDatastore createDatastore error!", e);
            throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.CREATE_DATASTORE_EXCEPTION_MSG);
        }
    }
}
