/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.vm.SnapShotInfo;
import com.vmware.sample.model.vm.VirtualMachineInfo;
import com.vmware.sample.model.vm.VmConfigurationInfo;
import com.vmware.sample.model.vm.VmTemplateInfo;
import com.vmware.sample.model.vm.VmVNCInfo;
import com.vmware.sample.model.vm.VmVNCStatusInfo;

import com.vmware.vim25.GuestOsDescriptor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Virtual machine controller
 *
 * @since 2020-09-18
 */
@Validated
public interface VMController {
    /**
     * Get a list of virtual machines by vmwareId
     *
     * @param vmwareId vmware id
     * @return virtual machines
     */
    @GetMapping("{vmwareId}/vms")
    RestResult<List<VirtualMachineInfo>> getVms(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);

    /**
     * Get virtual machines by vmwareId and hostId
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return virtual machines
     */
    @GetMapping("{vmwareId}/hosts/{hostId}/vms")
    RestResult<List<VirtualMachineInfo>> getVmsByHost(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.HID_REGEXP) String hostId);

    /**
     * Create virtual machine snapshot
     *
     * @param vmwareId vmware id
     * @param snapshot snapshot
     * @return snapshot name
     */
    @PostMapping("/{vmwareId}/vms/snapshot")
    RestResult<String> createVmSnapshot(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid SnapShotInfo snapshot);

    /**
     * Machine power stop
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return power stop status
     */
    @PostMapping("/{vmwareId}/vms/{vmId}/power/stop")
    RestResult<String> powerStopByVmId(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Virtual machine power start
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return power start status
     */
    @PostMapping("/{vmwareId}/vms/{vmId}/power/start")
    RestResult<String> powerStartByVmId(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Virtual machine power reset
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return power restart status
     */
    @PostMapping("/{vmwareId}/vms/{vmId}/power/reset")
    RestResult<String> powerResetByVmId(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Get the operating system supported by the host.
     *
     * @param vmwareId vmware id
     * @param domainId domain id
     * @param hostId host id
     * @return virtual machine operating system map
     */
    @GetMapping("/{vmwareId}/domain/guest-systems")
    RestResult<List<GuestOsDescriptor>> getGuestSystems(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId, @RequestParam String domainId,
        @RequestParam String hostId);

    /**
     * Mount virtual machine tools
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return success or fail
     */
    @PostMapping("/{vmwareId}/vms/{vmId}/vmware-tools/mount")
    RestResult<String> mountVmwareTools(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Get vmware tools status by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return vmware tools status
     */
    @GetMapping("/{vmwareId}/vms/{vmId}/vmware-tools/status")
    RestResult<String> getVmwareToolsStatus(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Delete virtual machine by vmwareId and virtual machine Id
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return delete status
     */
    @PostMapping("/{vmwareId}/vms/{vmId}/delete")
    RestResult<String> deleteVmByVmId(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @NotBlank @PathVariable @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Marking a virtual machine as a virtual machine Template by vmwareId and vmId
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return mark virtual machine template status
     */
    @PostMapping("/{vmwareId}/vms/{vmId}/mark/template")
    RestResult<String> markVmTemplate(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Get virtual machine and host info by vmwareId and hostId
     *
     * @param vmwareId vmwareId
     * @param hostIds host id list
     * @return virtual machine and host info
     */
    @GetMapping("/{vmwareId}/hosts/hostIds/vms")
    RestResult<Map<String, List<VirtualMachineInfo>>> getVmByHosts(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid List<String> hostIds);

    /**
     * Get vnc information by vmwareId and vmId
     *
     * @param vmwareId vmwareId
     * @param vmId virtual machine id
     * @return Vm VNCInfo
     */
    @GetMapping("/{vmwareId}/vms/{vmId}/vnc")
    RestResult<VmVNCInfo> getVmVNCByVmId(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Get virtual machine by vmware id and vm id
     *
     * @param vmwareId vmware id
     * @param vmId virtual machine id
     * @return virtual machine information
     */
    @GetMapping("/{vmwareId}/vms/{vmId}")
    RestResult<VirtualMachineInfo> getVmByVmId(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Get virtual machine vnc status by vmwareId and vmId
     *
     * @param vmwareId vmwareId
     * @param vmId virtual machine  id
     * @return virtual machine vnc status
     */
    @GetMapping("/{vmwareId}/vms/{vmId}/vnc/status")
    RestResult<VmVNCStatusInfo> getVmVNCStatus(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.VID_REGEXP) String vmId);

    /**
     * Create virtual machine by config information
     *
     * @param vmwareId vmware id
     * @param vmConfigurationInfo config information
     * @return virtual machine name
     */
    @PostMapping("/{vmwareId}/vms/action/create")
    RestResult<String> createVmByConfig(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @Valid @RequestBody VmConfigurationInfo vmConfigurationInfo);

    /**
     * Clone virtual machine by template
     *
     * @param vmwareId vmwareId
     * @param vmTemplateInfo virtual machine template information
     * @return virtual machine by config information name
     */
    @PostMapping("/{vmwareId}/vms/action/clone")
    RestResult<String> cloneVmByTemplate(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @Valid @RequestBody VmTemplateInfo vmTemplateInfo);

    /**
     * Get virtual machine id by virtual machine name
     *
     * @param vmwareId vmware id
     * @param vmName virtual machine name
     * @return virtual machine id
     */
    @GetMapping("/{vmwareId}/vm/{vmName}")
    RestResult<String> getVmIdByVmName(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String vmName);
}
