/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.mo;

/**
 * 登录实例
 *
 * @since 2019 -09-09
 */
public class LoginParamMo {
    private String vmwareId;
    private String username;
    private String ip;
    private String password;
    private String url;

    /**
     * Gets vmware id.
     *
     * @return the vmware id
     */
    public String getVmwareId() {
        return vmwareId;
    }

    /**
     * Sets vmware id.
     *
     * @param vmwareId the vmware id
     * @return the vmware id
     */
    public LoginParamMo setVmwareId(String vmwareId) {
        this.vmwareId = vmwareId;
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
    public LoginParamMo setUsername(String username) {
        this.username = username;
        return this;
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
     * @return the ip
     */
    public LoginParamMo setIp(String ip) {
        this.ip = ip;
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
    public LoginParamMo setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     * @return the url
     */
    public LoginParamMo setUrl(String url) {
        this.url = url;
        return this;
    }
}
