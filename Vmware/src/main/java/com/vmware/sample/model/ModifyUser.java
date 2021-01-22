/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * Modify user
 *
 * @since 2020-11-21
 */
@Getter
@Setter
public class ModifyUser {
    private String username = "admin";
    @NotEmpty
    private char[] password;
    @NotEmpty
    private char[] newPassword;
}
