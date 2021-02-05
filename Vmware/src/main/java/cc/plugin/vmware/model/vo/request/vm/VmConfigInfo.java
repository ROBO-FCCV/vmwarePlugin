/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 创建虚拟机实体类
 *
 * @since 2019 -09-09
 */
public class VmConfigInfo {
    /**
     * 虚拟机名称
     */
    @ApiModelProperty(value = "虚拟机名称", example = "vm-2", required = true)
    @NotEmpty
    private String vmName;

    /**
     * 主机名称
     */
    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    @NotEmpty
    private String hostName;

    /**
     * 主机名称
     */
    @ApiModelProperty(value = "主机Urn", example = "192.0.2.0", required = false)
    private String hostUrn;

    /**
     * 主机名称
     */
    @ApiModelProperty(value = "主机Ip", example = "192.0.2.0", required = false)
    private String hostIp;

    /**
     * 数据中心名称
     */
    @ApiModelProperty(value = "数据中心名称", example = "datacenter", required = true)
    @NotEmpty
    private String datacenterName;

    /**
     * 机器密码
     */
    @ApiModelProperty(value = "机器密码", example = "abc123", required = false)
    private String ospassword;

    /**
     * 存储id
     */
    @ApiModelProperty(value = "存储id", example = "ds-123", required = false)
    private String datastoreId;

    /**
     * 存储名称
     */
    @ApiModelProperty(value = "存储名称", example = "datastore-1", required = true)
    @NotEmpty
    private String datastoreName;

    @ApiModelProperty(value = "cpu", required = true)
    private Cpu cpu;

    @ApiModelProperty(value = "Memory", required = true)
    private Memory memory;

    /**
     * 虚拟网络
     */
    @ApiModelProperty(value = "端口信息", required = true)
    @Valid
    private List<Network> nics;

    @ApiModelProperty(value = "硬盘信息", required = true)
    private List<Disk> disks;

    /**
     * 系统类型
     */
    @ApiModelProperty(value = "系统类型", example = "WINDOWS", required = true)
    @NotEmpty
    private String osType;

    @ApiModelProperty(value = "系统类型", example = "winVistaGuest", required = false)
    private String osVersion;

    @ApiModelProperty(value = "lunNames", example = "lun-123", required = false)
    private List<String> lunNames;

    /**
     * 客户操作系统的标志符
     */
    @ApiModelProperty(value = "客户操作系统的标志符", example = "guest2", required = true)
    @NotEmpty
    private String osFullName;

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
     * Gets host urn.
     *
     * @return the host urn
     */
    public String getHostUrn() {
        return hostUrn;
    }

    /**
     * Sets host urn.
     *
     * @param hostUrn the host urn
     */
    public void setHostUrn(String hostUrn) {
        this.hostUrn = hostUrn;
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
     * Gets datacenter name.
     *
     * @return the datacenter name
     */
    public String getDatacenterName() {
        return datacenterName;
    }

    /**
     * Sets datacenter name.
     *
     * @param datacenterName the datacenter name
     */
    public void setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
    }

    /**
     * Gets ospassword.
     *
     * @return the ospassword
     */
    public String getOspassword() {
        return ospassword;
    }

    /**
     * Sets ospassword.
     *
     * @param ospassword the ospassword
     */
    public void setOspassword(String ospassword) {
        this.ospassword = ospassword;
    }

    /**
     * Gets datastore id.
     *
     * @return the datastore id
     */
    public String getDatastoreId() {
        return datastoreId;
    }

    /**
     * Sets datastore id.
     *
     * @param datastoreId the datastore id
     */
    public void setDatastoreId(String datastoreId) {
        this.datastoreId = datastoreId;
    }

    /**
     * Gets datastore name.
     *
     * @return the datastore name
     */
    public String getDatastoreName() {
        return datastoreName;
    }

    /**
     * Sets datastore name.
     *
     * @param datastoreName the datastore name
     */
    public void setDatastoreName(String datastoreName) {
        this.datastoreName = datastoreName;
    }

    /**
     * Gets cpu.
     *
     * @return the cpu
     */
    public Cpu getCpu() {
        return cpu;
    }

    /**
     * Sets cpu.
     *
     * @param cpu the cpu
     */
    public void setCpu(Cpu cpu) {
        this.cpu = cpu;
    }

    /**
     * Gets memory.
     *
     * @return the memory
     */
    public Memory getMemory() {
        return memory;
    }

    /**
     * Sets memory.
     *
     * @param memory the memory
     */
    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    /**
     * Gets nics.
     *
     * @return the nics
     */
    public List<Network> getNics() {
        return nics;
    }

    /**
     * Sets nics.
     *
     * @param nics the nics
     */
    public void setNics(List<Network> nics) {
        this.nics = nics;
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
     * Gets os version.
     *
     * @return the os version
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Sets os version.
     *
     * @param osVersion the os version
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * Gets lun names.
     *
     * @return the lun names
     */
    public List<String> getLunNames() {
        return lunNames;
    }

    /**
     * Sets lun names.
     *
     * @param lunNames the lun names
     */
    public void setLunNames(List<String> lunNames) {
        this.lunNames = lunNames;
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
     */
    public void setOsFullName(String osFullName) {
        this.osFullName = osFullName;
    }

}