/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.datacenter.DataCenterInfo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * DataCenter Controller
 *
 * @since 2020-10-10
 */
@Validated
public interface DataCenterController {
    @GetMapping("/{vmwareId}/datacenters")
    RestResult<List<DataCenterInfo>> dataCenters(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);
}
