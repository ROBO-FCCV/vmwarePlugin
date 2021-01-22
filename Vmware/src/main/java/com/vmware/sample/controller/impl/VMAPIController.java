/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.VMController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.vm.SnapShotInfo;
import com.vmware.sample.model.vm.VirtualMachineInfo;
import com.vmware.sample.model.vm.VmConfigurationInfo;
import com.vmware.sample.model.vm.VmTemplateInfo;
import com.vmware.sample.model.vm.VmVNCInfo;
import com.vmware.sample.model.vm.VmVNCStatusInfo;
import com.vmware.sample.service.VMService;

import com.vmware.vim25.GuestOsDescriptor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Virtual machine api controller
 *
 * @since 2020-09-16
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class VMAPIController implements VMController {
    private final VMService vmService;

    @Autowired
    public VMAPIController(@Qualifier("vm-api-service") VMService vmService) {
        this.vmService = vmService;
    }

    @Override
    public RestResult<List<VirtualMachineInfo>> getVms(String vmwareId) {
        return RestResult.success(vmService.getVms(vmwareId));
    }

    @Override
    public RestResult<List<VirtualMachineInfo>> getVmsByHost(String vmwareId, String hostId) {
        return RestResult.success(vmService.getVmsByHost(vmwareId, hostId));
    }

    @Override
    public RestResult<String> createVmSnapshot(String vmwareId, SnapShotInfo snapshot) {
        return RestResult.success(vmService.createVmSnapshot(vmwareId, snapshot));
    }

    @Override
    public RestResult<String> powerStopByVmId(String vmwareId, String vmId) {
        return RestResult.success(vmService.powerStopByVmId(vmwareId, vmId));
    }

    @Override
    public RestResult<String> powerStartByVmId(String vmwareId, String vmId) {
        return RestResult.success(vmService.powerStartByVmId(vmwareId, vmId));
    }

    @Override
    public RestResult<String> powerResetByVmId(String vmwareId, String vmId) {
        return RestResult.success(vmService.powerResetByVmId(vmwareId, vmId));
    }

    @Override
    public RestResult<List<GuestOsDescriptor>> getGuestSystems(String vmwareId, String domainId, String hostId) {
        return RestResult.success(vmService.getGuestSystems(vmwareId, domainId, hostId));
    }

    @Override
    public RestResult<String> mountVmwareTools(String vmwareId, String vmId) {
        return RestResult.success(vmService.mountVmwareTools(vmwareId, vmId));
    }

    @Override
    public RestResult<String> getVmwareToolsStatus(String vmwareId, String vmId) {
        return RestResult.success(vmService.getVmwareToolsStatus(vmwareId, vmId));
    }

    @Override
    public RestResult<String> deleteVmByVmId(String vmwareId, String vmId) {
        return RestResult.success(vmService.deleteVmByVmId(vmwareId, vmId));
    }

    @Override
    public RestResult<String> markVmTemplate(String vmwareId, String vmId) {
        return RestResult.success(vmService.markVmTemplate(vmwareId, vmId));
    }

    @Override
    public RestResult<Map<String, List<VirtualMachineInfo>>> getVmByHosts(String vmwareId, List<String> hostIds) {
        return RestResult.success(vmService.getVmsByHosts(vmwareId, hostIds));
    }

    @Override
    public RestResult<VmVNCInfo> getVmVNCByVmId(String vmwareId, String vmId) {
        return RestResult.success(vmService.getVmVNCbyVmId(vmwareId, vmId));
    }

    @Override
    public RestResult<VirtualMachineInfo> getVmByVmId(String vmwareId, String vmId) {
        return RestResult.success(vmService.getVmByVmId(vmwareId, vmId));
    }

    @Override
    public RestResult<VmVNCStatusInfo> getVmVNCStatus(String vmwareId, String vmId) {
        return RestResult.success(vmService.getVmVNCStatus(vmwareId, vmId));
    }

    @Override
    public RestResult<String> createVmByConfig(String vmwareId, VmConfigurationInfo vmConfigurationInfo) {
        return RestResult.success(vmService.createVmByConfig(vmwareId, vmConfigurationInfo));
    }

    @Override
    public RestResult<String> cloneVmByTemplate(String vmwareId, VmTemplateInfo vmTemplateInfo) {
        return RestResult.success(vmService.cloneVmByTemplate(vmwareId, vmTemplateInfo));
    }

    @Override
    public RestResult<String> getVmIdByVmName(String vmwareId, String vmName) {
        return RestResult.success(vmService.getVmIdByVmName(vmwareId, vmName));
    }
}
