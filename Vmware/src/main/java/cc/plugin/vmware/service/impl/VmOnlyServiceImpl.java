/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.constant.Constants;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ParaVirtualSCSIController;
import com.vmware.vim25.VirtualCdromIsoBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.VirtualSCSISharing;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-23
 */
@Service
public class VmOnlyServiceImpl extends AbstractVmOnlyService {
    @Override
    protected VirtualDeviceBackingInfo getBackingInfo(ManagedObjectReference dataStoreRef, String hostName) {
        VirtualCdromIsoBackingInfo virtualCdromIsoBackingInfo = new VirtualCdromIsoBackingInfo();
        virtualCdromIsoBackingInfo.setDatastore(dataStoreRef);
        virtualCdromIsoBackingInfo.setFileName(hostName);
        return virtualCdromIsoBackingInfo;
    }

    @Override
    protected VirtualDevice getVirtualDevice() {
        ParaVirtualSCSIController paraVirtualScsiController = new ParaVirtualSCSIController();
        paraVirtualScsiController.setKey(Constants.PARA_VIRTUAL_SCSI_CONTROLLER_KEY);
        paraVirtualScsiController.setSharedBus(VirtualSCSISharing.NO_SHARING);
        return paraVirtualScsiController;
    }

    @Override
    protected List<Integer> getUsedUnitNumbers(List<Integer> usedUnitNumbers) {
        return Arrays.asList(Constants.DEFAULT_VIRTUAL_DISK_UNIT_NUMBER);
    }

}
