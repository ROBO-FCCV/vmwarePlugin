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
public class PasswordVo {
    @ApiModelProperty(value = "密码", required = true)
    private String password;

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
    public PasswordVo setPassword(String password) {
        this.password = password;
        return this;
    }
}
