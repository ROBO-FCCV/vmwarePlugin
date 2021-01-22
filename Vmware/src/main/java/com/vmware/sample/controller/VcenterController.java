/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.vcenter.VcenterBasicInfo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Vcenter controller
 *
 * @since 2020-10-08
 */
@Validated
public interface VcenterController {
    /**
     * Vcenter basic info
     *
     * @param vmwareId vmware id
     * @return vcenter basic info
     */
    @GetMapping("/{vmwareId}/resources")
    RestResult<VcenterBasicInfo> queryVcenterBasicInfo(
        @PathVariable @NotNull @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);
}
