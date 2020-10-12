/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * Vm查询返回结果
 *
 * @since 2019 -09-10
 */
public class VncVo {
    @ApiModelProperty(value = "端口号", example = "0000", required = true)
    private String vncPort;
    @ApiModelProperty(value = "主机", example = "192.0.2.0", required = true)
    private String vncHost;
    @ApiModelProperty(value = "密码", example = "", required = true)
    private String vncHelso;
    @ApiModelProperty(value = "虚拟机名称", example = "vm-new", required = true)
    private String vmName;
    @ApiModelProperty(value = "系统类型", example = "WINDOWS", required = true)
    private String osType;
    @ApiModelProperty(value = "版本号", example = "1.1", required = true)
    private String version;
    @ApiModelProperty(value = "票据", example = "1.1", required = true)
    private String ticket;
    @ApiModelProperty(value = "是否允许vnc登录", example = "true", required = true)
    private boolean vncEnabled;

    public boolean isVncEnabled() {
        return vncEnabled;
    }

    public void setVncEnabled(boolean vncEnabled) {
        this.vncEnabled = vncEnabled;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
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
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

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
}
