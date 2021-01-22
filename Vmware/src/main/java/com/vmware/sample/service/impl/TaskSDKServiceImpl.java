/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.service.TaskService;

import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.TaskInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Task sdk service
 *
 * @since 2020-09-25
 */
@Slf4j
@Service("task-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TaskSDKServiceImpl implements TaskService {
    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public TaskInfo get(String vmwareId, String taskId) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().value(taskId).type("Task").build(),
            Collections.singletonList("info"));
        Object val = objectContents.get(0).getPropSet().get(0).getVal();
        if (val instanceof TaskInfo) {
            return (TaskInfo) val;
        }
        throw new PluginException(RestCodeEnum.SYSTEM_ERROR);
    }
}
