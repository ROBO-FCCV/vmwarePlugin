/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;

import com.vmware.vim25.TaskInfo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Task controller
 *
 * @since 2020-09-23
 */
@Validated
public interface TaskController {
    @GetMapping("/{vmwareId}/tasks/{taskId}")
    RestResult<TaskInfo> get(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "task-\\d+") String taskId);
}
