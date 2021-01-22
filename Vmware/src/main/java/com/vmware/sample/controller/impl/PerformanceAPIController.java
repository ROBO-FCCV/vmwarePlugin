/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.consts.VMConstants;
import com.vmware.sample.controller.PerformanceController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.performace.HostPerformanceReq;
import com.vmware.sample.model.performace.PerformanceData;
import com.vmware.sample.model.performace.VMPerformanceReq;
import com.vmware.sample.service.PerformanceService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Performance api controller
 *
 * @since 2020-09-29
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class PerformanceAPIController implements PerformanceController {
    private final PerformanceService performanceService;

    public PerformanceAPIController(@Qualifier("performance-api-service") PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @Override
    public RestResult<List<PerformanceData>> vmPerformances(String vmwareId, VMPerformanceReq vmPerformanceReq) {
        return RestResult.success(
            performanceService.performances(vmwareId, vmPerformanceReq, VMConstants.VIRTUAL_MACHINE));
    }

    @Override
    public RestResult<List<PerformanceData>> hostPerformances(String vmwareId, HostPerformanceReq hostPerformanceReq) {
        return RestResult.success(
            performanceService.performances(vmwareId, hostPerformanceReq, VMConstants.HOST_SYSTEM));
    }
}
