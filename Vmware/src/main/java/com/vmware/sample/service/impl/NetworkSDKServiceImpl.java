/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.factory.TraversalSpecBuilder;
import com.vmware.sample.model.network.NetworkInfo;
import com.vmware.sample.service.NetworkService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ObjectContent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Network sdk service implement
 *
 * @since 2020-09-22
 */
@Slf4j
@Service("network-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class NetworkSDKServiceImpl implements NetworkService {
    private final ObjectMapper objectMapper;

    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public List<NetworkInfo> list(String vmwareId, String dataCenterId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance()
                .type(VMwareConstants.DATACENTER_TYPE)
                .value(dataCenterId)
                .build(), Collections.singletonList(TraversalSpecBuilder.getInstance()
                .name("DatacenterToNetwork")
                .path("network")
                .type(VMwareConstants.DATACENTER_TYPE)
                .build()), Collections.singletonList(VMwareConstants.NETWORK_TYPE),
            Collections.singletonList(VMwareConstants.NAME));
        if (CollectionUtils.isNotEmpty(objectContents)) {
            return getNetworkInfos(objectContents);
        }
        return Collections.emptyList();
    }

    @Override
    public List<NetworkInfo> listByHost(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> retrieveResult = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
            Collections.singletonList(TraversalSpecBuilder.getInstance()
                .name("HostToNetwork")
                .path("network")
                .type(VMwareConstants.HOST_SYSTEM)
                .build()), Collections.singletonList(VMwareConstants.NETWORK_TYPE),
            Collections.singletonList(VMwareConstants.NAME));
        if (retrieveResult != null) {
            return getNetworkInfos(retrieveResult);
        }
        return Collections.emptyList();
    }

    @Override
    public List<NetworkInfo> listByDomain(String vmwareId, String domainId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.COMPUTE_RESOURCE).value(domainId).build(),
            Collections.singletonList(TraversalSpecBuilder.getInstance()
                .name("ComputeResourceToNetwork")
                .path("network")
                .type(VMwareConstants.COMPUTE_RESOURCE)
                .build()), Collections.singletonList(VMwareConstants.NETWORK_TYPE),
            Collections.singletonList(VMwareConstants.NAME));
        if (objectContents != null) {
            return getNetworkInfos(objectContents);
        }
        return Collections.emptyList();
    }

    private List<NetworkInfo> getNetworkInfos(List<ObjectContent> retrieveResult) {
        List<NetworkInfo> networkInfos = new ArrayList<>();
        for (ObjectContent object : retrieveResult.stream()
            .filter(item -> item.getObj().getType().equals(VMwareConstants.NETWORK_TYPE))
            .collect(Collectors.toList())) {
            NetworkInfo networkInfo = new NetworkInfo();
            Map<String, Object> collect = object.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            try {
                JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
                networkInfo.setName(jsonNode.path("name").asText());
                networkInfo.setVlanId(object.getObj().getValue());
                networkInfos.add(networkInfo);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return networkInfos;
    }
}
