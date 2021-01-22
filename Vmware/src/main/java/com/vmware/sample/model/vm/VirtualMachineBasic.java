/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Virtual machine basic information
 *
 * @since 2020-09-15
 */
@Getter
@Setter
public class VirtualMachineBasic {
    /**
     * modId
     */
    private String modId;

    /**
     * virtual machine name
     */
    private String vmName;

    /**
     * virtual machine id
     */
    private String vmId;

    /**
     * virtual machine status
     */
    private String status;

    /**
     * virtual machine power status
     */
    private String powerStatus;

    /**
     * virtual machine ipAddress
     */
    private String ipAddress;

    /**
     * virtual machine ipAddress cpu count
     */
    private long cpuCount;

    /**
     * Number cores per socket
     */
    private long numCoresPerSocket;

    /**
     * virtual machine ipAddress memory size
     */
    private long memorySize;
}
