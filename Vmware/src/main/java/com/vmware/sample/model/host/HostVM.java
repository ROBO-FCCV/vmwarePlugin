/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.host;

import com.vmware.sample.model.vm.VirtualMachineInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Host with vms
 *
 * @since 2020-09-16
 */
@Getter
@Setter
public class HostVM extends HostInfo {
    private List<VirtualMachineInfo> vms;
}
