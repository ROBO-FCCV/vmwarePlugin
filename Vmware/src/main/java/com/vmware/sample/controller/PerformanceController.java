/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.performace.HostPerformanceReq;
import com.vmware.sample.model.performace.PerformanceData;
import com.vmware.sample.model.performace.VMPerformanceReq;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Query performance info
 *
 * @since 2020-09-27
 */
@Validated
public interface PerformanceController {
    /**
     * Query virtual machine performance
     *
     * @param vmwareId vmware id
     * @param vmPerformanceReq virtual machine performance req
     * @return performance data
     */
    @PostMapping("/{vmwareId}/vms/performance")
    RestResult<List<PerformanceData>> vmPerformances(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid VMPerformanceReq vmPerformanceReq);

    /**
     * Query host performance
     *
     * @param vmwareId vmware id
     * @param hostPerformanceReq host performance req
     * @return performance data
     */
    @PostMapping("/{vmwareId}/hosts/performance")
    RestResult<List<PerformanceData>> hostPerformances(
        @PathVariable @Pattern(regexp = Constants.ID_REGEXP) @NotBlank String vmwareId,
        @RequestBody @Valid HostPerformanceReq hostPerformanceReq);
}
