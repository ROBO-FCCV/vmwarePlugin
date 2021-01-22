/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.factory.TraversalSpecBuilder;
import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.datastore.DatastoreCreate;
import com.vmware.sample.service.DatastoreService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.HostConfigFaultFaultMsg;
import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NotFoundFaultMsg;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ResourceInUseFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VmfsDatastoreCreateSpec;
import com.vmware.vim25.VmfsDatastoreOption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Datastore sdk service implement
 *
 * @since 2020-09-21
 */
@Slf4j
@Service("datastore-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DatastoreSDKServiceImpl implements DatastoreService {
    private final ObjectMapper objectMapper;

    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public ManagedObjectReference createDatastore(String vmwareId, String hostId, DatastoreCreate datastoreCreate) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> configManager = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
            Collections.singletonList(VMwareConstants.CONFIG_MANAGER));
        // Single object
        ObjectContent objectContent = configManager.get(0);
        Map<String, Object> collect = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            String type = jsonNode.path(VMwareConstants.CONFIG_MANAGER)
                .path(VMwareConstants.DATASTORE_SYSTEM)
                .path(VMwareConstants.TYPE)
                .asText();
            String value = jsonNode.path(VMwareConstants.CONFIG_MANAGER)
                .path(VMwareConstants.DATASTORE_SYSTEM)
                .path(VMwareConstants.VALUE)
                .asText();
            ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
                .value(value)
                .type(type)
                .build();
            List<HostScsiDisk> hostScsiDisks = sdkInstance.getVimPort()
                .queryAvailableDisksForVmfs(managedObjectReference, null);
            Optional<HostScsiDisk> any = hostScsiDisks.stream()
                .filter(item -> item.getKey().equalsIgnoreCase(datastoreCreate.getKey()))
                .findAny();
            if (any.isPresent()) {
                HostScsiDisk hostScsiDisk = any.get();
                List<VmfsDatastoreOption> vmfsDatastoreOptions = sdkInstance.getVimPort()
                    .queryVmfsDatastoreCreateOptions(managedObjectReference, hostScsiDisk.getDevicePath(), null);
                VmfsDatastoreOption vmfsDatastoreOption = vmfsDatastoreOptions.get(0);
                VmfsDatastoreCreateSpec vmfsDatastoreCreateSpec = new VmfsDatastoreCreateSpec();
                BeanUtils.copyProperties(vmfsDatastoreOption.getSpec(), vmfsDatastoreCreateSpec);
                vmfsDatastoreCreateSpec.getVmfs().setVolumeName(datastoreCreate.getDatastoreName());
                vmfsDatastoreCreateSpec.getVmfs().setMajorVersion(5);
                return sdkInstance.getVimPort().createVmfsDatastore(managedObjectReference, vmfsDatastoreCreateSpec);
            }
            log.error("The key {} was error.Can't find any host disk.", datastoreCreate.getKey());
        } catch (JsonProcessingException | RuntimeFaultFaultMsg | HostConfigFaultFaultMsg | NotFoundFaultMsg e) {
            log.error("Create datastore failed.", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (DuplicateNameFaultMsg e) {
            log.error("Create datastore failed: DuplicateNameFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.CREATE_DATASTORE_ERROR);
    }

    @Override
    public String delDatastore(String vmwareId, String hostId, String datastoreId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
            Collections.singletonList(VMwareConstants.CONFIG_MANAGER));
        // Single object
        ObjectContent objectContent = objectContents.get(0);
        Map<String, Object> collect = objectContent.getPropSet()
            .stream()
            .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
            String type = jsonNode.path(VMwareConstants.CONFIG_MANAGER)
                .path(VMwareConstants.DATASTORE_SYSTEM)
                .path(VMwareConstants.TYPE)
                .asText();
            String value = jsonNode.path(VMwareConstants.CONFIG_MANAGER)
                .path(VMwareConstants.DATASTORE_SYSTEM)
                .path(VMwareConstants.VALUE)
                .asText();
            ManagedObjectReference managedObjectReference = ManagedObjectReferenceBuilder.getInstance()
                .value(value)
                .type(type)
                .build();
            sdkInstance.getVimPort()
                .removeDatastore(managedObjectReference, ManagedObjectReferenceBuilder.getInstance()
                    .type(VMwareConstants.DATASTORE_TYPE)
                    .value(datastoreId)
                    .build());
            return RestCodeEnum.SUCCESS.getMsg();
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
        } catch (NotFoundFaultMsg | HostConfigFaultFaultMsg | ResourceInUseFaultMsg | RuntimeFaultFaultMsg e) {
            log.error("Delete datastore error.", SensitiveExceptionUtils.hideSensitiveInfo(e));
        }
        throw new PluginException(RestCodeEnum.DELETE_DATASTORE_ERROR);
    }

    @Override
    public List<DatastoreBasic> datastore(String vmwareId, String hostId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        TraversalSpec traversalSpec = TraversalSpecBuilder.getInstance()
            .type(VMwareConstants.HOST_SYSTEM)
            .name("HostDatastore")
            .path(VMwareConstants.DATASTORE)
            .build();
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.HOST_SYSTEM).value(hostId).build(),
            Collections.singletonList(traversalSpec), Collections.singletonList(VMwareConstants.DATASTORE_TYPE),
            Arrays.asList(VMwareConstants.SUMMARY, VMwareConstants.NAME));
        if (CollectionUtils.isNotEmpty(objectContents)) {
            return getDatastoreBasics(objectContents);
        }
        return Collections.emptyList();
    }

    @Override
    public List<DatastoreBasic> datastore(String vmwareId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectWithProperties(sdkInstance,
            Collections.singletonList(VMwareConstants.DATASTORE_TYPE),
            Arrays.asList(VMwareConstants.SUMMARY, VMwareConstants.NAME));
        if (objectContents != null) {
            return getDatastoreBasics(objectContents);
        }
        return Collections.emptyList();
    }

    @Override
    public List<DatastoreBasic> clusterDataStore(String vmwareId, String domainId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        TraversalSpec traversalSpec = TraversalSpecBuilder.getInstance()
            .type(VMwareConstants.COMPUTE_RESOURCE)
            .name("ComputeDatastore")
            .path(VMwareConstants.DATASTORE)
            .build();
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveObjectsWithPropsBelowManagedObjRef(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMwareConstants.COMPUTE_RESOURCE).value(domainId).build(),
            Collections.singletonList(traversalSpec), Collections.singletonList(VMwareConstants.DATASTORE_TYPE),
            Arrays.asList(VMwareConstants.SUMMARY, VMwareConstants.NAME));
        if (objectContents != null) {
            return getDatastoreBasics(objectContents);
        }
        return Collections.emptyList();
    }

    private List<DatastoreBasic> getDatastoreBasics(List<ObjectContent> retrieveResult) {
        List<DatastoreBasic> datastoreBasics = new ArrayList<>();
        for (ObjectContent object : retrieveResult) {
            Map<String, Object> collect = object.getPropSet()
                .stream()
                .collect(Collectors.toMap(DynamicProperty::getName, DynamicProperty::getVal));
            DatastoreBasic datastoreBasic = new DatastoreBasic();
            try {
                JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(collect));
                datastoreBasic.setName(jsonNode.path(VMwareConstants.NAME).asText());
                datastoreBasic.setType(jsonNode.path(VMwareConstants.SUMMARY).path(VMwareConstants.TYPE).asText());
                datastoreBasic.setModId(object.getObj().getValue());
                datastoreBasic.setMaintenanceMode(
                    jsonNode.path(VMwareConstants.SUMMARY).path("maintenanceMode").asBoolean());
                datastoreBasic.setMultipleHostAccess(
                    jsonNode.path(VMwareConstants.SUMMARY).path("multipleHostAccess").asBoolean());
                datastoreBasic.setCapacity(
                    DataSize.ofBytes(jsonNode.path(VMwareConstants.SUMMARY).path("capacity").asLong()));
                datastoreBasic.setFreeSpace(
                    DataSize.ofBytes(jsonNode.path(VMwareConstants.SUMMARY).path("freeSpace").asLong()));
                datastoreBasic.setUrl(jsonNode.path(VMwareConstants.SUMMARY).path("url").asText());
                datastoreBasic.setAccessible(jsonNode.path(VMwareConstants.SUMMARY).path("accessible").asBoolean());
                datastoreBasic.setUncommitted(
                    DataSize.ofBytes(jsonNode.path(VMwareConstants.SUMMARY).path("uncommitted").asLong()));
                datastoreBasic.setUsedSpace(DataSize.ofBytes(
                    jsonNode.path(VMwareConstants.SUMMARY).path("capacity").asLong() - jsonNode.path(
                        VMwareConstants.SUMMARY).path("freeSpace").asLong()));
                datastoreBasics.add(datastoreBasic);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException", SensitiveExceptionUtils.hideSensitiveInfo(e));
            }
        }
        return datastoreBasics;
    }
}
