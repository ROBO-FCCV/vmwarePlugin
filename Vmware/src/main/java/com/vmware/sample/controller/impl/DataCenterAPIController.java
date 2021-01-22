/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.DataCenterController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.datacenter.DataCenterInfo;
import com.vmware.sample.service.DataCenterService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Datacenter api controller
 *
 * @since 2020-10-10
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class DataCenterAPIController implements DataCenterController {
    private final DataCenterService dataCenterService;

    public DataCenterAPIController(@Qualifier("datacenter-api-service") DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    @Override
    public RestResult<List<DataCenterInfo>> dataCenters(String vmwareId) {
        return RestResult.success(dataCenterService.dataCenters(vmwareId));
    }
}
