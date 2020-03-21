/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Vm查询返回结果
 *
 * @since 2019 -09-10
 */
public class VmInfo {

    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    private String hostName;
    @ApiModelProperty(value = "虚拟机名称", example = "vm-new", required = true)
    private String vmName;
    @ApiModelProperty(value = "虚拟机ID", example = "vm-278", required = true)
    private String vmId;
    @ApiModelProperty(value = "集群ID", example = "domain-c112", required = true)
    private String clusterMoId;
    @ApiModelProperty(value = "虚拟机IP", example = "192.0.2.0", required = true)
    private String ip;
    @ApiModelProperty(value = "虚拟机ID", example = "vm-278", required = true)
    private String moId;
    @ApiModelProperty(value = "内存容量(MB)", example = "24576", required = true)
    private int memoryMB;
    @ApiModelProperty(value = "虚拟机状态:POWERED_ON,POWERED_OFF", example = "POWERED_ON", required = true)
    private String vmStatus;
    @ApiModelProperty(value = "主机ID", example = "host-261", required = true)
    private String hostMoId;
    @ApiModelProperty(value = "集群名称", example = "cluster_name", required = true)
    private String clusterName;
    @ApiModelProperty(value = "Socket数量", example = "1", required = true)
    private int numSocket;
    @ApiModelProperty(value = "CPU数量", example = "12", required = true)
    private int numCpu;
    @ApiModelProperty(value = "系统名称", example = "WINDOWS", required = true)
    private String osName;
    @ApiModelProperty(value = "网络信息", required = true)
    private List<Network> networks;
    @ApiModelProperty(value = "硬盘信息", required = true)
    private List<Disk> disks;
    @ApiModelProperty(value = "网卡列表", required = true)
    private List<Net> nets;

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
     */
    public void setVmName(String vmName) {
        this.vmName = vmName;
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
     */
    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    /**
     * Gets cluster mo id.
     *
     * @return the cluster mo id
     */
    public String getClusterMoId() {
        return clusterMoId;
    }

    /**
     * Sets cluster mo id.
     *
     * @param clusterMoId the cluster mo id
     */
    public void setClusterMoId(String clusterMoId) {
        this.clusterMoId = clusterMoId;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets mo id.
     *
     * @return the mo id
     */
    public String getMoId() {
        return moId;
    }

    /**
     * Sets mo id.
     *
     * @param moId the mo id
     */
    public void setMoId(String moId) {
        this.moId = moId;
    }

    /**
     * Gets memory mb.
     *
     * @return the memory mb
     */
    public int getMemoryMB() {
        return memoryMB;
    }

    /**
     * Sets memory mb.
     *
     * @param memoryMB the memory mb
     */
    public void setMemoryMB(int memoryMB) {
        this.memoryMB = memoryMB;
    }

    /**
     * Gets vm status.
     *
     * @return the vm status
     */
    public String getVmStatus() {
        return vmStatus;
    }

    /**
     * Sets vm status.
     *
     * @param vmStatus the vm status
     */
    public void setVmStatus(String vmStatus) {
        this.vmStatus = vmStatus;
    }

    /**
     * Gets host mo id.
     *
     * @return the host mo id
     */
    public String getHostMoId() {
        return hostMoId;
    }

    /**
     * Sets host mo id.
     *
     * @param hostMoId the host mo id
     */
    public void setHostMoId(String hostMoId) {
        this.hostMoId = hostMoId;
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
     */
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * Gets num socket.
     *
     * @return the num socket
     */
    public int getNumSocket() {
        return numSocket;
    }

    /**
     * Sets num socket.
     *
     * @param numSocket the num socket
     */
    public void setNumSocket(int numSocket) {
        this.numSocket = numSocket;
    }

    /**
     * Gets num cpu.
     *
     * @return the num cpu
     */
    public int getNumCpu() {
        return numCpu;
    }

    /**
     * Sets num cpu.
     *
     * @param numCpu the num cpu
     */
    public void setNumCpu(int numCpu) {
        this.numCpu = numCpu;
    }

    /**
     * Gets networks.
     *
     * @return the networks
     */
    public List<Network> getNetworks() {
        return networks;
    }

    /**
     * Sets networks.
     *
     * @param networks the networks
     */
    public void setNetworks(List<Network> networks) {
        this.networks = networks;
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
     */
    public void setNets(List<Net> nets) {
        this.nets = nets;
    }
}
