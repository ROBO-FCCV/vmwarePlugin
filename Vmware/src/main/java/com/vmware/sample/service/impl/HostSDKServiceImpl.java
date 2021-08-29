/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.host.HostBasic;
import com.vmware.sample.model.host.HostBusAdapterVo;
import com.vmware.sample.service.DatastoreService;
import com.vmware.sample.service.HostService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.HostBlockHba;
import com.vmware.vim25.HostConfigFaultFaultMsg;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostFibreChannelHba;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostHostBusAdapter;
import com.vmware.vim25.HostInternetScsiHba;
import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.HostSerialAttachedHba;
import com.vmware.vim25.HostSystemConnectionState;
import com.vmware.vim25.HostSystemIdentificationInfo;
import com.vmware.vim25.HostSystemPowerState;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NotFoundFaultMsg;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ScsiLun;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Host sdk service
 *
 * @since 2020-09-23
 */
@Slf4j
@Service("host-sdk-service")
public class HostSDKServiceImpl implements HostService {
    private final ObjectMapper objectMapper;
    private final DatastoreService datastoreService;
    private final VmwareSDKClient vmwareSDKClient;

    public HostSDKServiceImpl(VmwareSDKClient vmwareSDKClient, ObjectMapper objectMapper,
        @Qualifier("datastore-sdk-service") DatastoreService datastoreService) {
        this.vmwareSDKClient = vmwareSDKClient;
        this.objectMapper = objectMapper;
        this.datastoreService = datastoreService;
    }

    @Override
    public HostBasic getHost(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMwareConstants.HOST_SYSTEM)
            .value(hostId)
            .build();
        List<String> properties = new ArrayList<>();
        properties.add(VMwareConstants.SUMMARY);
        properties.add(VMwareConstants.NAME);
        properties.add(VMwareConstants.HARDWARE);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            properties);
        // Single object
        ObjectContent objectContent = objectContents.get(0);
        Map<String, Object> collect = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            return setHostBasicInfo(vmwareId, hostId, jsonNode);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        return new HostBasic();
    }

    private HostBasic setHostBasicInfo(String vmwareId, String hostId, JsonNode jsonNode) {
        HostBasic hostInfo = new HostBasic();
        hostInfo.setMoId(hostId);
        hostInfo.setInMaintenanceMode(jsonNode.path(VMwareConstants.SUMMARY)
            .path(VMwareConstants.RUN_TIME)
            .path("inMaintenanceMode")
            .asBoolean());
        hostInfo.setInQuarantineMode(
            jsonNode.path(VMwareConstants.SUMMARY).path(VMwareConstants.RUN_TIME).path("inQuarantineMode").asBoolean());
        hostInfo.setPowerState(HostSystemPowerState.valueOf(
            jsonNode.path(VMwareConstants.SUMMARY).path(VMwareConstants.RUN_TIME).path("powerState").asText()));
        hostInfo.setStatus(HostSystemConnectionState.valueOf(
            jsonNode.path(VMwareConstants.SUMMARY).path(VMwareConstants.RUN_TIME).path("connectionState").asText()));

        hostInfo.setIpAddress(jsonNode.path(VMwareConstants.NAME).asText());
        hostInfo.setName(jsonNode.path(VMwareConstants.NAME).asText());
        hostInfo.setTotalCpu(BigDecimal.valueOf(
            jsonNode.path(VMwareConstants.HARDWARE).path(VMwareConstants.CPU_INFO).path("hz").asLong())
            .multiply(BigDecimal.valueOf(
                jsonNode.path(VMwareConstants.HARDWARE).path(VMwareConstants.CPU_INFO).path("numCpuCores").asLong()))
            .longValue());
        hostInfo.setUsedCpu(BigDecimal.valueOf(
            jsonNode.path(VMwareConstants.SUMMARY).path("quickStats").path("overallCpuUsage").asLong())
            .multiply(BigDecimal.valueOf(Math.pow(10, 6)))
            .longValue());
        hostInfo.setFreeCpu(hostInfo.getTotalCpu() - hostInfo.getUsedCpu());
        hostInfo.setTotalMemory(DataSize.ofBytes(jsonNode.path(VMwareConstants.HARDWARE).path("memorySize").asLong()));
        hostInfo.setUsedMemory(DataSize.ofMegabytes(
            jsonNode.path(VMwareConstants.SUMMARY).path("quickStats").path("overallMemoryUsage").asLong()));
        hostInfo.setFreeMemory(
            DataSize.ofBytes(hostInfo.getTotalMemory().toBytes() - hostInfo.getUsedMemory().toBytes()));
        List<DatastoreBasic> datastore = datastoreService.datastore(vmwareId, hostInfo.getMoId());
        long total = 0L;
        long used = 0L;
        for (DatastoreBasic datastoreBasic : datastore) {
            total += datastoreBasic.getCapacity().toBytes();
            used += datastoreBasic.getUsedSpace().toBytes();
        }
        hostInfo.setTotalDatastore(DataSize.ofBytes(total));
        hostInfo.setUsedDatastore(DataSize.ofBytes(used));
        hostInfo.setFreeDatastore(DataSize.ofBytes(total - used));
        return hostInfo;
    }

    @Override
    public List<HostBasic> list(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance,
            Collections.singletonList(VMwareConstants.HOST_SYSTEM),
            Arrays.asList(VMwareConstants.SUMMARY, VMwareConstants.NAME, VMwareConstants.HARDWARE));
        List<HostBasic> hostInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(objectContents)) {
            for (ObjectContent object : objectContents) {
                Map<String, Object> collect = object.getPropSet()
                    .stream()
                    .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
                try {
                    JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
                    HostBasic hostInfo = setHostBasicInfo(vmwareId, object.getObj().getValue(), jsonNode);
                    hostInfos.add(hostInfo);
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
                }
            }
        }
        return hostInfos;
    }

    @Override
    public List<HostScsiDisk> availableDisks(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        DynamicProperty dynamicProperty = getDynamicProperty(hostId, sdkInstance, VMwareConstants.CONFIG_MANAGER);
        if (dynamicProperty.getVal() instanceof HostConfigManager) {
            HostConfigManager hostConfigManager = (HostConfigManager) dynamicProperty.getVal();
            ManagedObjectReference datastoreSystem = hostConfigManager.getDatastoreSystem();
            try {
                return sdkInstance.getVimPort().queryAvailableDisksForVmfs(datastoreSystem, null);
            } catch (HostConfigFaultFaultMsg | RuntimeFaultFaultMsg | NotFoundFaultMsg e) {
                log.error("Query availableDisks failed.", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> queryStorageScsiLun(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        DynamicProperty dynamicProperty = getDynamicProperty(hostId, sdkInstance, VMwareConstants.CONFIG);
        if (dynamicProperty.getVal() instanceof HostConfigInfo) {
            HostConfigInfo hostConfigInfo = (HostConfigInfo) dynamicProperty.getVal();
            List<ScsiLun> scsiLun = hostConfigInfo.getStorageDevice().getScsiLun();
            return scsiLun.stream()
                .filter(item -> item.getCanonicalName().startsWith("naa."))
                .map(item -> StringUtils.substringAfter(item.getCanonicalName(), "naa."))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String rescanHba(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        DynamicProperty dynamicProperty = getDynamicProperty(hostId, sdkInstance, VMwareConstants.CONFIG_MANAGER);
        if (dynamicProperty.getVal() instanceof HostConfigManager) {
            HostConfigManager hostConfigManager = (HostConfigManager) dynamicProperty.getVal();
            ManagedObjectReference datastoreSystem = hostConfigManager.getStorageSystem();
            try {
                sdkInstance.getVimPort().rescanAllHba(datastoreSystem);
            } catch (HostConfigFaultFaultMsg | RuntimeFaultFaultMsg e) {
                log.error("Query availableDisks failed.", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return "Rescan done.";
    }

    @Override
    public HostBusAdapterVo busAdapters(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMwareConstants.HOST_SYSTEM)
            .value(hostId)
            .build();
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            Collections.singletonList(VMwareConstants.CONFIG));
        Object val = objectContents.get(0).getPropSet().get(0).getVal();
        if (val instanceof HostConfigInfo) {
            HostConfigInfo hostConfigInfo = (HostConfigInfo) val;
            List<HostHostBusAdapter> hostBusAdapter = hostConfigInfo.getStorageDevice().getHostBusAdapter();

            return convertVmwareHostHostBusAdaptersToHostBusAdapters(hostBusAdapter);
        }
        return new HostBusAdapterVo();
    }

    private HostBusAdapterVo convertVmwareHostHostBusAdaptersToHostBusAdapters(
        List<HostHostBusAdapter> hostBusAdapters) {
        HostBusAdapterVo hostBusAdapterVo = new HostBusAdapterVo();
        List<HostSerialAttachedHba> hostSerialAttachedHbas = new ArrayList<>();
        List<HostBlockHba> hostBlockHbas = new ArrayList<>();
        List<HostFibreChannelHba> hostFibreChannelHbas = new ArrayList<>();
        List<HostInternetScsiHba> hostInternetScsiHbas = new ArrayList<>();
        for (HostHostBusAdapter hostBusAdapter : hostBusAdapters) {
            if (hostBusAdapter instanceof HostSerialAttachedHba) {
                hostSerialAttachedHbas.add((HostSerialAttachedHba) hostBusAdapter);
            } else if (hostBusAdapter instanceof HostFibreChannelHba) {
                hostFibreChannelHbas.add((HostFibreChannelHba) hostBusAdapter);
            } else if (hostBusAdapter instanceof HostBlockHba) {
                hostBlockHbas.add((HostBlockHba) hostBusAdapter);
            } else if (hostBusAdapter instanceof HostInternetScsiHba) {
                hostInternetScsiHbas.add((HostInternetScsiHba) hostBusAdapter);
            } else {
                log.warn("Maybe new HostBusAdapterVo.Please add.");
            }
        }
        hostBusAdapterVo.setHostBlockHbas(hostBlockHbas);
        hostBusAdapterVo.setHostSerialAttachedHbas(hostSerialAttachedHbas);
        hostBusAdapterVo.setHostFibreChannelHbas(hostFibreChannelHbas);
        hostBusAdapterVo.setHostInternetScsiHbas(hostInternetScsiHbas);
        return hostBusAdapterVo;
    }

    @Override
    public ManagedObjectReference resourcePool(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMwareConstants.HOST_SYSTEM)
            .value(hostId)
            .build();
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            Collections.singletonList("parent"));
        Object val = objectContents.get(0).getPropSet().get(0).getVal();
        if (val instanceof ManagedObjectReference) {
            List<ObjectContent> retrieveProperties = vmwareSDKClient.retrieveProperties(sdkInstance,
                (ManagedObjectReference) val, Collections.singletonList(VMwareConstants.RESOURCE_POOL));
            Object val1 = retrieveProperties.get(0).getPropSet().get(0).getVal();
            if (val1 instanceof ManagedObjectReference) {
                return (ManagedObjectReference) val1;
            }
        }
        return new ManagedObjectReference();
    }

    @Override
    public String querySerialNumber(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> retrieveProperties = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
            Collections.singletonList("hardware"));
        Object dynamicProperty = retrieveProperties.get(0).getPropSet().get(0).getVal();
        if (dynamicProperty instanceof HostHardwareInfo) {
            HostHardwareInfo hostHardwareInfo = (HostHardwareInfo) dynamicProperty;
            List<HostSystemIdentificationInfo> otherIdentifyingInfo = hostHardwareInfo.getSystemInfo()
                .getOtherIdentifyingInfo();
            if (CollectionUtils.isNotEmpty(otherIdentifyingInfo)) {
                return lookupSerialNumber(otherIdentifyingInfo);
            }
        }
        return Strings.EMPTY;
    }

    private String lookupSerialNumber(List<HostSystemIdentificationInfo> otherIdentifyingInfo) {
        for (HostSystemIdentificationInfo hostSystemIdentificationInfo : otherIdentifyingInfo) {
            if (hostSystemIdentificationInfo.getIdentifierType().getKey().equalsIgnoreCase("SerialNumberTag")) {
                hostSystemIdentificationInfo.getIdentifierValue();
            }
        }
        return Strings.EMPTY;
    }

    private DynamicProperty getDynamicProperty(String hostId, VMwareSDK sdkInstance, String configManager) {
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
            Collections.singletonList(configManager));
        ObjectContent objectContent = objectContents.get(0);
        List<DynamicProperty> propSet = objectContent.getPropSet();
        return propSet.get(0);
    }
}
