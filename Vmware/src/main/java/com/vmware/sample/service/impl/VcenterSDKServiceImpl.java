/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.model.vcenter.VcenterBasicInfo;
import com.vmware.sample.service.VcenterService;
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
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Vcenter sdk service implement
 *
 * @since 2020-09-16
 */
@Slf4j
@Service("vcenter-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VcenterSDKServiceImpl implements VcenterService {
    private final ObjectMapper objectMapper;

    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public VcenterBasicInfo queryVcenterBasicInfo(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        VcenterBasicInfo vcenterBasicInfo = new VcenterBasicInfo();

        List<ObjectContent> contents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance,
            Arrays.asList(VMwareConstants.HOST_SYSTEM, VMwareConstants.COMPUTE_RESOURCE,
                VMwareConstants.CLUSTER_COMPUTE_RESOURCE, VMwareConstants.DATASTORE_TYPE),
            Collections.singletonList(VMwareConstants.SUMMARY));
        if (CollectionUtils.isNotEmpty(contents)) {
            Map<String, List<ObjectContent>> collect = contents.stream()
                .collect(Collectors.groupingBy(item -> item.getObj().getType()));
            try {
                calculateMemory(vcenterBasicInfo, collect.get(VMwareConstants.HOST_SYSTEM));
                calculateDatastoreSpace(vcenterBasicInfo, collect.get(VMwareConstants.DATASTORE_TYPE));
                List<ObjectContent> objectContents = collect.get(VMwareConstants.COMPUTE_RESOURCE) == null
                    ? new ArrayList<>()
                    : collect.get(VMwareConstants.COMPUTE_RESOURCE);
                List<ObjectContent> clusterObjectContents =
                    collect.get(VMwareConstants.CLUSTER_COMPUTE_RESOURCE) == null
                        ? new ArrayList<>()
                        : collect.get(VMwareConstants.CLUSTER_COMPUTE_RESOURCE);
                objectContents.addAll(clusterObjectContents);
                calculateCpu(vcenterBasicInfo, objectContents);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return vcenterBasicInfo;
    }

    private void calculateCpu(VcenterBasicInfo vcenterBasicInfo, List<ObjectContent> objectContents)
        throws JsonProcessingException {
        long totalCpu = 0;
        for (ObjectContent objectContent : objectContents) {
            Map<String, Object> collect = objectContent.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));

            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            totalCpu += jsonNode.path(VMwareConstants.SUMMARY).path("totalCpu").asLong();
        }
        vcenterBasicInfo.setTotalCpu(totalCpu);
    }

    private void calculateDatastoreSpace(VcenterBasicInfo vcenterBasicInfo, List<ObjectContent> objectContents)
        throws JsonProcessingException {
        long totalDatastoreSpace = 0;
        long freeDatastoreSpace = 0;
        for (ObjectContent objectContent : objectContents) {
            Map<String, Object> collect = objectContent.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            totalDatastoreSpace += jsonNode.path(VMwareConstants.SUMMARY).path("capacity").asLong();
            freeDatastoreSpace += jsonNode.path(VMwareConstants.SUMMARY).path("freeSpace").asLong();
        }
        vcenterBasicInfo.setTotalDatastoreSpace(DataSize.ofBytes(totalDatastoreSpace));
        vcenterBasicInfo.setFreeDatastoreSpace(DataSize.ofBytes(freeDatastoreSpace));
    }

    private void calculateMemory(VcenterBasicInfo vcenterBasicInfo, List<ObjectContent> objectContents)
        throws JsonProcessingException {
        long usedCpu = 0;
        long totalMemory = 0;
        long usedMemory = 0;
        for (ObjectContent objectContent : objectContents) {
            Map<String, Object> collect = objectContent.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            usedCpu += jsonNode.path(VMwareConstants.SUMMARY).path("quickStats").path("overallCpuUsage").asLong();
            totalMemory += jsonNode.path(VMwareConstants.SUMMARY).path("hardware").path("memorySize").asLong();
            usedMemory += jsonNode.path(VMwareConstants.SUMMARY).path("quickStats").path("overallMemoryUsage").asLong();
        }
        vcenterBasicInfo.setFreeMemory(DataSize.ofBytes(totalMemory - DataSize.ofMegabytes(usedMemory).toBytes()));
        vcenterBasicInfo.setUsedMemory(DataSize.ofMegabytes(usedMemory));
        vcenterBasicInfo.setTotalMemory(DataSize.ofBytes(totalMemory));
        vcenterBasicInfo.setUsedCpu(usedCpu);
    }
}
