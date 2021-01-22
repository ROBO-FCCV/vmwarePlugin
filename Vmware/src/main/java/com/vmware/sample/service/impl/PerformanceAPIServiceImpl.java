/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.performace.PerformanceData;
import com.vmware.sample.model.performace.PerformanceReq;
import com.vmware.sample.service.PerformanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Performance api service
 *
 * @since 2020-09-29
 */
@Slf4j
@Service("performance-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PerformanceAPIServiceImpl implements PerformanceService {

    @Override
    public List<PerformanceData> performances(String vmwareId, PerformanceReq vmPerformanceReq, String type) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
