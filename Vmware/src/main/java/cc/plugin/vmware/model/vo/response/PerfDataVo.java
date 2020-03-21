/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * vcenter性能数据返回
 *
 * @since 2019 -09-19
 */
public class PerfDataVo {
    /**
     * 数据的唯一标识，与host或者vm的id一致
     */
    @ApiModelProperty(value = "数据的唯一标识", example = "c3cad795db454511bd3f51b9eecc1227:host-10", required = true)
    private String urn;
    /**
     * 对应虚拟机或者主机的id
     */
    @ApiModelProperty(value = "虚拟机或者主机的ID", example = "c3cad795db454511bd3f51b9eecc1227:host-10", required = true)
    private String id;
    /**
     * 主机名称
     */
    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    private String hostName;
    /**
     * 主机ip
     */
    @ApiModelProperty(value = "主机IP", example = "192.0.2.0", required = true)
    private String hostIp;
    /**
     * 设备（主机或者虚机）名称
     */
    @ApiModelProperty(value = "主机或者虚机名称", example = "192.0.2.0", required = true)
    private String objectName;
    /**
     * 数据类型，host or vm
     */
    @ApiModelProperty(value = "数据类型", example = "host（主机）,vm（虚拟机）", required = true)
    private String type;
    /**
     * 指标名称
     */
    @ApiModelProperty(value = "指标名称", example = "mem_usage", required = true)
    private String metricName;
    /**
     * 数据单位
     */
    @ApiModelProperty(value = "数据单位", example = "%", required = true)
    private String unit;
    /**
     * 设备ip
     */
    @ApiModelProperty(value = "设备IP", example = "192.0.2.0", required = true)
    private String vmIp;
    /**
     * 数据值
     */
    @ApiModelProperty(value = "数据值", example = "35.25", required = true)
    private BigDecimal value;

    /**
     * 0发送，1更新host性能列
     */
    @ApiModelProperty(value = "操作类型", example = "0发送，1更新host性能列", required = true)
    private Integer dataType = 0;

    /**
     * Gets host ip.
     *
     * @return the host ip
     */
    public String getHostIp() {
        return hostIp;
    }

    /**
     * Sets host ip.
     *
     * @param hostIp the host ip
     */
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    /**
     * Gets urn.
     *
     * @return the urn
     */
    public String getUrn() {
        return urn;
    }

    /**
     * Sets urn.
     *
     * @param urn the urn
     */
    public void setUrn(String urn) {
        this.urn = urn;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets object name.
     *
     * @return the object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets object name.
     *
     * @param objectName the object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

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
     * Gets metric name.
     *
     * @return the metric name
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Sets metric name.
     *
     * @param metricName the metric name
     */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Gets unit.
     *
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets unit.
     *
     * @param unit the unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Gets vm ip.
     *
     * @return the vm ip
     */
    public String getVmIp() {
        return vmIp;
    }

    /**
     * Sets vm ip.
     *
     * @param vmIp the vm ip
     */
    public void setVmIp(String vmIp) {
        this.vmIp = vmIp;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets data type.
     *
     * @return the data type
     */
    public Integer getDataType() {
        return dataType;
    }

    /**
     * Sets data type.
     *
     * @param dataType the data type
     */
    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    /**
     * Gets host name.
     *
     * @return the host name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets host name.
     *
     * @param hostName the host name
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
