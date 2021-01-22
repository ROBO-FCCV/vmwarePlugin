/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.model.cluster.ClusterInfo;
import com.vmware.sample.model.datacenter.DataCenterInfo;
import com.vmware.sample.model.host.HostInfo;
import com.vmware.sample.model.host.SingleHostInfo;
import com.vmware.sample.service.ClusterService;
import com.vmware.sample.service.DataCenterService;
import com.vmware.sample.service.HostService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ObjectContent;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Datacenter sdk service
 *
 * @since 2020-10-10
 */
@Slf4j
@Service("datacenter-sdk-service")
public class DataCenterSDKServiceImpl implements DataCenterService {
    private final ObjectMapper objectMapper;
    private final ClusterService clusterService;
    private final HostService hostService;
    private final VmwareSDKClient vmwareSDKClient;

    public DataCenterSDKServiceImpl(VmwareSDKClient vmwareSDKClient, ObjectMapper objectMapper,
        @Qualifier("cluster-sdk-service") ClusterService clusterService,
        @Qualifier("host-sdk-service") HostService hostService) {
        this.vmwareSDKClient = vmwareSDKClient;
        this.objectMapper = objectMapper;
        this.clusterService = clusterService;
        this.hostService = hostService;
    }

    @Override
    public List<DataCenterInfo> dataCenters(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance,
            Collections.singletonList(VMwareConstants.DATACENTER_TYPE),
            Arrays.asList("hostFolder", VMwareConstants.NAME, VMwareConstants.OVERALL_STATUS));
        List<DataCenterInfo> dataCenterInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(objectContents)) {
            for (ObjectContent object : objectContents) {
                DataCenterInfo dataCenterInfo = new DataCenterInfo();
                dataCenterInfo.setId(object.getObj().getValue());
                Map<String, Object> collect = object.getPropSet()
                    .stream()
                    .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
                retrieveName(dataCenterInfo, collect);
                retrieveHosts(dataCenterInfo, collect, sdkInstance, vmwareId);
                dataCenterInfos.add(dataCenterInfo);
            }
        }
        return dataCenterInfos;
    }

    private void retrieveHosts(DataCenterInfo dataCenterInfo, Map<String, Object> collect, VMwareSDK sdkInstance,
        String vmwareId) {
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            List<ObjectContent> childEntity = vmwareSDKClient.retrieveProperties(sdkInstance,
                ManagedObjectReferenceBuilder.getInstance()
                    .type(jsonNode.path("hostFolder").path(VMwareConstants.TYPE).asText())
                    .value(jsonNode.path("hostFolder").path(VMwareConstants.VALUE).asText())
                    .build(), Collections.singletonList("childEntity"));
            Map<String, Object> items = childEntity.get(0)
                .getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode itemNodes = objectMapper.readTree(objectMapper.writeValueAsString(items));
            Iterator<JsonNode> jsonNodeIterator = itemNodes.path("childEntity")
                .path(VMwareConstants.MANAGED_OBJECT_REFERENCE)
                .elements();
            List<ClusterInfo> clusterInfos = new ArrayList<>();
            List<SingleHostInfo> hostInfos = new ArrayList<>();
            while (jsonNodeIterator.hasNext()) {
                JsonNode item = jsonNodeIterator.next();
                String type = item.path(VMwareConstants.TYPE).asText();
                String value = item.path(VMwareConstants.VALUE).asText();
                if (VMwareConstants.CLUSTER_COMPUTE_RESOURCE.equals(type)) {
                    ClusterInfo clusterInfo = clusterService.getClusterInfo(vmwareId, value);
                    clusterInfos.add(clusterInfo);
                } else {
                    ClusterInfo clusterInfo = clusterService.getClusterInfo(vmwareId, value);
                    SingleHostInfo hostInfo = new SingleHostInfo();
                    HostInfo singleHost = clusterInfo.getHosts().get(0);
                    hostInfo.setInMaintenanceMode(singleHost.isInMaintenanceMode());
                    hostInfo.setHostInfo(singleHost);
                    hostInfo.setIpAddress(singleHost.getIpAddress());
                    hostInfo.setName(clusterInfo.getName());
                    hostInfo.setMoId(clusterInfo.getMoId());
                    hostInfo.setStatus(singleHost.getStatus());
                    hostInfos.add(hostInfo);
                }
            }
            dataCenterInfo.setClusterInfos(clusterInfos);
            dataCenterInfo.setHostInfos(hostInfos);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
    }

    private void retrieveName(DataCenterInfo dataCenterInfo, Map<String, Object> collect) {
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            dataCenterInfo.setName(jsonNode.path("name").asText());
            dataCenterInfo.setStatus(jsonNode.path(VMwareConstants.OVERALL_STATUS).asText());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
    }
}
