/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -09-20
 */
public class VmStatus {
    @ApiModelProperty(value = "任务Id", required = true)
    private String taskId;

    @ApiModelProperty(value = "状态", required = true)
    private String status;

    /**
     * Gets task id.
     *
     * @return the task id
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets task id.
     *
     * @param taskId the task id
     * @return the task id
     */
    public VmStatus setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     * @return the status
     */
    public VmStatus setStatus(String status) {
        this.status = status;
        return this;
    }
}
