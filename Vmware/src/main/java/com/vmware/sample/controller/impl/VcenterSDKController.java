/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.VcenterController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.vcenter.VcenterBasicInfo;
import com.vmware.sample.service.VcenterService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Vcenter basic info
 *
 * @since 2020-09-27
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/sdk")
public class VcenterSDKController implements VcenterController {
    private final VcenterService vcenterService;

    public VcenterSDKController(@Qualifier("vcenter-sdk-service") VcenterService vcenterService) {
        this.vcenterService = vcenterService;
    }

    @Override
    public RestResult<VcenterBasicInfo> queryVcenterBasicInfo(String vmwareId) {
        return RestResult.success(vcenterService.queryVcenterBasicInfo(vmwareId));
    }
}
