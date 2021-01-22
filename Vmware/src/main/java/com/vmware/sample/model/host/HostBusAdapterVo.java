/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.host;

import com.vmware.vim25.HostBlockHba;
import com.vmware.vim25.HostFibreChannelHba;
import com.vmware.vim25.HostInternetScsiHba;
import com.vmware.vim25.HostSerialAttachedHba;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Host bus adapter
 *
 * @since 2020-10-12
 */
@Getter
@Setter
public class HostBusAdapterVo {
    private List<HostSerialAttachedHba> hostSerialAttachedHbas;
    private List<HostFibreChannelHba> hostFibreChannelHbas;
    private List<HostBlockHba> hostBlockHbas;
    private List<HostInternetScsiHba> hostInternetScsiHbas;
}
