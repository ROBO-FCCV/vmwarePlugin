/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.service.TaskService;

import com.vmware.vim25.TaskInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Task api service
 *
 * @since 2020-09-24
 */
@Slf4j
@Service("task-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TaskAPIServiceImpl implements TaskService {

    @Override
    public TaskInfo get(String vmwareId, String taskId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
