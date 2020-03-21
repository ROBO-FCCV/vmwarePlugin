/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -10-28
 */
public class VMVo {
    @ApiModelProperty(value = "虚拟机标识", example = "vm-596", required = true)
    private String modId;
    @ApiModelProperty(value = "虚拟机名称", example = "vm-new", required = true)
    private String vmName;
    @ApiModelProperty(value = "虚拟机ID", example = "692ebfc7ff3549ff98a91ab0ffb73842:vm-596", required = true)
    private String vmId;
    @ApiModelProperty(value = "vnc是否可用", example = "false", required = true)
    private Boolean vncenabled;
    @ApiModelProperty(value = "系统名称", example = "WINDOWS", required = true)
    private String osName;
    @ApiModelProperty(value = "状态", example = "", required = false)
    private String status;
    @ApiModelProperty(value = "电源状态", example = "POWERED_OFF", required = true)
    private String powerStatus;
    @ApiModelProperty(value = "虚拟机IP", example = "192.0.2.0", required = false)
    private String ipAddess;
    @ApiModelProperty(value = "虚拟机CPU数量", example = "8", required = true)
    private Integer vcpu;
    @ApiModelProperty(value = "内存", example = "50", required = true)
    private Integer memory;
    @ApiModelProperty(value = "集群标识", example = "domain-c21", required = true)
    private String clusterId;
    @ApiModelProperty(value = "主机标识", example = "host-24", required = true)
    private String hostId;
    @ApiModelProperty(value = "网卡列表", required = true)
    private List<Net> nets;
    @ApiModelProperty(value = "硬盘列表", required = true)
    private List<Disk> disks;
    @ApiModelProperty(value = "集群名称", example = "Cluster", required = true)
    private String clusterName;
    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    private String hostName;
    @ApiModelProperty(value = "操作系统全名", example = "CentOS 4/5 or later (64-bit)", required = true)
    private String osFullName;

    /**
     * Gets mod id.
     *
     * @return the mod id
     */
    public String getModId() {
        return modId;
    }

    /**
     * Sets mod id.
     *
     * @param modId the mod id
     * @return the mod id
     */
    public VMVo setModId(String modId) {
        this.modId = modId;
        return this;
    }

    /**
     * Gets vm name.
     *
     * @return the vm name
     */
    public String getVmName() {
        return vmName;
    }

    /**
     * Sets vm name.
     *
     * @param vmName the vm name
     * @return the vm name
     */
    public VMVo setVmName(String vmName) {
        this.vmName = vmName;
        return this;
    }

    /**
     * Gets vm id.
     *
     * @return the vm id
     */
    public String getVmId() {
        return vmId;
    }

    /**
     * Sets vm id.
     *
     * @param vmId the vm id
     * @return the vm id
     */
    public VMVo setVmId(String vmId) {
        this.vmId = vmId;
        return this;
    }

    /**
     * Gets vncenabled.
     *
     * @return the vncenabled
     */
    public Boolean getVncenabled() {
        return vncenabled;
    }

    /**
     * Sets vncenabled.
     *
     * @param vncenabled the vncenabled
     * @return the vncenabled
     */
    public VMVo setVncenabled(Boolean vncenabled) {
        this.vncenabled = vncenabled;
        return this;
    }

    /**
     * Gets os name.
     *
     * @return the os name
     */
    public String getOsName() {
        return osName;
    }

    /**
     * Sets os name.
     *
     * @param osName the os name
     * @return the os name
     */
    public VMVo setOsName(String osName) {
        this.osName = osName;
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
    public VMVo setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Gets power status.
     *
     * @return the power status
     */
    public String getPowerStatus() {
        return powerStatus;
    }

    /**
     * Sets power status.
     *
     * @param powerStatus the power status
     * @return the power status
     */
    public VMVo setPowerStatus(String powerStatus) {
        this.powerStatus = powerStatus;
        return this;
    }

    /**
     * Gets ip addess.
     *
     * @return the ip addess
     */
    public String getIpAddess() {
        return ipAddess;
    }

    /**
     * Sets ip addess.
     *
     * @param ipAddess the ip addess
     * @return the ip addess
     */
    public VMVo setIpAddess(String ipAddess) {
        this.ipAddess = ipAddess;
        return this;
    }

    /**
     * Gets vcpu.
     *
     * @return the vcpu
     */
    public Integer getVcpu() {
        return vcpu;
    }

    /**
     * Sets vcpu.
     *
     * @param vcpu the vcpu
     * @return the vcpu
     */
    public VMVo setVcpu(Integer vcpu) {
        this.vcpu = vcpu;
        return this;
    }

    /**
     * Gets memory.
     *
     * @return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * Sets memory.
     *
     * @param memory the memory
     * @return the memory
     */
    public VMVo setMemory(Integer memory) {
        this.memory = memory;
        return this;
    }

    /**
     * Gets cluster id.
     *
     * @return the cluster id
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     * Sets cluster id.
     *
     * @param clusterId the cluster id
     * @return the cluster id
     */
    public VMVo setClusterId(String clusterId) {
        this.clusterId = clusterId;
        return this;
    }

    /**
     * Gets host id.
     *
     * @return the host id
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Sets host id.
     *
     * @param hostId the host id
     * @return the host id
     */
    public VMVo setHostId(String hostId) {
        this.hostId = hostId;
        return this;
    }

    /**
     * Gets nets.
     *
     * @return the nets
     */
    public List<Net> getNets() {
        return nets;
    }

    /**
     * Sets nets.
     *
     * @param nets the nets
     * @return the nets
     */
    public VMVo setNets(List<Net> nets) {
        this.nets = nets;
        return this;
    }

    /**
     * Gets cluster name.
     *
     * @return the cluster name
     */
    public String getClusterName() {
        return clusterName;
    }

    /**
     * Sets cluster name.
     *
     * @param clusterName the cluster name
     * @return the cluster name
     */
    public VMVo setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
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
     * @return the host name
     */
    public VMVo setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    /**
     * Gets os full name.
     *
     * @return the os full name
     */
    public String getOsFullName() {
        return osFullName;
    }

    /**
     * Sets os full name.
     *
     * @param osFullName the os full name
     * @return the os full name
     */
    public VMVo setOsFullName(String osFullName) {
        this.osFullName = osFullName;
        return this;
    }

    /**
     * Gets disks.
     *
     * @return the disks
     */
    public List<Disk> getDisks() {
        return disks;
    }

    /**
     * Sets disks.
     *
     * @param disks the disks
     */
    public void setDisks(List<Disk> disks) {
        this.disks = disks;
    }
}
