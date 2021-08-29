/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.consts.VMConstants;
import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.factory.TraversalSpecBuilder;
import com.vmware.sample.model.network.Network;
import com.vmware.sample.model.vm.CdromInfo;
import com.vmware.sample.model.vm.DiskInfo;
import com.vmware.sample.model.vm.DiskToInfo;
import com.vmware.sample.model.vm.NetToInfo;
import com.vmware.sample.model.vm.NetworkBasic;
import com.vmware.sample.model.vm.NetworkInfo;
import com.vmware.sample.model.vm.RDMInfo;
import com.vmware.sample.model.vm.SnapShotInfo;
import com.vmware.sample.model.vm.VirtualMachineBasic;
import com.vmware.sample.model.vm.VirtualMachineInfo;
import com.vmware.sample.model.vm.VmConfigurationBasicInfo;
import com.vmware.sample.model.vm.VmConfigurationInfo;
import com.vmware.sample.model.vm.VmTemplateInfo;
import com.vmware.sample.model.vm.VmVNCInfo;
import com.vmware.sample.model.vm.VmVNCStatusInfo;
import com.vmware.sample.service.VMService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.AlreadyExistsFaultMsg;
import com.vmware.vim25.CustomizationAdapterMapping;
import com.vmware.vim25.CustomizationFaultFaultMsg;
import com.vmware.vim25.CustomizationFixedIp;
import com.vmware.vim25.CustomizationFixedName;
import com.vmware.vim25.CustomizationGlobalIPSettings;
import com.vmware.vim25.CustomizationGuiUnattended;
import com.vmware.vim25.CustomizationIPSettings;
import com.vmware.vim25.CustomizationIdentification;
import com.vmware.vim25.CustomizationLicenseDataMode;
import com.vmware.vim25.CustomizationLicenseFilePrintData;
import com.vmware.vim25.CustomizationLinuxPrep;
import com.vmware.vim25.CustomizationPassword;
import com.vmware.vim25.CustomizationSpec;
import com.vmware.vim25.CustomizationSysprep;
import com.vmware.vim25.CustomizationUserData;
import com.vmware.vim25.Description;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.GuestOsDescriptor;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.MigrationFaultFaultMsg;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.OutOfBoundsFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SnapshotFaultFaultMsg;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.ToolsConfigInfo;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimFaultFaultMsg;
import com.vmware.vim25.VirtualCdrom;
import com.vmware.vim25.VirtualCdromIsoBackingInfo;
import com.vmware.vim25.VirtualCdromRemotePassthroughBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskCompatibilityMode;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualDiskMode;
import com.vmware.vim25.VirtualDiskRawDiskMappingVer1BackingInfo;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualHardwareOption;
import com.vmware.vim25.VirtualLsiLogicController;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigOption;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineTicket;
import com.vmware.vim25.VirtualSCSISharing;
import com.vmware.vim25.VirtualVmxnet3;
import com.vmware.vim25.VmConfigFaultFaultMsg;
import com.vmware.vim25.VmToolsUpgradeFaultFaultMsg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Virtual machine SDK Service
 *
 * @since 2020-09-14
 */
@Slf4j
@Service("vm-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VMSDKServiceImpl implements VMService {
    // 网卡初始标识key
    private static final int INIT_DEVICE_KEY = 4000;
    private final ObjectMapper objectMapper;
    private final VmwareSDKClient vmwareSDKClient;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public List<VirtualMachineInfo> getVms(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<String> retrieveObjects = new ArrayList<>();
        retrieveObjects.add(VMConstants.VIRTUAL_MACHINE);
        List<String> retrieveProperties = Arrays.asList(VMConstants.NAME, VMConstants.GUEST, VMConstants.OVERALL_STATE,
            VMConstants.SUMMARY, VMConstants.CONFIG, VMConstants.RUNTIME, VMConstants.RESOURCE_POOL);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance, retrieveObjects,
            retrieveProperties);
        if (CollectionUtils.isNotEmpty(objectContents)) {
            return getVirtualMachineInfos(sdkInstance, objectContents);
        }
        return Collections.emptyList();
    }

    /**
     * Get VirtualMachineInfo list by ObjectContent obj
     *
     * @param sdkInstance vmware instance
     * @param objs ObjectContent list
     * @return VirtualMachineInfo
     */
    private List<VirtualMachineInfo> getVirtualMachineInfos(VMwareSDK sdkInstance, List<ObjectContent> objs) {
        List<VirtualMachineInfo> vLists = new ArrayList<>();
        for (ObjectContent obj : objs) {
            Map<String, Object> collect = obj.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode jsonNode;
            try {
                if (!collect.containsKey(VMConstants.CONFIG)) {
                    continue;
                }
                jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
                VirtualMachineInfo vInfo = getVirtualMachineInfo(sdkInstance, obj, collect, jsonNode);
                vLists.add(vInfo);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return vLists;
    }

    private VirtualMachineInfo getVirtualMachineInfo(VMwareSDK sdkInstance, ObjectContent obj,
        Map<String, Object> collect, JsonNode jsonNode) {
        VirtualMachineInfo vInfo = new VirtualMachineInfo();
        ManagedObjectReference objectReference = obj.getObj();
        vInfo.setModId(objectReference.getValue());
        vInfo.setVmId(objectReference.getValue());
        vInfo.setTemplate(
            jsonNode.path(VMConstants.SUMMARY).path(VMConstants.CONFIG).path(VMConstants.TEMPLATE).asBoolean());
        if (collect.get(VMConstants.CONFIG) instanceof VirtualMachineConfigInfo) {
            VirtualMachineConfigInfo virtualMachineConfigInfo = (VirtualMachineConfigInfo) collect.get(
                VMConstants.CONFIG);
            vInfo.setVnc(isUseVnc(virtualMachineConfigInfo));
            setDiskAndNetworks(vInfo, virtualMachineConfigInfo);
        }
        vInfo.setVmName(jsonNode.path(VMConstants.NAME).asText());
        vInfo.setStatus(jsonNode.path(VMConstants.OVERALL_STATE).asText());
        vInfo.setPowerStatus(jsonNode.path(VMConstants.RUNTIME).path(VMConstants.POWER_STATE).asText());
        vInfo.setHostId(jsonNode.path(VMConstants.RUNTIME).path(VMConstants.HOST).path(VMConstants.VALUE).asText());
        vInfo.setHostName(getObjName(sdkInstance,
            jsonNode.path(VMConstants.RUNTIME).path(VMConstants.HOST).path(VMConstants.VALUE).asText(),
            VMConstants.HOST_SYSTEM));
        vInfo.setIpAddress(jsonNode.path(VMConstants.GUEST).path(VMConstants.IPADDRESS).textValue());
        Iterator<JsonNode> elements = jsonNode.path(VMConstants.GUEST).path(VMConstants.NET).elements();
        vInfo.setNets(getVirtualDeviceNets(elements));
        vInfo.setMemorySize(DataSize.ofMegabytes(
            jsonNode.path(VMConstants.SUMMARY).path(VMConstants.CONFIG).path(VMConstants.MEMORY_SIZE_MB).asLong())
            .toBytes());
        vInfo.setCpuCount(
            jsonNode.path(VMConstants.SUMMARY).path(VMConstants.CONFIG).path(VMConstants.NUM_CPU).asLong());
        vInfo.setNumCoresPerSocket(
            jsonNode.path(VMConstants.CONFIG).path(VMwareConstants.HARDWARE).path("numCoresPerSocket").asLong());
        vInfo.setOsName(getOSNameByFullName(jsonNode.path(VMConstants.GUEST).path("guestFullName").asText()));
        vInfo.setOsFullName(jsonNode.path(VMConstants.GUEST).path("guestFullName").asText());
        vInfo.setToolsRunningStatus(jsonNode.path(VMConstants.GUEST).path("guestState").asText());
        vInfo.setGuestStatus(jsonNode.path(VMConstants.GUEST).path("toolsRunningStatus").asText());
        vInfo.setToolsStatus(jsonNode.path(VMConstants.GUEST).path("toolsStatus").asText());
        if (!collect.containsKey(VMConstants.RESOURCE_POOL)) {
            return vInfo;
        }
        if (collect.get(VMConstants.RESOURCE_POOL) instanceof ManagedObjectReference) {
            ManagedObjectReference resource = (ManagedObjectReference) collect.get(VMConstants.RESOURCE_POOL);
            getCluster(sdkInstance, resource, vInfo);
        }

        return vInfo;
    }

    private void getCluster(VMwareSDK sdkInstance, ManagedObjectReference resource, VirtualMachineInfo vInfo) {
        List<String> properties = new ArrayList<>();
        properties.add("parent");
        List<ObjectContent> contents = vmwareSDKClient.retrieveProperties(sdkInstance, resource, properties);
        if ((contents.get(0).getPropSet().get(0).getVal()) instanceof ManagedObjectReference) {
            ManagedObjectReference mor = (ManagedObjectReference) contents.get(0).getPropSet().get(0).getVal();
            vInfo.setClusterId(mor.getValue());
            if (StringUtils.contains(mor.getValue(), "domain-s")) {
                return;
            } else {
                String clusterName = getObjName(sdkInstance, mor.getValue(), VMConstants.CLUSTER_COMPUTE_RESOURCE);
                vInfo.setClusterName(clusterName);
            }
        }
    }

    private void setDiskAndNetworks(VirtualMachineInfo vInfo, VirtualMachineConfigInfo virtualMachineConfigInfo) {
        List<DiskToInfo> diskList = new ArrayList<>();
        List<NetworkBasic> networkBasics = new ArrayList<>();
        VirtualHardware virtualHardware = virtualMachineConfigInfo.getHardware();
        List<VirtualDevice> virtualDeviceList = virtualHardware.getDevice();
        for (VirtualDevice vd : virtualDeviceList) {
            if (vd instanceof VirtualDisk) {
                DiskToInfo disk = new DiskToInfo();
                Description description = vd.getDeviceInfo();
                String diskName = description.getLabel();
                disk.setDiskName(diskName);
                Long diskSize = ((VirtualDisk) vd).getCapacityInBytes();
                disk.setDiskSize(diskSize);
                setDiskMap(vd, disk);
                diskList.add(disk);
            }
            if (vd instanceof VirtualEthernetCard) {
                NetworkBasic networkBasic = new NetworkBasic();
                VirtualEthernetCard virtualEthernetCard = (VirtualEthernetCard) vd;
                networkBasic.setName(virtualEthernetCard.getDeviceInfo().getSummary());
                networkBasic.setType(virtualEthernetCard.getClass().getSimpleName());
                networkBasic.setConnected(virtualEthernetCard.getConnectable().isConnected());
                networkBasics.add(networkBasic);
            }
        }
        vInfo.setDisks(diskList);
        vInfo.setNetworks(networkBasics);
    }

    /**
     * Is vnc available
     *
     * @param virtualMachineConfigInfo virtual Machine Config info
     * @return true or false
     */
    private boolean isUseVnc(VirtualMachineConfigInfo virtualMachineConfigInfo) {
        for (OptionValue option : virtualMachineConfigInfo.getExtraConfig()) {
            if (VMConstants.VNC_ENABLED.equals(option.getKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get name by id and vmwareSdk
     *
     * @param vMwareSDK vmware instance
     * @param id id
     * @param type object type
     * @return name
     */
    private String getObjName(VMwareSDK vMwareSDK, String id, String type) {
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(type)
            .value(id)
            .build();
        List<String> properties = new ArrayList<>();
        properties.add(VMConstants.NAME);
        List<ObjectContent> contents = vmwareSDKClient.retrieveProperties(vMwareSDK, managedObjectReference,
            properties);
        Object object = contents.get(0).getPropSet().get(0).getVal();
        String name = "";
        if (object instanceof String) {
            name = (String) object;
        }
        return name;
    }

    private String[] getDataStore(VMwareSDK vMwareSDK, String id, String type) {
        String[] dataStore = new String[2];
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(type)
            .value(id)
            .build();
        List<String> properties = Arrays.asList(VMConstants.NAME, VMConstants.SUMMARY);
        List<ObjectContent> contents = vmwareSDKClient.retrieveProperties(vMwareSDK, managedObjectReference,
            properties);
        for (ObjectContent obj : contents) {
            Map<String, Object> collect = obj.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode jsonNode;

            try {
                jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
                dataStore[0] = jsonNode.path(VMConstants.NAME).asText();
                dataStore[1] = jsonNode.path(VMwareConstants.SUMMARY).path("freeSpace").asText();
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }

        return dataStore;
    }

    /**
     * Get net list
     *
     * @param jsonNodes net jsonNode
     * @return net list
     */
    private List<NetToInfo> getVirtualDeviceNets(Iterator<JsonNode> jsonNodes) {
        List<NetToInfo> netList = new ArrayList<>();
        while (jsonNodes.hasNext()) {
            JsonNode guestNicInfo = jsonNodes.next();
            NetToInfo net = new NetToInfo();
            Iterator<JsonNode> ipNode = guestNicInfo.path(VMConstants.IPADDRESS).elements();
            List<String> ipList = new ArrayList<>();
            while (ipNode.hasNext()) {
                JsonNode ip = ipNode.next();
                ipList.add(ip.asText());
            }
            net.setName(guestNicInfo.path(VMConstants.NETWORK).asText());
            net.setIp(ipList);
            netList.add(net);
        }
        return netList;
    }

    private void setDiskMap(VirtualDevice vd, DiskToInfo disk) {
        if (vd.getBacking() instanceof VirtualDiskFlatVer2BackingInfo) {
            VirtualDiskFlatVer2BackingInfo back = (VirtualDiskFlatVer2BackingInfo) vd.getBacking();
            disk.setThinProvisioned(back.isThinProvisioned());
        } else {
            disk.setThinProvisioned(false);
        }
    }

    /**
     * Standardized OS name
     *
     * @param fullSystemName system fullName
     * @return OS name
     */
    private String getOSNameByFullName(String fullSystemName) {
        String name = null;
        if (!StringUtils.isEmpty(fullSystemName)) {
            if (fullSystemName.toUpperCase(Locale.ROOT).contains(VMConstants.OS_TYPE_WINDOWS)) {
                name = VMConstants.OS_TYPE_WINDOWS;
            } else if (fullSystemName.toUpperCase(Locale.ROOT).contains(VMConstants.OS_TYPE_LINUX)) {
                name = VMConstants.OS_TYPE_LINUX;
            } else {
                name = VMConstants.OS_TYPE_OTHER;
            }
        }
        return name;
    }

    @Override
    public List<VirtualMachineBasic> queryVmsByHost(String vmwareId, String hostId) {
        TraversalSpec traversalSpec = TraversalSpecBuilder.getInstance().build();
        traversalSpec.setSkip(false);
        traversalSpec.setPath("vm");
        ManagedObjectReference host = ManagedObjectReferenceBuilder.getInstance()
            .type(VMConstants.HOST_SYSTEM)
            .value(hostId)
            .build();
        traversalSpec.setType((host.getType()));
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<String> properties = new ArrayList<>();
        properties.add(VMConstants.SUMMARY);
        properties.add(VMConstants.NAME);
        properties.add(VMConstants.OVERALL_STATE);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            host, Collections.singletonList(traversalSpec), Collections.singletonList(VMConstants.VIRTUAL_MACHINE),
            properties);
        List<VirtualMachineBasic> virtualMachineBasics = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(objectContents)) {
            handleResult(virtualMachineBasics, objectContents);
        }
        return virtualMachineBasics;
    }

    private void handleResult(List<VirtualMachineBasic> virtualMachineBasics, List<ObjectContent> objects) {
        for (ObjectContent object : objects) {
            Map<String, Object> collect = object.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            try {
                JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));

                VirtualMachineBasic virtualMachineBasic = new VirtualMachineBasic();
                virtualMachineBasic.setVmId(object.getObj().getValue());
                virtualMachineBasics.add(virtualMachineBasic);
                virtualMachineBasic.setCpuCount(
                    jsonNode.path(VMConstants.SUMMARY).path(VMConstants.CONFIG).path(VMConstants.NUM_CPU).asLong());
                virtualMachineBasic.setIpAddress(
                    jsonNode.path(VMConstants.SUMMARY).path(VMConstants.GUEST).path(VMConstants.IPADDRESS).textValue());
                virtualMachineBasic.setMemorySize(jsonNode.path(VMConstants.SUMMARY)
                    .path(VMwareConstants.CONFIG)
                    .path(VMConstants.MEMORY_SIZE_MB)
                    .asLong());
                virtualMachineBasic.setPowerStatus(jsonNode.path(VMConstants.SUMMARY)
                    .path(VMwareConstants.RUN_TIME)
                    .path(VMConstants.POWER_STATE)
                    .asText());
                virtualMachineBasic.setVmName(jsonNode.path(VMConstants.NAME).asText());
                virtualMachineBasic.setStatus(jsonNode.path(VMConstants.OVERALL_STATE).asText());
                virtualMachineBasics.add(virtualMachineBasic);
            } catch (JsonProcessingException e) {
                log.error(VMwareConstants.JSON_PROCESSING_EXCEPTION, SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
    }

    @Override
    public List<VirtualMachineInfo> getVmsByHost(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference host = ManagedObjectReferenceBuilder.getInstance()
            .type(VMConstants.HOST_SYSTEM)
            .value(hostId)
            .build();
        List<String> retrieveProperties = Arrays.asList(VMConstants.NAME, VMConstants.GUEST, VMConstants.OVERALL_STATE,
            VMConstants.SUMMARY, VMConstants.CONFIG, VMConstants.RUNTIME, VMConstants.RESOURCE_POOL);
        TraversalSpec traversalSpec = TraversalSpecBuilder.getInstance().build();
        traversalSpec.setSkip(false);
        traversalSpec.setPath("vm");
        traversalSpec.setType((host.getType()));
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            host, Collections.singletonList(traversalSpec), Collections.singletonList(VMConstants.VIRTUAL_MACHINE),
            retrieveProperties);
        if (CollectionUtils.isNotEmpty(objectContents)) {
            return getVirtualMachineInfos(sdkInstance, objectContents);
        }
        return Collections.emptyList();
    }

    @Override
    public String createVmSnapshot(String vmwareId, SnapShotInfo snapshot) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(VMConstants.VIRTUAL_MACHINE);
        vmRef.setValue(snapshot.getVmId());
        try {
            ManagedObjectReference taskMor = sdkInstance.getVimPort()
                .createSnapshotTask(vmRef, snapshot.getName(), snapshot.getDescription(), false, false);
            return taskMor.getValue();
        } catch (FileFaultFaultMsg e) {
            log.error("FileFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidNameFaultMsg e) {
            log.error("InvalidNameFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (SnapshotFaultFaultMsg e) {
            log.error("SnapshotFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (TaskInProgressFaultMsg e) {
            log.error("TaskInProgressFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VmConfigFaultFaultMsg e) {
            log.error("VmConfigFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public String powerStopByVmId(String vmwareId, String vmId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(VMConstants.VIRTUAL_MACHINE);
        vmRef.setValue(vmId);
        try {
            ManagedObjectReference taskMor = sdkInstance.getVimPort().powerOffVMTask(vmRef);
            return taskMor.getValue();
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (TaskInProgressFaultMsg e) {
            log.error("TaskInProgressFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.POWER_OFF_VM_ERROR);
    }

    @Override
    public String powerStartByVmId(String vmwareId, String vmId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(VMConstants.VIRTUAL_MACHINE);
        vmRef.setValue(vmId);
        try {
            ManagedObjectReference taskMor = sdkInstance.getVimPort().powerOnVMTask(vmRef, null);
            return taskMor.getValue();
        } catch (FileFaultFaultMsg e) {
            log.error("FileFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InsufficientResourcesFaultFaultMsg e) {
            log.error("InsufficientResourcesFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (TaskInProgressFaultMsg e) {
            log.error("TaskInProgressFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VmConfigFaultFaultMsg e) {
            log.error("VmConfigFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.POWER_ON_VM_ERROR);
    }

    @Override
    public List<GuestOsDescriptor> getGuestSystems(String vmwareId, String clusterId, String hostId) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        String clusterIdNew = clusterId;
        if (StringUtils.isBlank(clusterIdNew)) {
            List<ObjectContent> retrieveProperties = vmwareSDKClient.retrieveProperties(vMwareSDK,
                ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
                Collections.singletonList("parent"));
            ManagedObjectReference cluster = (ManagedObjectReference) (retrieveProperties.get(0)
                .getPropSet()
                .get(0)
                .getVal());
            clusterIdNew = cluster.getValue();
        }
        ManagedObjectReference environmentBrowser = getResourcePool(vMwareSDK, clusterIdNew, "environmentBrowser");
        ManagedObjectReference hostMor = StringUtils.isNotBlank(hostId) ? ManagedObjectReferenceBuilder.getInstance()
            .type(VMConstants.HOST_SYSTEM)
            .value(hostId)
            .build() : null;
        try {
            VirtualMachineConfigOption virtualMachineConfigOption = vMwareSDK.getVimPort()
                .queryConfigOption(environmentBrowser, null, hostMor);
            return virtualMachineConfigOption.getGuestOSDescriptor();
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public String mountVmwareTools(String vmwareId, String vmId) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(VMConstants.VIRTUAL_MACHINE);
        vmRef.setValue(vmId);
        try {
            vMwareSDK.getVimPort().mountToolsInstaller(vmRef);
            return RestCodeEnum.SUCCESS.getMsg();
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VmConfigFaultFaultMsg e) {
            log.error("VmConfigFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VmToolsUpgradeFaultFaultMsg e) {
            log.error("VmToolsUpgradeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public String markVmTemplate(String vmwareId, String vmId) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(VMConstants.VIRTUAL_MACHINE);
        vmRef.setValue(vmId);
        try {
            vMwareSDK.getVimPort().markAsTemplate(vmRef);
            return RestCodeEnum.SUCCESS.getMsg();
        } catch (FileFaultFaultMsg e) {
            log.error("FileFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VmConfigFaultFaultMsg e) {
            log.error("VmConfigFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public String getVmwareToolsStatus(String vmwareId, String vmId) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmMor = new ManagedObjectReference();
        vmMor.setType(VMConstants.VIRTUAL_MACHINE);
        vmMor.setValue(vmId);
        List<String> properties = new ArrayList<>();
        properties.add(VMConstants.GUEST);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(vMwareSDK, vmMor, properties);
        if (objectContents != null) {
            Map<String, Object> map = objectContents.get(0)
                .getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            try {
                JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(map));
                return jsonNode.path(VMConstants.GUEST).path(VMConstants.VMWARE_TOOLS_STATUS).asText();
            } catch (JsonProcessingException e) {
                log.error(VMwareConstants.JSON_PROCESSING_EXCEPTION, SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public String powerResetByVmId(String vmwareId, String vmId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmMor = new ManagedObjectReference();
        vmMor.setType(VMConstants.VIRTUAL_MACHINE);
        vmMor.setValue(vmId);
        try {
            ManagedObjectReference taskMor = sdkInstance.getVimPort().resetVMTask(vmMor);
            return taskMor.getValue();
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (TaskInProgressFaultMsg e) {
            log.error("TaskInProgressFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.RESET_VM_ERROR);
    }

    @Override
    public String deleteVmByVmId(String vmwareId, String vmId) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmMor = new ManagedObjectReference();
        vmMor.setValue(vmId);
        vmMor.setType(VMConstants.VIRTUAL_MACHINE);
        List<String> properties = Collections.singletonList(VMConstants.RUNTIME);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(vMwareSDK, vmMor, properties);
        if (objectContents.get(0).getPropSet().get(0).getVal() instanceof VirtualMachineRuntimeInfo) {
            VirtualMachineRuntimeInfo runtimeInfo = (VirtualMachineRuntimeInfo) objectContents.get(0)
                .getPropSet()
                .get(0)
                .getVal();
            if (runtimeInfo != null) {
                String powerState = runtimeInfo.getPowerState().value();
                if ("poweredOn".equals(powerState)) {
                    powerStopByVmId(vmwareId, vmId);
                }
            }
        }
        try {
            ManagedObjectReference taskMor = vMwareSDK.getVimPort().destroyTask(vmMor);
            return taskMor.getValue();
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VimFaultFaultMsg e) {
            log.error("VimFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.DELETE_VM_ERROR);
    }

    @Override
    public Map<String, List<VirtualMachineInfo>> getVmsByHosts(String vmwareId, List<String> hostIds) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance,
            Collections.singletonList(VMConstants.HOST_SYSTEM), Collections.singletonList(VMConstants.NAME));
        if (CollectionUtils.isNotEmpty(objectContents)) {
            return hostVms(vmwareId, hostIds, objectContents);
        }
        return new HashMap<>();
    }

    private Map<String, List<VirtualMachineInfo>> hostVms(String vmwareId, List<String> hostIds,
        List<ObjectContent> retrieveResult) {
        Map<String, List<VirtualMachineInfo>> map = new HashMap<>();
        for (ObjectContent object : retrieveResult) {
            Map<String, Object> objMap = object.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            try {
                JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(objMap));
                String hostId = object.getObj().getValue();
                if (hostIds.contains(hostId)) {
                    List<VirtualMachineInfo> virtualMachineInfos = getVmsByHost(vmwareId, hostId);
                    map.put(jsonNode.path(VMConstants.NAME).asText(), virtualMachineInfos);
                }
            } catch (JsonProcessingException e) {
                log.error(VMwareConstants.JSON_PROCESSING_EXCEPTION, SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return map;
    }

    @Override
    public VmVNCInfo getVmVNCbyVmId(String vmwareId, String vmId) {
        VmVNCInfo vmVNCInfo = new VmVNCInfo();
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMConstants.VIRTUAL_MACHINE)
            .value(vmId)
            .build();
        List<String> properties = new ArrayList<>();
        properties.add(VMConstants.SUMMARY);
        properties.add(VMConstants.NAME);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            properties);
        ObjectContent objectContent = objectContents.get(0);
        Map<String, Object> objMap = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(objMap));
            vmVNCInfo.setOsType(getOSNameByFullName(
                jsonNode.path(VMConstants.SUMMARY).path(VMConstants.GUEST).path("guestFullName").asText()));
            vmVNCInfo.setVmName(jsonNode.path(VMConstants.NAME).asText());
            vmVNCInfo.setIp(
                jsonNode.path(VMConstants.SUMMARY).path(VMConstants.GUEST).path(VMConstants.IPADDRESS).textValue());
            String powerStatus = jsonNode.path(VMConstants.SUMMARY)
                .path(VMConstants.RUNTIME)
                .path(VMConstants.POWER_STATE)
                .asText();
            vmVNCInfo.setStatus(StringUtils.equalsIgnoreCase(powerStatus, "poweredOn") ? "running" : "stopped");
            VirtualMachineTicket ticket = sdkInstance.getVimPort()
                .acquireTicket(managedObjectReference, VMConstants.WEB_MKS);
            vmVNCInfo.setVncPort(String.valueOf(ticket.getPort()));
            vmVNCInfo.setVncHost(ticket.getHost());
            vmVNCInfo.setVncPassword(ticket.getTicket());
        } catch (JsonProcessingException | InvalidStateFaultMsg | RuntimeFaultFaultMsg e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        return vmVNCInfo;
    }

    @Override
    public VirtualMachineInfo getVmByVmId(String vmwareId, String vmId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMConstants.VIRTUAL_MACHINE)
            .value(vmId)
            .build();
        List<String> properties = Arrays.asList(VMConstants.NAME, VMConstants.GUEST, VMConstants.OVERALL_STATE,
            VMConstants.SUMMARY, VMConstants.CONFIG, VMConstants.RUNTIME, VMConstants.RESOURCE_POOL);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            properties);
        return getVirtualMachineInfos(sdkInstance, objectContents).get(0);
    }

    @Override
    public VmVNCStatusInfo getVmVNCStatus(String vmwareId, String vmId) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference vmRef = ManagedObjectReferenceBuilder.getInstance()
            .type(VMConstants.VIRTUAL_MACHINE)
            .value(vmId)
            .build();
        List<String> properties = Arrays.asList(VMConstants.NAME, VMConstants.SUMMARY, VMConstants.CONFIG);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(vMwareSDK, vmRef, properties);
        Map<String, Object> objMap = objectContents.get(0)
            .getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(objMap));
            VmVNCStatusInfo vmVNCStatusInfo = new VmVNCStatusInfo();
            vmVNCStatusInfo.setOsType(getOSNameByFullName(
                jsonNode.path(VMConstants.SUMMARY).path(VMConstants.GUEST).path("guestFullName").asText()));
            vmVNCStatusInfo.setVmName(jsonNode.path(VMwareConstants.NAME).asText());
            vmVNCStatusInfo.setVersion(vMwareSDK.getServiceContent().getAbout().getVersion());
            VirtualMachineTicket virtualMachineTicket = vMwareSDK.getVimPort()
                .acquireTicket(vmRef, VMConstants.WEB_MKS);
            vmVNCStatusInfo.setTicket(virtualMachineTicket.getTicket());
            vmVNCStatusInfo.setVncPort(String.valueOf(virtualMachineTicket.getPort()));
            vmVNCStatusInfo.setVncHost(virtualMachineTicket.getHost());
            vmVNCStatusInfo.setVncEnabled(false);
            vmVNCStatusInfo.setVncPassword(virtualMachineTicket.getTicket());
            return vmVNCStatusInfo;
        } catch (JsonProcessingException | InvalidStateFaultMsg | RuntimeFaultFaultMsg e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }

    private ManagedObjectReference getHostMor(VMwareSDK vMwareSDK, String clusterId, String hostId) {
        ManagedObjectReference hostSystem = new ManagedObjectReference();
        hostSystem.setType(VMConstants.HOST_SYSTEM);
        hostSystem.setValue(hostId);
        if (StringUtils.containsAny(clusterId, "domain-s")) {
            return StringUtils.isNotBlank(hostId) ? hostSystem : new ManagedObjectReference();
        }
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMwareConstants.CLUSTER_COMPUTE_RESOURCE)
            .value(clusterId)
            .build();
        List<String> properties = Collections.singletonList(VMConstants.CONFIGURATION);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(vMwareSDK, managedObjectReference,
            properties);
        ObjectContent objectContent = objectContents.get(0);
        Map<String, Object> collect = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        JsonNode jsonNode;
        boolean drs = false;
        try {
            jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            drs = jsonNode.path(VMConstants.CONFIGURATION).path("drsConfig").path("enabled").asBoolean();
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        if (StringUtils.isEmpty(hostId)) {
            if (!drs) {
                throw new PluginException(RestCodeEnum.CREATE_VM_ERROR);
            }
        }
        return StringUtils.isNotBlank(hostId) ? hostSystem : new ManagedObjectReference();
    }

    @Override
    public String createVmByConfig(String vmwareId, VmConfigurationInfo vmConfigInfo) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        processingParam(vMwareSDK, vmConfigInfo);
        ManagedObjectReference hostSystem = getHostMor(vMwareSDK, vmConfigInfo.getClusterId(),
            vmConfigInfo.getHostId());
        if (StringUtils.isBlank(hostSystem.getValue())) {
            hostSystem = null;
        }
        if (checkHardWare(vMwareSDK, vmConfigInfo, hostSystem)) {
            throw new PluginException(RestCodeEnum.ILLEGAL_INPUT_PARAMS_ERROR);
        }
        ManagedObjectReference vmFolderRef = getMor(vMwareSDK, VMConstants.DATACENTER, vmConfigInfo.getDataCenterId(),
            VMConstants.VM_FOLDER);
        VirtualMachineConfigSpec vmConfigSpec = getVirtualMachineConfigSpec(vmConfigInfo, vMwareSDK);
        ManagedObjectReference resourceRef = getResourcePool(vMwareSDK, vmConfigInfo.getClusterId(),
            VMConstants.RESOURCE_POOL);
        try {
            ManagedObjectReference taskMor = vMwareSDK.getVimPort()
                .createVMTask(vmFolderRef, vmConfigSpec, resourceRef, hostSystem);
            checkCreateVMTaskAndPowerOnVMAsync(taskMor, vmwareId);
            return taskMor.getValue();
        } catch (AlreadyExistsFaultMsg e) {
            log.error("AlreadyExistsFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (DuplicateNameFaultMsg e) {
            log.error("DuplicateNameFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (FileFaultFaultMsg e) {
            log.error("FileFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InsufficientResourcesFaultFaultMsg e) {
            log.error("InsufficientResourcesFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidDatastoreFaultMsg e) {
            log.error("InvalidDatastoreFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidNameFaultMsg e) {
            log.error("InvalidNameFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (InvalidStateFaultMsg e) {
            log.error("InvalidStateFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (OutOfBoundsFaultMsg e) {
            log.error("OutOfBoundsFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (VmConfigFaultFaultMsg e) {
            log.error("VmConfigFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.CREATE_VM_ERROR);
    }

    private VirtualMachineConfigSpec getVirtualMachineConfigSpec(VmConfigurationInfo vmConfigInfo,
        VMwareSDK vMwareSDK) {
        VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();
        String vmName = StringUtils.replace(vmConfigInfo.getVmName(), "%", "%25");
        vmConfigSpec.setName(vmName);
        vmConfigSpec.setGuestId(vmConfigInfo.getOsVersion());
        vmConfigSpec.setMemoryMB(vmConfigInfo.getMemorySize());
        vmConfigSpec.setNumCPUs(vmConfigInfo.getCpuInfo().getCount());
        vmConfigSpec.setNumCoresPerSocket(vmConfigInfo.getCpuInfo().getCoreSockets());
        String[] dataStore = getDataStore(vMwareSDK, vmConfigInfo.getDatastoreId(), VMConstants.DATASTORE);
        vmConfigSpec.getDeviceChange().addAll(getNetworks(vmConfigInfo.getNetworks()));
        vmConfigSpec.getDeviceChange().addAll(getController());
        vmConfigSpec.getDeviceChange().addAll(getDisks(0, vmConfigInfo.getDisks(), dataStore, vmName));
        vmConfigSpec.getDeviceChange().addAll(getCdrom(vmConfigInfo.getCdrom(), vmConfigInfo.getDatastoreId()));
        if (vmConfigInfo.getDisks() != null) {
            vmConfigSpec.getDeviceChange()
                .addAll(getRDMs(vmConfigInfo.getDisks().size(), vmConfigInfo.getRdms(), dataStore[0]));
        } else {
            vmConfigSpec.getDeviceChange().addAll(getRDMs(0, vmConfigInfo.getRdms(), dataStore[0]));
        }
        vmConfigSpec.setFiles(getVmFileInfo(vmName, dataStore[0]));
        return vmConfigSpec;
    }

    /**
     * processing pram
     *
     * @param vMwareSDK vmware sdk
     * @param vmConfigInfo config
     */
    private void processingParam(VMwareSDK vMwareSDK, VmConfigurationBasicInfo vmConfigInfo) {
        if (StringUtils.isBlank(vmConfigInfo.getClusterId()) && StringUtils.isNotBlank(vmConfigInfo.getHostId())) {
            List<ObjectContent> retrieveProperties = vmwareSDKClient.retrieveProperties(vMwareSDK,
                ManagedObjectReferenceBuilder.getInstance()
                    .type(VMwareConstants.HOST_SYSTEM)
                    .value(vmConfigInfo.getHostId())
                    .build(), Collections.singletonList("parent"));
            ManagedObjectReference cluster = (ManagedObjectReference) (retrieveProperties.get(0)
                .getPropSet()
                .get(0)
                .getVal());
            vmConfigInfo.setClusterId(cluster.getValue());
        }
        if (StringUtils.isBlank(vmConfigInfo.getDataCenterId())) {
            List<ObjectContent> retrieveProperties = vmwareSDKClient.retrieveProperties(vMwareSDK,
                ManagedObjectReferenceBuilder.getInstance()
                    .type(VMwareConstants.COMPUTE_RESOURCE)
                    .value(vmConfigInfo.getClusterId())
                    .build(), Collections.singletonList("parent"));
            if ((retrieveProperties.get(0).getPropSet().get(0).getVal()) instanceof ManagedObjectReference) {
                ManagedObjectReference folder = (ManagedObjectReference) (retrieveProperties.get(0)
                    .getPropSet()
                    .get(0)
                    .getVal());
                String groupFolder = folder.getValue();
                List<ObjectContent> retrieveProp = vmwareSDKClient.retrieveProperties(vMwareSDK,
                    ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.FOLDER).value(groupFolder).build(),
                    Collections.singletonList("parent"));
                if ((retrieveProp.get(0).getPropSet().get(0).getVal()) instanceof ManagedObjectReference) {
                    ManagedObjectReference dataCenter = (ManagedObjectReference) (retrieveProp.get(0)
                        .getPropSet()
                        .get(0)
                        .getVal());
                    vmConfigInfo.setDataCenterId(dataCenter.getValue());
                }
            }
        }
    }

    private void checkCreateVMTaskAndPowerOnVMAsync(ManagedObjectReference taskMor, String vmwareId) {
        taskExecutor.execute(() -> loopCheckCreateVMTaskAndPowerOnVm(taskMor, vmwareId));
    }

    private void loopCheckCreateVMTaskAndPowerOnVm(ManagedObjectReference taskMor, String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<String> properties = Collections.singletonList("info");
        boolean flag = true;
        while (flag) {
            List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, taskMor, properties);
            if (objectContents.get(0).getPropSet().get(0).getVal() instanceof TaskInfo) {
                TaskInfo task = (TaskInfo) objectContents.get(0).getPropSet().get(0).getVal();
                String createStatus = task.getState().toString();
                if (StringUtils.equalsIgnoreCase(TaskInfoState.SUCCESS.value(), createStatus) &&
                    task.getResult() instanceof ManagedObjectReference) {
                    flag = false;
                    ManagedObjectReference vmMor = (ManagedObjectReference) task.getResult();
                    powerStartByVmId(vmwareId, vmMor.getValue());
                } else if (StringUtils.equalsIgnoreCase(TaskInfoState.ERROR.value(), createStatus)) {
                    throw new PluginException(RestCodeEnum.CREATE_VM_ERROR);
                } else {
                    log.info("loop until CreateVM_Task finished.");
                }
            }
        }
    }

    @Override
    public String getVmIdByVmName(String vmwareId, String vmName) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<String> retrieveObjects = new ArrayList<>();
        retrieveObjects.add(VMConstants.VIRTUAL_MACHINE);
        List<String> retrieveProperties = Collections.singletonList(VMConstants.NAME);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance, retrieveObjects,
            retrieveProperties);
        if (CollectionUtils.isNotEmpty(objectContents)) {
            for (ObjectContent obj : objectContents) {
                String vmQueryName = obj.getPropSet().get(0).getVal().toString();
                if (vmName.equals(vmQueryName)) {
                    return obj.getObj().getValue();
                }
            }
        }
        throw new PluginException(RestCodeEnum.GET_VM_ERROR);
    }

    private ManagedObjectReference getMor(VMwareSDK vMwareSDK, String type, String value, String property) {
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(type)
            .value(value)
            .build();
        List<String> properties = Collections.singletonList(property);
        List<ObjectContent> contents = vmwareSDKClient.retrieveProperties(vMwareSDK, managedObjectReference,
            properties);
        Object object = contents.get(0).getPropSet().get(0).getVal();
        ManagedObjectReference mor = null;
        if (object instanceof ManagedObjectReference) {
            mor = (ManagedObjectReference) object;
        }
        return mor;
    }

    private boolean checkHardWare(VMwareSDK vMwareSDK, VmConfigurationBasicInfo vmConfigurationBasicInfo,
        ManagedObjectReference hostMor) {
        ManagedObjectReference environmentBrowser = getResourcePool(vMwareSDK, vmConfigurationBasicInfo.getClusterId(),
            "environmentBrowser");
        VirtualMachineConfigOption virtualMachineConfigOption;
        try {
            virtualMachineConfigOption = vMwareSDK.getVimPort().queryConfigOption(environmentBrowser, null, hostMor);
            VirtualHardwareOption virtualHardwareOption = virtualMachineConfigOption.getHardwareOptions();
            if (vmConfigurationBasicInfo.getCpuInfo().getCoreSockets() >
                virtualHardwareOption.getNumCoresPerSocket().getMax()) {
                return true;
            }
            if (vmConfigurationBasicInfo.getMemorySize() > virtualHardwareOption.getMemoryMB().getMax()) {
                return true;
            }
        } catch (RuntimeFaultFaultMsg e) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        return false;
    }

    private ManagedObjectReference getResourcePool(VMwareSDK vMwareSDK, String clusterId, String property) {
        ManagedObjectReference mor;
        try {
            mor = getMor(vMwareSDK, VMConstants.CLUSTER_COMPUTE_RESOURCE, clusterId, property);
        } catch (RuntimeException e) {
            mor = getMor(vMwareSDK, VMConstants.COMPUTE_RESOURCE, clusterId, property);
        }
        return mor;
    }

    @Override
    public String cloneVmByTemplate(String vmwareId, VmTemplateInfo vmTemplateInfo) {
        VMwareSDK vMwareSDK = vmwareSDKClient.getSDKInstance(vmwareId);
        processingParam(vMwareSDK, vmTemplateInfo);
        ManagedObjectReference hostSystem = getHostMor(vMwareSDK, vmTemplateInfo.getClusterId(),
            vmTemplateInfo.getHostId());
        if (StringUtils.isBlank(hostSystem.getValue())) {
            hostSystem = null;
        }
        if (checkHardWare(vMwareSDK, vmTemplateInfo, hostSystem)) {
            throw new PluginException(RestCodeEnum.ILLEGAL_INPUT_PARAMS_ERROR);
        }
        ManagedObjectReference vmMor = new ManagedObjectReference();
        vmMor.setValue(vmTemplateInfo.getTemplateId());
        vmMor.setType(VMConstants.VIRTUAL_MACHINE);
        ManagedObjectReference vmFolderRef = getMor(vMwareSDK, VMConstants.DATACENTER, vmTemplateInfo.getDataCenterId(),
            VMConstants.VM_FOLDER);
        String vmName = StringUtils.replace(vmTemplateInfo.getVmName(), "%", "%25");
        String[] dataStore = getDataStore(vMwareSDK, vmTemplateInfo.getDatastoreId(), VMConstants.DATASTORE);
        List<String> properties = Arrays.asList(VMConstants.GUEST, VMConstants.CONFIG);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(vMwareSDK, vmMor, properties);
        List<DynamicProperty> dynamicProperties = objectContents.get(0).getPropSet();
        int index = 0;
        for (DynamicProperty dynamicProperty : dynamicProperties) {
            if (VMConstants.CONFIG.equals(dynamicProperty.getName())) {
                if (dynamicProperty.getVal() instanceof VirtualMachineConfigInfo) {
                    VirtualMachineConfigInfo virtualMachineConfigInfo
                        = (VirtualMachineConfigInfo) dynamicProperty.getVal();
                    index = getDiskNum(virtualMachineConfigInfo, dataStore[1]);
                }
            }
        }
        VirtualMachineCloneSpec virtualMachineCloneSpec = getVmCloneSpec(vMwareSDK, index, vmTemplateInfo, dataStore);
        try {
            ManagedObjectReference taskMor = vMwareSDK.getVimPort()
                .cloneVMTask(vmMor, vmFolderRef, vmName, virtualMachineCloneSpec);
            return taskMor.getValue();
        } catch (CustomizationFaultFaultMsg | FileFaultFaultMsg | InsufficientResourcesFaultFaultMsg | InvalidDatastoreFaultMsg | InvalidStateFaultMsg | RuntimeFaultFaultMsg | MigrationFaultFaultMsg | TaskInProgressFaultMsg | VmConfigFaultFaultMsg e) {
            log.error("VmConfigFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.CREATE_VM_ERROR);
    }

    private Integer getDiskNum(VirtualMachineConfigInfo virtualMachineConfigInfo, String dataStoreMemory) {
        int index = 0;
        VirtualHardware virtualHardware = virtualMachineConfigInfo.getHardware();
        List<VirtualDevice> virtualDeviceList = virtualHardware.getDevice();
        long memory = 0;
        for (VirtualDevice vd : virtualDeviceList) {
            if (vd instanceof VirtualDisk) {
                long diskSize = ((VirtualDisk) vd).getCapacityInBytes();
                index++;
                memory += diskSize;
                if (memory > Long.valueOf(dataStoreMemory)) {
                    throw new PluginException(RestCodeEnum.ILLEGAL_INPUT_PARAMS_ERROR);
                }
            }
        }
        return index;
    }

    private VirtualMachineCloneSpec getVmCloneSpec(VMwareSDK vMwareSDK, int index, VmTemplateInfo vmTemplateInfo,
        String[] dataStore) {
        VirtualMachineCloneSpec virtualMachineCloneSpec = new VirtualMachineCloneSpec();
        virtualMachineCloneSpec.setPowerOn(vmTemplateInfo.isPowerOn());
        VirtualMachineRelocateSpec location = new VirtualMachineRelocateSpec();
        ManagedObjectReference hostRef;
        if (!StringUtils.isEmpty(vmTemplateInfo.getHostId())) {
            hostRef = new ManagedObjectReference();
            hostRef.setType(VMConstants.HOST_SYSTEM);
            hostRef.setValue(vmTemplateInfo.getHostId());
        }
        ManagedObjectReference dataStoreRef = new ManagedObjectReference();
        dataStoreRef.setType(VMConstants.DATASTORE);
        dataStoreRef.setValue(vmTemplateInfo.getDatastoreId());
        location.setDatastore(dataStoreRef);
        ManagedObjectReference resourceRef = getResourcePool(vMwareSDK, vmTemplateInfo.getClusterId(),
            VMConstants.RESOURCE_POOL);
        location.setPool(resourceRef);
        virtualMachineCloneSpec.setLocation(location);
        ToolsConfigInfo info = new ToolsConfigInfo();
        info.setAfterPowerOn(true);
        info.setAfterResume(true);
        info.setBeforeGuestStandby(false);
        info.setBeforeGuestReboot(true);
        info.setBeforeGuestShutdown(true);
        VirtualMachineConfigSpec config = new VirtualMachineConfigSpec();
        config.setTools(info);
        config.setNumCPUs(vmTemplateInfo.getCpuInfo().getCount());
        config.setMemoryMB(vmTemplateInfo.getMemorySize());
        config.setNumCoresPerSocket(vmTemplateInfo.getCpuInfo().getCoreSockets());
        config.setFiles(getVmFileInfo(vmTemplateInfo.getVmName(), dataStore[0]));
        config.setName(vmTemplateInfo.getVmName());
        config.getDeviceChange().addAll(getNetworks(vmTemplateInfo.getNetworks()));
        config.getDeviceChange()
            .addAll(getDisks(index, vmTemplateInfo.getDisks(), dataStore, vmTemplateInfo.getVmName() + "_clone"));
        if (vmTemplateInfo.getDisks() != null) {
            config.getDeviceChange()
                .addAll(getRDMs(vmTemplateInfo.getDisks().size() + index, vmTemplateInfo.getRdms(), dataStore[0]));
            virtualMachineCloneSpec.setConfig(config);
        } else {
            config.getDeviceChange().addAll(getRDMs(index, vmTemplateInfo.getRdms(), dataStore[0]));
        }
        virtualMachineCloneSpec.setConfig(config);
        virtualMachineCloneSpec.setPowerOn(vmTemplateInfo.isPowerOn());
        virtualMachineCloneSpec.setTemplate(false);
        if (vmTemplateInfo.isAppmanagement()) {
            CustomizationSpec customization = processingCustomization(vmTemplateInfo);
            virtualMachineCloneSpec.setCustomization(customization);
        }
        return virtualMachineCloneSpec;
    }

    private CustomizationSpec processingCustomization(VmTemplateInfo vmTemplateInfo) {
        CustomizationSpec customization = new CustomizationSpec();
        if (CollectionUtils.isNotEmpty(vmTemplateInfo.getNis())) {
            for (Network ni : vmTemplateInfo.getNis()) {
                if (ni.isManageTypeNetwork()) {
                    customization.getNicSettingMap().add(0, addAdapter(ni));
                } else {
                    customization.getNicSettingMap().add(addAdapter(ni));
                }
            }
        }
        processingIdentification(vmTemplateInfo, customization);
        return customization;
    }

    private String hostName(String name) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9]+");
        Matcher matcher = pattern.matcher(name);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            result.append(matcher.group());
        }
        return result.toString();
    }

    private void processingIdentification(VmTemplateInfo vmTemplateInfo, CustomizationSpec customization) {
        if (StringUtils.isNotBlank(vmTemplateInfo.getOsPassword())) {
            CustomizationPassword customizationPassword = new CustomizationPassword();
            customizationPassword.setPlainText(true);
            customizationPassword.setValue(vmTemplateInfo.getOsPassword());
            CustomizationGuiUnattended guiUnattended = new CustomizationGuiUnattended();
            guiUnattended.setPassword(customizationPassword);
            guiUnattended.setAutoLogon(false);
            guiUnattended.setTimeZone(201);
            guiUnattended.setAutoLogonCount(0);
            CustomizationFixedName fixedName = new CustomizationFixedName();
            fixedName.setName(hostName(vmTemplateInfo.getVmName()));
            CustomizationGlobalIPSettings globalIPSettings = new CustomizationGlobalIPSettings();
            if (Constants.OS_WINDOWS.equalsIgnoreCase(vmTemplateInfo.getOsType())) {
                processingWindowsIdentification(fixedName, vmTemplateInfo.getVmName(), customizationPassword,
                    guiUnattended, customization);
            } else {
                CustomizationLinuxPrep identitySettings = new CustomizationLinuxPrep();
                identitySettings.setHostName(fixedName);
                identitySettings.setDomain("localdomain");
                customization.setIdentity(identitySettings);
                if (CollectionUtils.isNotEmpty(vmTemplateInfo.getNis()) && getGlobalIps(vmTemplateInfo).size() > 0) {
                    globalIPSettings.getDnsServerList().addAll(getGlobalIps(vmTemplateInfo));
                }
            }
            customization.setGlobalIPSettings(globalIPSettings);
        }
    }

    private  List<String> getGlobalIps(VmTemplateInfo vmTemplateInfo) {
        return Optional.ofNullable(vmTemplateInfo.getNis())
            .orElse(Collections.emptyList())
            .stream()
            .filter(network -> Objects.nonNull(network) && CollectionUtils.isNotEmpty(network.getDns()))
            .flatMap(network -> network.getDns().stream())
            .filter(StringUtils::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
    }

    private void processingWindowsIdentification(CustomizationFixedName fixedName, String vmName,
        CustomizationPassword customizationPassword, CustomizationGuiUnattended guiUnattended,
        CustomizationSpec customization) {
        log.info("Create Windows customization identification enter.");
        CustomizationUserData userdata = new CustomizationUserData();
        userdata.setComputerName(fixedName);
        userdata.setFullName(vmName);
        userdata.setOrgName(Constants.LOCAL_DOMAIN);
        userdata.setProductId("");
        CustomizationIdentification identification = new CustomizationIdentification();
        identification.setJoinWorkgroup(Constants.WINDOWS_WORKGROUP);
        identification.setDomainAdminPassword(customizationPassword);
        CustomizationSysprep identitySettings = new CustomizationSysprep();
        identitySettings.setGuiUnattended(guiUnattended);
        identitySettings.setUserData(userdata);
        identitySettings.setIdentification(identification);
        CustomizationLicenseFilePrintData licenseFilePrintData = new CustomizationLicenseFilePrintData();
        licenseFilePrintData.setAutoUsers(5);
        licenseFilePrintData.setAutoMode(CustomizationLicenseDataMode.PER_SERVER);
        identitySettings.setLicenseFilePrintData(licenseFilePrintData);
        customization.setIdentity(identitySettings);
    }

    private CustomizationAdapterMapping addAdapter(Network network) {
        CustomizationIPSettings adapter3 = new CustomizationIPSettings();
        CustomizationFixedIp ip3 = new CustomizationFixedIp();
        ip3.setIpAddress(network.getIpAddress());
        adapter3.setIp(ip3);
        adapter3.setDnsDomain("localdomain");
        adapter3.setSubnetMask(network.getNetmask());
        adapter3.getGateway().add(network.getGateway());

        List<String> dnsServerList = Optional.ofNullable(network.getDns())
            .orElse(Collections.emptyList())
            .stream()
            .filter(StringUtils::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dnsServerList)) {
            // dns 列表不为空时，添加到自定义网卡中
            adapter3.getDnsServerList().addAll(dnsServerList);
        }
        CustomizationAdapterMapping map3 = new CustomizationAdapterMapping();
        map3.setAdapter(adapter3);
        return map3;
    }

    private VirtualMachineFileInfo getVmFileInfo(String vmName, String dataStoreName) {
        String vmPath = String.format(Locale.ROOT, "[%s]%s%s%s.vmx", dataStoreName, vmName, File.separator, vmName);
        VirtualMachineFileInfo virtualMachineFileInfo = new VirtualMachineFileInfo();
        virtualMachineFileInfo.setVmPathName(vmPath);
        return virtualMachineFileInfo;
    }

    private List<VirtualDeviceConfigSpec> getNetworks(List<NetworkInfo> networkInfos) {
        List<VirtualDeviceConfigSpec> virtualDeviceConfigSpecList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(networkInfos)) {
            int key = INIT_DEVICE_KEY;
            for (NetworkInfo networkInfo : networkInfos) {
                VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
                virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
                VirtualEthernetCard virtualEthernetCard = new VirtualVmxnet3();
                if (StringUtils.isEmpty(networkInfo.getMacAddress())) {
                    virtualEthernetCard.setAddressType("Generated");
                } else {
                    virtualEthernetCard.setAddressType("Manual");
                    virtualEthernetCard.setMacAddress(networkInfo.getMacAddress());
                }
                VirtualEthernetCardNetworkBackingInfo nicBackingInfo = new VirtualEthernetCardNetworkBackingInfo();
                nicBackingInfo.setDeviceName(networkInfo.getName());
                virtualEthernetCard.setBacking(nicBackingInfo);
                virtualEthernetCard.setKey(key++);
                virtualDeviceConfigSpec.setDevice(virtualEthernetCard);
                virtualDeviceConfigSpecList.add(virtualDeviceConfigSpec);
            }
        }
        return virtualDeviceConfigSpecList;
    }

    private List<VirtualDeviceConfigSpec> getController() {
        VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
        virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        VirtualLsiLogicController virtualLsiLogicController = new VirtualLsiLogicController();
        virtualLsiLogicController.setKey(VMConstants.VIRTUAL_SSSI_CONTROLLER_KEY);
        virtualLsiLogicController.setBusNumber(0);
        virtualLsiLogicController.setSharedBus(VirtualSCSISharing.NO_SHARING);
        virtualDeviceConfigSpec.setDevice(virtualLsiLogicController);
        return Collections.singletonList(virtualDeviceConfigSpec);
    }

    private List<VirtualDeviceConfigSpec> getDisks(int index, List<DiskInfo> disks, String[] dataStore, String vmName) {
        List<VirtualDeviceConfigSpec> virtualDeviceConfigSpecList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(disks)) {
            long memory = 0L;
            for (int i = 0; i < disks.size(); i++) {
                VirtualDisk virtualDisk = new VirtualDisk();
                virtualDisk.setCapacityInKB(DataSize.ofBytes(disks.get(i).getMemory()).toKilobytes());
                memory += virtualDisk.getCapacityInKB();
                if (memory > Long.valueOf(dataStore[1])) {
                    throw new PluginException(RestCodeEnum.CREATE_VM_ERROR);
                }
                VirtualDeviceConfigSpec virtualDeviceConfigSpecDisk = new VirtualDeviceConfigSpec();
                virtualDeviceConfigSpecDisk.setOperation(VirtualDeviceConfigSpecOperation.ADD);
                virtualDeviceConfigSpecDisk.setFileOperation(VirtualDeviceConfigSpecFileOperation.CREATE);
                VirtualDiskFlatVer2BackingInfo virtualDiskFlatVer2BackingInfo = new VirtualDiskFlatVer2BackingInfo();
                virtualDiskFlatVer2BackingInfo.setDiskMode(VirtualDiskMode.PERSISTENT.value());
                virtualDiskFlatVer2BackingInfo.setThinProvisioned(disks.get(i).isThin());
                String fileName = String.format(Locale.ROOT, "[%s]%s%s%s-%s.vmdk", dataStore[0], vmName, File.separator,
                    vmName, i);
                virtualDiskFlatVer2BackingInfo.setFileName(fileName);

                virtualDisk.setUnitNumber(index + i);
                virtualDisk.setControllerKey(VMConstants.VIRTUAL_SSSI_CONTROLLER_KEY);
                virtualDisk.setBacking(virtualDiskFlatVer2BackingInfo);
                virtualDeviceConfigSpecDisk.setDevice(virtualDisk);
                virtualDeviceConfigSpecList.add(virtualDeviceConfigSpecDisk);
            }
        }
        return virtualDeviceConfigSpecList;
    }

    private List<VirtualDeviceConfigSpec> getCdrom(List<CdromInfo> cdromInfos, String dataStoreId) {
        List<VirtualDeviceConfigSpec> virtualDeviceConfigSpecList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cdromInfos)) {
            for (CdromInfo cdromInfo : cdromInfos) {
                VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
                VirtualCdrom virtualCdrom = new VirtualCdrom();
                VirtualDeviceBackingInfo virtualDeviceBackingInfo;
                if (VMConstants.VIRTUAL_CDROM_ISO.equals(cdromInfo.getDeviceType())) {
                    ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
                        .type(VMConstants.DATASTORE)
                        .value(dataStoreId)
                        .build();
                    virtualDeviceBackingInfo = getISOBacking(managedObjectReference, cdromInfo.getIsoFile());
                } else {
                    virtualDeviceBackingInfo = getPassBacking(cdromInfo);
                }
                virtualCdrom.setBacking(virtualDeviceBackingInfo);
                virtualCdrom.setControllerKey(VMConstants.VIRTUAL_IDE_CONTROLLER_KEY);
                virtualDeviceConfigSpec.setDevice(virtualCdrom);
                virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
                virtualDeviceConfigSpecList.add(virtualDeviceConfigSpec);
            }
        }
        return virtualDeviceConfigSpecList;
    }

    private VirtualDeviceBackingInfo getISOBacking(ManagedObjectReference dataStoreRef, String fileName) {
        VirtualCdromIsoBackingInfo virtualDeviceBackingInfo = new VirtualCdromIsoBackingInfo();
        virtualDeviceBackingInfo.setDatastore(dataStoreRef);
        virtualDeviceBackingInfo.setFileName(fileName);
        return virtualDeviceBackingInfo;
    }

    private VirtualDeviceBackingInfo getPassBacking(CdromInfo cdromInfo) {
        VirtualCdromRemotePassthroughBackingInfo vcBacking = new VirtualCdromRemotePassthroughBackingInfo();
        vcBacking.setExclusive(false);
        vcBacking.setUseAutoDetect(false);
        vcBacking.setDeviceName(cdromInfo.getName());
        return vcBacking;
    }

    private List<VirtualDeviceConfigSpec> getRDMs(int index, List<RDMInfo> rdmInfos, String dataStoreName) {
        List<VirtualDeviceConfigSpec> virtualDeviceConfigSpecList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rdmInfos)) {
            for (int i = 0; i < rdmInfos.size(); i++) {
                RDMInfo rdmInfo = rdmInfos.get(i);
                VirtualDisk virtualDisk = new VirtualDisk();
                virtualDisk.setControllerKey(VMConstants.VIRTUAL_SSSI_CONTROLLER_KEY);
                virtualDisk.setUnitNumber(index + i);
                VirtualDiskRawDiskMappingVer1BackingInfo rdmBacking = new VirtualDiskRawDiskMappingVer1BackingInfo();
                rdmBacking.setCompatibilityMode(VirtualDiskCompatibilityMode.PHYSICAL_MODE.value());
                rdmBacking.setDeviceName(rdmInfo.getDeviceName());
                rdmBacking.setFileName(String.format(Locale.ROOT, "[%s]", dataStoreName));
                virtualDisk.setBacking(rdmBacking);
                VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
                virtualDeviceConfigSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.CREATE);
                virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
                virtualDeviceConfigSpec.setDevice(virtualDisk);
                virtualDeviceConfigSpecList.add(virtualDeviceConfigSpec);
            }
        }
        return virtualDeviceConfigSpecList;
    }

}


