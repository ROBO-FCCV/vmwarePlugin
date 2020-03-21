/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.host;

import cc.plugin.vmware.model.vo.request.vm.VcenterVm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 请求获取vcenter主机性能数据model
 *
 * @since 2019 -09-19
 */
public class VcenterEsxi {
    /**
     * 主机id
     */
    @ApiModelProperty(value = "主机id", example = "6c0a814", required = true)
    private String id;
    /**
     * 主机名称
     */
    @ApiModelProperty(value = "主机名称", example = "test", required = true)
    private String hostName;
    /**
     * 主机ip
     */
    @ApiModelProperty(value = "主机ip", example = "127.0.0.1", required = true)
    private String hostIp;

    /**
     * 指标集合
     */
    @ApiModelProperty(value = "指标集合", example = "\\{'Temperature','cpu_usage'\\}", required = true)
    private List<String> metrics;
    /**
     * 主机下虚拟机列表
     */
    @ApiModelProperty(value = "主机下虚拟机列表", required = true)
    private List<VcenterVm> vmList;

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
     * Gets metrics.
     *
     * @return the metrics
     */
    public List<String> getMetrics() {
        return metrics;
    }

    /**
     * Sets metrics.
     *
     * @param metrics the metrics
     */
    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    /**
     * Gets vm list.
     *
     * @return the vm list
     */
    public List<VcenterVm> getVmList() {
        return vmList;
    }

    /**
     * Sets vm list.
     *
     * @param vmList the vm list
     */
    public void setVmList(List<VcenterVm> vmList) {
        this.vmList = vmList;
    }
}
