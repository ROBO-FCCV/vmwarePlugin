/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Cdrom information
 *
 * @since 2020-10-12
 */
@Setter
@Getter
public class CdromInfo {
    /**
     * name
     */
    private String name;

    /**
     * device type
     */
    @NotBlank
    private String deviceType;

    /**
     * iso file
     */
    private String isoFile;

    /**
     * host device
     */
    private String hostDevice;

    /**
     * access type
     */
    @NotBlank
    private String accessType;

    /**
     * smart connected
     */
    private boolean connected;
}
