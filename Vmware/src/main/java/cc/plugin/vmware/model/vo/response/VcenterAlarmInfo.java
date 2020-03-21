/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import com.vmware.vim25.ManagedEntityStatus;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 从vcenter同步的告警模型
 *
 * @since 2019 -09-23
 */
public class VcenterAlarmInfo {
    @ApiModelProperty(value = "告警描述", example = "vm_cpu_test", required = true)
    private String description;
    // 告警名称
    @ApiModelProperty(value = "告警名称", example = "vm_cpu", required = true)
    private String alarmName;
    // 告警id
    @ApiModelProperty(value = "告警标识", example = "alarm-209.vm-12", required = true)
    private String alarmId;
    // 是否被确认
    @ApiModelProperty(value = "是否被确认", example = "false", required = true)
    private Boolean acknowledged;
    // 确认时间
    @ApiModelProperty(value = "确认时间", example = "2019-10-25T18:29:50.315+0000", required = true)
    private Date acknowledgedTime;
    // 确认者
    @ApiModelProperty(value = "确认者", example = "xxx", required = true)
    private String acknowledgedByUser;
    // 对象
    @ApiModelProperty(value = "告警对象名称", example = "vCenter6.5u1", required = true)
    private String ObjectName;
    // 状态
    @ApiModelProperty(value = "状态", example = "YELLOW", required = true)
    private ManagedEntityStatus overallStatus;
    // 触发时间
    @ApiModelProperty(value = "触发时间", example = "2019-10-25T18:29:50.315+0000", required = true)
    private Date time;

    @Override
    public String toString() {
        return "VcenterAlarmInfo{" + "description='" + description + '\'' + ", alarmName='" + alarmName + '\''
            + ", alarmId='" + alarmId + '\'' + ", acknowledged=" + acknowledged + ", acknowledgedTime="
            + acknowledgedTime + ", acknowledgedByUser='" + acknowledgedByUser + '\'' + ", ObjectName='" + ObjectName
            + '\'' + ", overallStatus=" + overallStatus + ", time=" + time + '}';
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets alarm name.
     *
     * @return the alarm name
     */
    public String getAlarmName() {
        return alarmName;
    }

    /**
     * Sets alarm name.
     *
     * @param alarmName the alarm name
     */
    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    /**
     * Gets alarm id.
     *
     * @return the alarm id
     */
    public String getAlarmId() {
        return alarmId;
    }

    /**
     * Sets alarm id.
     *
     * @param alarmId the alarm id
     */
    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    /**
     * Gets acknowledged.
     *
     * @return the acknowledged
     */
    public Boolean getAcknowledged() {
        return acknowledged;
    }

    /**
     * Sets acknowledged.
     *
     * @param acknowledged the acknowledged
     */
    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    /**
     * Gets acknowledged time.
     *
     * @return the acknowledged time
     */
    public Date getAcknowledgedTime() {
        return acknowledgedTime;
    }

    /**
     * Sets acknowledged time.
     *
     * @param acknowledgedTime the acknowledged time
     */
    public void setAcknowledgedTime(Date acknowledgedTime) {
        this.acknowledgedTime = acknowledgedTime;
    }

    /**
     * Gets acknowledged by user.
     *
     * @return the acknowledged by user
     */
    public String getAcknowledgedByUser() {
        return acknowledgedByUser;
    }

    /**
     * Sets acknowledged by user.
     *
     * @param acknowledgedByUser the acknowledged by user
     */
    public void setAcknowledgedByUser(String acknowledgedByUser) {
        this.acknowledgedByUser = acknowledgedByUser;
    }

    /**
     * Gets object name.
     *
     * @return the object name
     */
    public String getObjectName() {
        return ObjectName;
    }

    /**
     * Sets object name.
     *
     * @param objectName the object name
     */
    public void setObjectName(String objectName) {
        ObjectName = objectName;
    }

    /**
     * Gets overall status.
     *
     * @return the overall status
     */
    public ManagedEntityStatus getOverallStatus() {
        return overallStatus;
    }

    /**
     * Sets overall status.
     *
     * @param overallStatus the overall status
     */
    public void setOverallStatus(ManagedEntityStatus overallStatus) {
        this.overallStatus = overallStatus;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(Date time) {
        this.time = time;
    }
}
