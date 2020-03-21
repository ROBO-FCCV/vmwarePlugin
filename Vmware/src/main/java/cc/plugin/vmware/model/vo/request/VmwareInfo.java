/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -09-16
 */
public class VmwareInfo {
    @ApiModelProperty(value = "IP", example = "127.0.0.1", required = true)
    private String ip;

    @ApiModelProperty(value = "用户名", example = "admin", required = true)
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

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
     * @return the ip
     */
    public VmwareInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     * @return the username
     */
    public VmwareInfo setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     * @return the password
     */
    public VmwareInfo setPassword(String password) {
        this.password = password;
        return this;
    }
}
