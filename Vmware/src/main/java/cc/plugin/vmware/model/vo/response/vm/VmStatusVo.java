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
public class VmStatusVo {
    @ApiModelProperty(value = "端口号", example = "0000", required = true)
    private String vncPort;

    @ApiModelProperty(value = "主机", example = "192.0.2.0", required = true)
    private String vncHost;

    @ApiModelProperty(value = "密码", example = "", required = true)
    private String vncHelso;

    @ApiModelProperty(value = "加密密码", example = "", required = true)
    private String vncEncHelso;

    @ApiModelProperty(value = "虚拟机名称", example = "vm-new", required = true)
    private String vmName;

    @ApiModelProperty(value = "系统类型", example = "WINDOWS", required = true)
    private String osType;

    @ApiModelProperty(value = "虚拟机状态", example = "notRunning", required = true)
    private String status;

    @ApiModelProperty(value = "虚拟机IP", example = "192.0.2.0", required = true)
    private String ip;

    /**
     * Gets vnc port.
     *
     * @return the vnc port
     */
    public String getVncPort() {
        return vncPort;
    }

    /**
     * Sets vnc port.
     *
     * @param vncPort the vnc port
     */
    public void setVncPort(String vncPort) {
        this.vncPort = vncPort;
    }

    /**
     * Gets vnc host.
     *
     * @return the vnc host
     */
    public String getVncHost() {
        return vncHost;
    }

    /**
     * Sets vnc host.
     *
     * @param vncHost the vnc host
     */
    public void setVncHost(String vncHost) {
        this.vncHost = vncHost;
    }

    /**
     * Gets vnc helso.
     *
     * @return the vnc helso
     */
    public String getVncHelso() {
        return vncHelso;
    }

    /**
     * Sets vnc helso.
     *
     * @param vncHelso the vnc helso
     */
    public void setVncHelso(String vncHelso) {
        this.vncHelso = vncHelso;
    }

    /**
     * Gets vnc enc helso.
     *
     * @return the vnc enc helso
     */
    public String getVncEncHelso() {
        return vncEncHelso;
    }

    /**
     * Sets vnc enc helso.
     *
     * @param vncEncHelso the vnc enc helso
     */
    public void setVncEncHelso(String vncEncHelso) {
        this.vncEncHelso = vncEncHelso;
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
}
