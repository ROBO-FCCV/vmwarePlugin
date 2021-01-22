/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Network information
 *
 * @since 2020-09-22
 */
@Getter
@Setter
public class NetworkInfo {
    /**
     * mac address
     */
    private String macAddress;

    /**
     * mac type
     */
    private String macType;

    /**
     * network name
     */
    @NotBlank
    private String name;
}
