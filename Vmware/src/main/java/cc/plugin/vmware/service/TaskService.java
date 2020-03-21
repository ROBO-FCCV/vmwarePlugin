/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface TaskService {

    /**
     * Gets task status.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the task status
     * @throws CustomException the custom exception
     */
    String getTaskStatus(String vmwareId, String vmId) throws CustomException;
}
