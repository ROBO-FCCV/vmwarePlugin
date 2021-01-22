/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model;

import com.vmware.sample.consts.Constants;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * VMware
 *
 * @since 2020-09-16
 */
@Setter
@Getter
public class VMware {
    @NotBlank
    private String username;
    @NotEmpty
    private char[] password;
    @NotBlank
    @Pattern(regexp = Constants.IP_RULE)
    private String ip;
    private String id;
    private int port = 443;
}

