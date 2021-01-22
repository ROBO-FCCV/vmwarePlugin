/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.TaskController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.service.TaskService;

import com.vmware.vim25.TaskInfo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task sdk controller
 *
 * @since 2020-09-24
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/sdk")
public class TaskSDKController implements TaskController {
    private final TaskService taskService;

    public TaskSDKController(@Qualifier("task-sdk-service") TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public RestResult<TaskInfo> get(String vmwareId, String taskId) {
        return RestResult.success(taskService.get(vmwareId, taskId));
    }
}
