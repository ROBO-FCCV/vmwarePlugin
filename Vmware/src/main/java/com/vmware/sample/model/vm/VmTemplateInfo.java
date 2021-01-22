/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import com.vmware.sample.model.network.Network;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.validation.constraints.NotBlank;

/**
 * Create virtual machine configuration information from a template
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class VmTemplateInfo extends VmConfigurationBasicInfo {

    /**
     * templateId
     */
    @NotBlank
    private String templateId;

    /**
     * disk list
     */
    private List<DiskInfo> disks;

    /**
     * power on
     */
    private boolean powerOn;

    /**
     * network
     */
    private List<NetworkInfo> networks;

    /**
     * Os type
     */
    private String osType;

    /**
     * Os password
     */
    private String osPassword;

    /**
     * nis
     */
    private List<Network> nis;

    /**
     * if customization
     */
    private boolean appmanagement;
}
