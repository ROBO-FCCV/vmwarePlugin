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
public class TaskVmVo {
    @ApiModelProperty(value = "类型", example = "Task", required = true)
    private String type;

    @ApiModelProperty(value = "任务名称", example = "task-9206", required = true)
    private String taskUri;

    @ApiModelProperty(value = "资源ID", example = "vm-1455", required = true)
    private String resourceId;

    @ApiModelProperty(value = "系统类型", example = "LINUX", required = true)
    private String osType;

    @ApiModelProperty(value = "模型ID", example = "vm-1381", required = true)
    private String templateId;

    @ApiModelProperty(value = "任务状态", example = "RUNNING", required = true)
    private String status;

    @ApiModelProperty(value = "任务ID", example = "task-9206", required = true)
    private String taskId;

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets task uri.
     *
     * @return the task uri
     */
    public String getTaskUri() {
        return taskUri;
    }

    /**
     * Sets task uri.
     *
     * @param taskUri the task uri
     */
    public void setTaskUri(String taskUri) {
        this.taskUri = taskUri;
    }

    /**
     * Gets os type.
     *
     * @return the os type
     */
    public String getOsType() {
        return osType;
    }

    /**
     * Sets os type.
     *
     * @param osType the os type
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * Gets template id.
     *
     * @return the template id
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * Sets template id.
     *
     * @param templateId the template id
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
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
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets resource id.
     *
     * @return the resource id
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Sets resource id.
     *
     * @param resourceId the resource id
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

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
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
