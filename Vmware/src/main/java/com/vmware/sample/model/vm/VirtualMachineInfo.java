/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * VirtualMachine detail information
 *
 * @since 2020-09-15
 */
@Getter
@Setter
public class VirtualMachineInfo extends VirtualMachineBasic {
    /**
     * vnc
     */
    private Boolean vnc;

    /**
     * OS name
     */
    private String osName;

    /**
     * VirtualMachine clusterId
     */
    private String clusterId;

    /**
     * VirtualMachine hostId
     */
    private String hostId;

    /**
     * VirtualMachine guest network lists
     */
    private List<NetToInfo> nets;
    /**
     * VirtualMachine networks
     */
    private List<NetworkBasic> networks;

    /**
     * VirtualMachine disk lists
     */
    private List<DiskToInfo> disks;

    /**
     * VirtualMachine cluster name
     */
    private String clusterName;

    /**
     * VirtualMachine host name
     */
    private String hostName;

    /**
     * VirtualMachine OS full name
     */
    private String osFullName;
    /**
     * Template identity
     */
    private boolean template;
    /**
     * Guest status
     */
    private String guestStatus;
    /**
     * Vmware tools running status
     */
    private String toolsRunningStatus;
    /**
     * Vmware tools status
     */
    private String toolsStatus;
}
