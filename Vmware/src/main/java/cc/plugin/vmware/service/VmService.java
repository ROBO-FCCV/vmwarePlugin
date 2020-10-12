/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SnapshotFaultFaultMsg;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.VimFaultFaultMsg;
import com.vmware.vim25.VmConfigFaultFaultMsg;
import com.vmware.vim25.VmToolsUpgradeFaultFaultMsg;

import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.host.HostRequest;
import cc.plugin.vmware.model.vo.request.vm.RenameVmRequest;
import cc.plugin.vmware.model.vo.request.vm.SnapshotRequest;
import cc.plugin.vmware.model.vo.response.vm.VMVo;
import cc.plugin.vmware.model.vo.response.vm.VcenterType;
import cc.plugin.vmware.model.vo.response.vm.VmByHostIpRes;
import cc.plugin.vmware.model.vo.response.vm.VmInfo;
import cc.plugin.vmware.model.vo.response.vm.VmOverStatus;
import cc.plugin.vmware.model.vo.response.vm.VmStatus;
import cc.plugin.vmware.model.vo.response.vm.VmStatusVo;
import cc.plugin.vmware.model.vo.response.vm.VncVo;

import java.util.List;

/**
 * The interface Vm service.
 *
 * @since 2019-09-23
 */
public interface VmService {
    /**
     * Power on vm string.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the string
     * @throws CustomException the custom exception
     */
    String pownOnVm(String vmwareId, String vmId) throws CustomException;

    /**
     * Power off vm string.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the string
     * @throws CustomException the custom exception
     */
    String pownOffVm(String vmwareId, String vmId) throws CustomException;

    /**
     * Query vm list by host vm by host ip res.
     *
     * @param vmwareId the vmware id
     * @param hostRequest the host request
     * @return the vm by host ip res
     * @throws CustomException the custom exception
     */
    VmByHostIpRes queryVmListByHost(String vmwareId, HostRequest hostRequest)
        throws CustomException;

    /**
     * Delete vm string.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @param vmName the vm name
     * @return the string
     * @throws ApplicationException the application exception
     * @throws CustomException the custom exception
     * @throws InvalidStateFaultMsg the invalid state fault msg
     * @throws InterruptedException the interrupted exception
     * @throws TaskInProgressFaultMsg the task in progress fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     * @throws VimFaultFaultMsg the vim fault fault msg
     */
    String deleteVm(String vmwareId, String vmId, String vmName) throws
        ApplicationException,
        CustomException,
        InvalidStateFaultMsg,
        InterruptedException,
        TaskInProgressFaultMsg,
        RuntimeFaultFaultMsg,
        VimFaultFaultMsg;

    /**
     * Reset vm string.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the string
     * @throws CustomException the custom exception
     * @throws TaskInProgressFaultMsg the task in progress fault msg
     * @throws InvalidStateFaultMsg the invalid state fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     */
    String resetVm(String vmwareId, String vmId) throws CustomException,
        TaskInProgressFaultMsg,
        InvalidStateFaultMsg,
        RuntimeFaultFaultMsg;

    /**
     * Re name vm string.
     *
     * @param vmwareId the vmware id
     * @param renameVmRequest the rename vm request
     * @return the string
     * @throws CustomException the custom exception
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     */
    String reNameVm(String vmwareId, RenameVmRequest renameVmRequest)
        throws CustomException, RuntimeFaultFaultMsg;

    /**
     * Gets vm id by name.
     *
     * @param vmwareId the vmware id
     * @param vmName the vm name
     * @return the vm id by name
     * @throws CustomException the custom exception
     */
    String getVmIdByName(String vmwareId, String vmName) throws CustomException;

    /**
     * Gets vm status by vm id.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the vm status by vm id
     * @throws CustomException the custom exception
     */
    VmStatusVo getVmStatusByVmId(String vmwareId, String vmId) throws CustomException;

    /**
     * Query vm detail by vm id vm info.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the vm info
     * @throws CustomException the custom exception
     */
    VmInfo queryVmDetailByVmId(String vmwareId, String vmId) throws CustomException;

    /**
     * Query vnc info vnc vo.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the vnc vo
     * @throws CustomException the custom exception
     */
    VncVo queryVncInfo(String vmwareId, String vmId) throws CustomException;

    /**
     * Gets vmware tools status.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the vmware tools status
     * @throws CustomException the custom exception
     */
    String getVmwareToolsStatus(String vmwareId, String vmId) throws CustomException;

    /**
     * Mark as template.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @throws CustomException the custom exception
     * @throws FileFaultFaultMsg the file fault fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     * @throws InvalidStateFaultMsg the invalid state fault msg
     * @throws VmConfigFaultFaultMsg the vm config fault fault msg
     */
    void markAsTemplate(String vmwareId, String vmId) throws
        CustomException,
        FileFaultFaultMsg,
        RuntimeFaultFaultMsg,
        InvalidStateFaultMsg,
        VmConfigFaultFaultMsg;

    /**
     * Mount vm tools installer.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @throws CustomException the custom exception
     * @throws VmToolsUpgradeFaultFaultMsg the vm tools upgrade fault fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     * @throws InvalidStateFaultMsg the invalid state fault msg
     * @throws VmConfigFaultFaultMsg the vm config fault fault msg
     */
    void mountVMToolsInstaller(String vmwareId, String vmId) throws
        CustomException,
        VmToolsUpgradeFaultFaultMsg,
        RuntimeFaultFaultMsg,
        InvalidStateFaultMsg,
        VmConfigFaultFaultMsg;

    /**
     * Gets all overall status.
     *
     * @param vmwareId the vmware id
     * @return the all overall status
     * @throws CustomException the custom exception
     */
    List<VmOverStatus> getAllOverallStatus(String vmwareId) throws CustomException;

    /**
     * Gets system type.
     *
     * @param vmwareId the vmware id
     * @return the system type
     * @throws CustomException the custom exception
     */
    List<VcenterType> getSystemType(String vmwareId) throws CustomException;

    /**
     * Power on vm status.
     *
     * @param vmwareId the vmware id
     * @param vmName the vm name
     * @return the vm status
     * @throws ApplicationException the application exception
     * @throws CustomException the custom exception
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     * @throws InsufficientResourcesFaultFaultMsg the insufficient resources fault fault msg
     * @throws FileFaultFaultMsg the file fault fault msg
     * @throws VmConfigFaultFaultMsg the vm config fault fault msg
     * @throws TaskInProgressFaultMsg the task in progress fault msg
     * @throws InvalidStateFaultMsg the invalid state fault msg
     */
    VmStatus powerOn(String vmwareId, String vmName) throws
        ApplicationException,
        CustomException,
        RuntimeFaultFaultMsg,
        InsufficientResourcesFaultFaultMsg,
        FileFaultFaultMsg,
        VmConfigFaultFaultMsg,
        TaskInProgressFaultMsg,
        InvalidStateFaultMsg;

    /**
     * Power off vm status.
     *
     * @param vmwareId the vmware id
     * @param vmName the vm name
     * @return the vm status
     * @throws ApplicationException the application exception
     * @throws CustomException the custom exception
     * @throws TaskInProgressFaultMsg the task in progress fault msg
     * @throws InvalidStateFaultMsg the invalid state fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     */
    VmStatus powerOff(String vmwareId, String vmName) throws ApplicationException, CustomException,
        TaskInProgressFaultMsg,
        InvalidStateFaultMsg,
        RuntimeFaultFaultMsg;

    /**
     * Create snapshot string.
     *
     * @param vmwareId the vmware id
     * @param vmName the vm name
     * @return the string
     * @throws InterruptedException the interrupted exception
     * @throws SnapshotFaultFaultMsg the snapshot fault fault msg
     * @throws InvalidNameFaultMsg the invalid name fault msg
     * @throws VmConfigFaultFaultMsg the vm config fault fault msg
     * @throws FileFaultFaultMsg the file fault fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     * @throws TaskInProgressFaultMsg the task in progress fault msg
     * @throws InvalidStateFaultMsg the invalid state fault msg
     * @throws CustomException the custom exception
     */
    String createSnapshot(String vmwareId, SnapshotRequest vmName) throws
        InterruptedException,
        SnapshotFaultFaultMsg,
        InvalidNameFaultMsg,
        VmConfigFaultFaultMsg,
        FileFaultFaultMsg,
        RuntimeFaultFaultMsg,
        TaskInProgressFaultMsg,
        InvalidStateFaultMsg, CustomException;

    /**
     * Gets host vms.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the host vms
     * @throws CustomException the custom exception
     */
    List<VMVo> getHostVms(String vmwareId, String hostId) throws CustomException;

    /**
     * Gets vms.
     *
     * @param vmwareId the vmware id
     * @return the vms
     * @throws CustomException the custom exception
     */
    List<VMVo> getVms(String vmwareId) throws CustomException;

    /**
     * Power on vm task string.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the string
     * @throws CustomException the custom exception
     */
    String powerOnVmTask(String vmwareId, String vmId) throws CustomException;

    /**
     * Power off vm task string.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the string
     * @throws CustomException the custom exception
     */
    String powerOffVmTask(String vmwareId, String vmId) throws CustomException;
}
