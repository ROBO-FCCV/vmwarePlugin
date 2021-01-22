/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.vm.SnapShotInfo;
import com.vmware.sample.model.vm.VirtualMachineBasic;
import com.vmware.sample.model.vm.VirtualMachineInfo;
import com.vmware.sample.model.vm.VmConfigurationInfo;
import com.vmware.sample.model.vm.VmTemplateInfo;
import com.vmware.sample.model.vm.VmVNCInfo;
import com.vmware.sample.model.vm.VmVNCStatusInfo;

import com.vmware.vim25.GuestOsDescriptor;

import java.util.List;
import java.util.Map;

/**
 * VM service interface
 *
 * @since 2020-09-16
 */
public interface VMService {
    /**
     * Get a list of virtual machines by vmwareId
     *
     * @param vmwareId vmwareId
     * @return virtual machines
     */
    List<VirtualMachineInfo> getVms(String vmwareId);

    /**
     * Get a list of virtual machines by vmwareId and hostId
     *
     * @param vmwareId vmware Id
     * @param hostId host Id
     * @return virtual machines info
     */
    List<VirtualMachineInfo> getVmsByHost(String vmwareId, String hostId);

    /**
     * Get a list of virtual machines by vmwareId and hostId
     *
     * @param vmwareId vmware Id
     * @param hostId host Id
     * @return virtual machines basic
     */
    List<VirtualMachineBasic> queryVmsByHost(String vmwareId, String hostId);

    /**
     * Create virtual machine snapshot
     *
     * @param vmwareId vmware id
     * @param snapshot snapshot
     * @return snapshot name
     */
    String createVmSnapshot(String vmwareId, SnapShotInfo snapshot);

    /**
     * Virtual machine power stop by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return power stop status
     */
    String powerStopByVmId(String vmwareId, String vmId);

    /**
     * Virtual machine power start by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return power start status
     */
    String powerStartByVmId(String vmwareId, String vmId);

    /**
     * Get the operating system supported by the host.
     *
     * @param vmwareId vmware id
     * @param domainId domain id
     * @param hostId host id
     * @return virtual machine operating system map
     */
    List<GuestOsDescriptor> getGuestSystems(String vmwareId, String domainId, String hostId);

    /**
     * Mount virtual machine tools by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return success or fail
     */
    String mountVmwareTools(String vmwareId, String vmId);

    /**
     * Marking a virtual machine as a virtual machine Template by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return success or fail
     */
    String markVmTemplate(String vmwareId, String vmId);

    /**
     * Get vmware tools state by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return vmware tools status
     */
    String getVmwareToolsStatus(String vmwareId, String vmId);

    /**
     * Reset virtual machine  power by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return power reset status
     */
    String powerResetByVmId(String vmwareId, String vmId);

    /**
     * Delete virtual machine  power by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return virtual machine delete status
     */
    String deleteVmByVmId(String vmwareId, String vmId);

    /**
     * Get virtual machine and host list
     *
     * @param vmwareId vmware id
     * @param hostIds hostIds
     * @return map of virtual machine list and hostname
     */
    Map<String, List<VirtualMachineInfo>> getVmsByHosts(String vmwareId, List<String> hostIds);

    /**
     * Get virtual machine vnc information
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return virtual machine VNC information
     */
    VmVNCInfo getVmVNCbyVmId(String vmwareId, String vmId);

    /**
     * Get virtual machine by vmwareId and vmId
     *
     * @param vmwareId vmwareId
     * @param vmId virtual machine id
     * @return virtual machine id info
     */
    VirtualMachineInfo getVmByVmId(String vmwareId, String vmId);

    /**
     * Get virtual machine vnc status
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return virtual machine vnc status
     */
    VmVNCStatusInfo getVmVNCStatus(String vmwareId, String vmId);

    /**
     * Clone virtual machine by template
     *
     * @param vmwareId vmwareId
     * @param vmTemplateInfo template config information
     * @return virtual machine name
     */
    String cloneVmByTemplate(String vmwareId, VmTemplateInfo vmTemplateInfo);

    /**
     * Create virtual machine by configuration
     *
     * @param vmwareId vmware id
     * @param vmConfigInfo config formation
     * @return create success or fail
     */
    String createVmByConfig(String vmwareId, VmConfigurationInfo vmConfigInfo);

    /**
     * Get virtual machine id by vmName
     *
     * @param vmwareId vmware id
     * @param vmName virtual machine name
     * @return virtual machine id
     */
    String getVmIdByVmName(String vmwareId, String vmName);
}
