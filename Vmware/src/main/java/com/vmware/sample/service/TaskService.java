/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.vim25.TaskInfo;

/**
 * Task service
 *
 * @since 2020-09-24
 */
public interface TaskService {
    /**
     * Get task
     *
     * @param vmwareId vmware id
     * @param taskId task id
     * @return task
     */
    TaskInfo get(String vmwareId, String taskId);
}
