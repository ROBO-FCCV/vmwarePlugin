/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.network.NetworkInfo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Network controller
 *
 * @since 2020-09-22
 */
@Validated
public interface NetworkController {
    /**
     * Query network below datacenter
     *
     * @param vmwareId vmware id
     * @param dataCenterId dataCenter id
     * @return networks
     */
    @GetMapping("/{vmwareId}/datacenters/{dataCenterId}/networks")
    RestResult<List<NetworkInfo>> list(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "datacenter-\\d+") String dataCenterId);

    /**
     * Query network below compute resource
     *
     * @param vmwareId vmware id
     * @param domainId compute resource id
     * @return networks
     */
    @GetMapping("/{vmwareId}/domains/{domainId}/networks")
    RestResult<List<NetworkInfo>> listByDomain(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = Constants.CID_REGEXP) String domainId);

    /**
     * Query network below host
     *
     * @param vmwareId vmware id
     * @param hostId dataCenter id
     * @return networks
     */
    @GetMapping("/{vmwareId}/hosts/{hostId}/networks")
    RestResult<List<NetworkInfo>> listByHost(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank @Pattern(regexp = "host-\\d+") String hostId);
}
