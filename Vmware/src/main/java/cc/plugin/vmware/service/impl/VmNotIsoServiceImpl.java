/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.constant.Constants;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualCdromRemotePassthroughBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.VirtualLsiLogicSASController;
import com.vmware.vim25.VirtualSCSISharing;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-27
 */
@Service
public class VmNotIsoServiceImpl extends AbstractVmOnlyService {
    @Override
    protected VirtualDeviceBackingInfo getBackingInfo(ManagedObjectReference dataStoreRef, String hostName) {
        VirtualCdromRemotePassthroughBackingInfo virtualCdromRemotePassthroughBackingInfo =
            new VirtualCdromRemotePassthroughBackingInfo();
        virtualCdromRemotePassthroughBackingInfo.setDeviceName("");
        virtualCdromRemotePassthroughBackingInfo.setExclusive(false);
        virtualCdromRemotePassthroughBackingInfo.setUseAutoDetect(false);
        return virtualCdromRemotePassthroughBackingInfo;
    }

    @Override
    protected VirtualDevice getVirtualDevice() {
        VirtualLsiLogicSASController paraVirtualScsiController = new VirtualLsiLogicSASController();
        paraVirtualScsiController.setKey(Constants.PARA_VIRTUAL_SCSI_CONTROLLER_KEY);
        paraVirtualScsiController.setSharedBus(VirtualSCSISharing.NO_SHARING);
        return paraVirtualScsiController;
    }

    @Override
    protected List<Integer> getUsedUnitNumbers(List<Integer> usedUnitNumbers) {
        return usedUnitNumbers;
    }
}
