/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.host.HostBasic;
import com.vmware.sample.model.host.HostBusAdapterVo;

import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.ManagedObjectReference;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Host controller
 *
 * @since 2020-09-18
 */
@Validated
public interface HostController {
    /**
     * Gets host info by vmwareId and hostId
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return host info
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}")
    RestResult<HostBasic> getHostBasicInfo(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);

    /**
     * Query all hosts below the given vmware
     *
     * @param vmwareId vmware id
     * @return hosts
     */
    @GetMapping("/{vmwareId}/hosts")
    RestResult<List<HostBasic>> list(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);

    /**
     * Query available disks below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return hostScsi disks
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/available-disks")
    RestResult<List<HostScsiDisk>> availableDisks(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);

    /**
     * Query storage scsi lun below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return wwn
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/wwn")
    RestResult<List<String>> queryStorageScsiLun(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);

    /**
     * Query bus adapters
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return bus adapters
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/bus-adapters")
    RestResult<HostBusAdapterVo> busAdapters(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);

    /**
     * Rescan hba
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return rescan result
     */
    @PostMapping("/{vmwareId}/hosts/{hostId}/action/rescan-hba")
    RestResult<String> rescanHba(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);

    /**
     * Query resource pool below host
     *
     * @param vmwareId vmware id
     * @param hostId host id
     * @return resource pool
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/resource")
    RestResult<ManagedObjectReference> resourcePool(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);

    /**
     * Query esxi serial number
     *
     * @param hostId host id
     * @param vmwareId vmware id
     * @return serial number
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/serial-number")
    RestResult<String> querySerialNumber(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);
}
