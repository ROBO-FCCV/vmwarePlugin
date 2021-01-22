/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Create virtual machine by configuration information
 *
 * @since 2020-09-21
 */
@Getter
@Setter
public class VmConfigurationInfo extends VmConfigurationBasicInfo {
    /**
     * os version
     */
    @NotBlank
    private String osVersion;

    /**
     * network
     */
    @Valid
    private List<NetworkInfo> networks;

    /**
     * disk list
     */
    @Valid
    private List<DiskInfo> disks;

    /**
     * cdrom
     */
    private List<CdromInfo> cdrom;
}
