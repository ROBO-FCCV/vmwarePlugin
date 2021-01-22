/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.factory.TraversalSpecBuilder;
import com.vmware.sample.model.cluster.ClusterBasic;
import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.model.host.HostInfo;
import com.vmware.sample.model.host.HostVM;
import com.vmware.sample.model.vm.VirtualMachineInfo;
import com.vmware.sample.service.ClusterService;
import com.vmware.sample.service.HostService;
import com.vmware.sample.service.VMService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.TraversalSpec;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cluster skd service
 *
 * @since 2020-09-25
 */
@Slf4j
@Service("cluster-sdk-service")
public class ClusterSDKServiceImpl implements ClusterService {
    private final ObjectMapper objectMapper;
    private final HostService hostService;
    private final VMService vmService;
    private final VmwareSDKClient vmwareSDKClient;

    public ClusterSDKServiceImpl(VmwareSDKClient vmwareSDKClient, ObjectMapper objectMapper,
        @Qualifier("host-sdk-service") HostService hostService, @Qualifier("vm-sdk-service") VMService vmService) {
        this.vmwareSDKClient = vmwareSDKClient;
        this.objectMapper = objectMapper;
        this.hostService = hostService;
        this.vmService = vmService;
    }

    @Override
    public ClusterBasic get(String vmwareId, String clusterId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMwareConstants.CLUSTER_COMPUTE_RESOURCE)
            .value(clusterId)
            .build();
        List<String> properties = new ArrayList<>();
        properties.add(VMwareConstants.SUMMARY);
        properties.add(VMwareConstants.NAME);
        properties.add(VMwareConstants.RESOURCE_POOL);
        properties.add(VMwareConstants.CONFIGURATION);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            properties);
        // single object.
        ObjectContent objectContent = objectContents.get(0);
        Map<String, Object> collect = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        ClusterBasic clusterBasic = new ClusterBasic();
        TraversalSpec clusterToDataStore = TraversalSpecBuilder.getInstance()
            .name("ClusterToDataStore")
            .path(VMwareConstants.DATASTORE)
            .type(managedObjectReference.getType())
            .build();
        TraversalSpec clusterToHost = TraversalSpecBuilder.getInstance()
            .name("ClusterToHost")
            .path(VMwareConstants.HOST)
            .type(managedObjectReference.getType())
            .build();
        // Query datastore and host below cluster.
        List<ObjectContent> datastoreAndHost = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            managedObjectReference, Arrays.asList(clusterToDataStore, clusterToHost),
            Arrays.asList(VMwareConstants.HOST_SYSTEM, VMwareConstants.DATASTORE_TYPE),
            Collections.singletonList(VMwareConstants.SUMMARY));

        return getClusterBasic(collect, clusterBasic, datastoreAndHost);
    }

    private ClusterBasic getClusterBasic(Map<String, Object> collect, ClusterBasic clusterBasic,
        List<ObjectContent> datastoreAndHost) {
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            clusterBasic.setName(jsonNode.path(VMwareConstants.NAME).asText());
            clusterBasic.setDrs(
                jsonNode.path(VMwareConstants.CONFIGURATION).path("drsConfig").path("enabled").asBoolean());
            clusterBasic.setResourcePool(
                jsonNode.path(VMwareConstants.RESOURCE_POOL).path(VMwareConstants.VALUE).asText());
            clusterBasic.setTotalCpu(jsonNode.path(VMwareConstants.SUMMARY).path("totalCpu").asLong());
            clusterBasic.setTotalMemory(
                DataSize.ofBytes(jsonNode.path(VMwareConstants.SUMMARY).path("totalMemory").asLong()));
            if (CollectionUtils.isNotEmpty(datastoreAndHost)) {
                // Group by object.
                Map<String, List<ObjectContent>> objectContentMapCollector = datastoreAndHost.stream()
                    .collect(Collectors.groupingBy(item -> item.getObj().getType()));
                setCpuAndMemory(clusterBasic, objectContentMapCollector.get(VMwareConstants.HOST_SYSTEM));
                setDataStore(clusterBasic, objectContentMapCollector.get(VMwareConstants.DATASTORE_TYPE));
            }
        } catch (JsonProcessingException e) {
            log.error(VMwareConstants.JSON_PROCESSING_EXCEPTION, SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        return clusterBasic;
    }

    @Override
    public ClusterInfo getClusterInfo(String vmwareId, String clusterId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> retrieveResult = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.COMPUTE_RESOURCE).value(clusterId).build(),
            Arrays.asList(VMwareConstants.HOST, VMwareConstants.NAME));
        ObjectContent object = retrieveResult.get(0);
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setMoId(object.getObj().getValue());
        setHostsAndName(vmwareId, object, clusterInfo);
        return clusterInfo;
    }

    @Override
    public List<ClusterInfo> list(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ClusterInfo> clusterInfos = new ArrayList<>();
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance,
            Collections.singletonList(VMwareConstants.CLUSTER_COMPUTE_RESOURCE),
            Arrays.asList(VMwareConstants.HOST, VMwareConstants.NAME));
        if (CollectionUtils.isNotEmpty(objectContents)) {
            for (ObjectContent object : objectContents) {
                ClusterInfo clusterInfo = new ClusterInfo();
                clusterInfo.setMoId(object.getObj().getValue());
                setHostsAndName(vmwareId, object, clusterInfo);
                clusterInfos.add(clusterInfo);
            }
            return clusterInfos;
        }
        return Collections.emptyList();
    }

    private void setHostsAndName(String vmwareId, ObjectContent object, ClusterInfo clusterInfo) {
        List<HostInfo> hostInfos = new ArrayList<>();
        try {
            Map<String, Object> collect = object.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            clusterInfo.setName(jsonNode.path(VMwareConstants.NAME).asText());
            Iterator<JsonNode> elements = jsonNode.path(VMwareConstants.HOST)
                .path(VMwareConstants.MANAGED_OBJECT_REFERENCE)
                .elements();
            while (elements.hasNext()) {
                JsonNode next = elements.next();
                HostInfo host = hostService.getHost(vmwareId, next.path(VMwareConstants.VALUE).asText());
                hostInfos.add(host);
            }
            clusterInfo.setHosts(hostInfos);
        } catch (JsonProcessingException e) {
            log.error(VMwareConstants.JSON_PROCESSING_EXCEPTION, SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
    }

    @Override
    public List<HostVM> queryHostsAndVms(String vmwareId, String clusterId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<HostVM> hostVMS = new ArrayList<>();
        ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
            .type(VMwareConstants.CLUSTER_COMPUTE_RESOURCE)
            .value(clusterId)
            .build();
        List<String> properties = new ArrayList<>();
        properties.add(VMwareConstants.HOST);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance, managedObjectReference,
            properties);
        // single object
        ObjectContent objectContent = objectContents.get(0);
        Map<String, Object> collect = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            Iterator<JsonNode> elements = jsonNode.path(VMwareConstants.HOST)
                .path(VMwareConstants.MANAGED_OBJECT_REFERENCE)
                .elements();
            while (elements.hasNext()) {
                HostVM hostVM = new HostVM();
                JsonNode host = elements.next();
                String hostId = host.path(VMwareConstants.VALUE).asText();
                // Query host basic information.
                HostInfo host1 = hostService.getHost(vmwareId, hostId);
                // Query virtual machine below the host.
                List<VirtualMachineInfo> virtualMachineBasics = vmService.getVmsByHost(vmwareId, hostId);
                BeanUtils.copyProperties(host1, hostVM);
                hostVM.setVms(virtualMachineBasics);
                hostVMS.add(hostVM);
            }
        } catch (JsonProcessingException e) {
            log.error(VMwareConstants.JSON_PROCESSING_EXCEPTION, SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        return hostVMS;
    }

    private void setDataStore(ClusterBasic clusterBasic, List<ObjectContent> objectContents)
        throws JsonProcessingException {
        long totalDataSize = 0;
        long freeDataSize = 0;
        for (ObjectContent objectContent : objectContents) {
            Map<String, Object> collect = objectContent.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode hostNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            totalDataSize += hostNode.path(VMwareConstants.SUMMARY).path("capacity").asLong();
            freeDataSize += hostNode.path(VMwareConstants.SUMMARY).path("freeSpace").asLong();
        }
        clusterBasic.setTotalDatastoreSpace(DataSize.ofBytes(totalDataSize));
        clusterBasic.setFreeDatastoreSpace(DataSize.ofBytes(freeDataSize));
    }

    private void setCpuAndMemory(ClusterBasic clusterBasic, List<ObjectContent> objectContents)
        throws JsonProcessingException {
        int usedCpu = 0;
        // The unit is MB.
        long usedMemory = 0;
        for (ObjectContent objectContent : objectContents) {
            Map<String, Object> collect = objectContent.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode hostNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            usedCpu += hostNode.path(VMwareConstants.SUMMARY)
                .path(VMwareConstants.QUICK_STATS)
                .path("overallCpuUsage")
                .asLong();
            // free equals total subtracted used
            usedMemory += hostNode.path(VMwareConstants.SUMMARY)
                .path(VMwareConstants.QUICK_STATS)
                .path("overallMemoryUsage")
                .asLong();
        }
        clusterBasic.setUsedCpu(usedCpu);
        // UsedMemory unit was mb
        clusterBasic.setFreeMemory(
            DataSize.ofBytes(clusterBasic.getTotalMemory().toBytes() - DataSize.ofMegabytes(usedMemory).toBytes()));
    }
}
