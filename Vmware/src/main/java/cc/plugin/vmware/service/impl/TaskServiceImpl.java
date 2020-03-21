/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.service.TaskService;

import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualMachineRuntimeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    @Override
    public String getTaskStatus(String vmwareId, String vmId) throws CustomException {
        String result = null;
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceUtil svc;
        ecb.connect();
        try {
            svc = ecb.getServiceUtil();
            ManagedObjectReference vmRef = new ManagedObjectReference();
            vmRef.setType("VirtualMachine");
            vmRef.setValue(vmId);
            if (svc != null && svc.connection != null) {
                GuestInfo guestinfo = (GuestInfo) svc.getDynamicProperty(vmRef, "guest");
                if (guestinfo == null) {
                    throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.GET_TASK_STATUS_EXCEPTION_MSG);
                }
                String guestStatus = guestinfo.getGuestState(); // "running"
                String toolsRunningStatus = guestinfo.getToolsRunningStatus(); // guestToolsRunning
                VirtualMachineRuntimeInfo vmRuntimeInfo = (VirtualMachineRuntimeInfo) svc.getDynamicProperty(vmRef,
                    "runtime");
                boolean isRunning = checkisRunning(guestStatus, toolsRunningStatus, vmRuntimeInfo);

                if (isRunning) {
                    result = guestStatus;
                } else {
                    result = "notRunning";
                }
                logger.info("isRunning {}", isRunning);
            }
        } catch (Exception e) {
            logger.error("get task status failed ", e);
            result = "notRunning";
        }
        return result;
    }

    private boolean checkisRunning(String guestStatus, String toolsRunningStatus,
        VirtualMachineRuntimeInfo vmRuntimeInfo) {
        boolean isRunning = false;
        if (vmRuntimeInfo != null) {
            String powderState = vmRuntimeInfo.getPowerState().value(); // poweredOn
            isRunning = "guestToolsRunning".equalsIgnoreCase(toolsRunningStatus) && "running".equalsIgnoreCase(
                guestStatus) && "poweredOn".equalsIgnoreCase(powderState);
        }
        return isRunning;
    }
}