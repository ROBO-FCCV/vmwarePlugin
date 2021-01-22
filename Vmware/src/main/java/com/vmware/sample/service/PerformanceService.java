/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.performace.PerformanceData;
import com.vmware.sample.model.performace.PerformanceReq;

import java.util.List;

/**
 * Performance service
 *
 * @since 2020-09-28
 */
public interface PerformanceService {
    /**
     * Query virtual machine performance
     *
     * @param vmwareId vmware id
     * @param vmPerformanceReq virtual machine performance req
     * @param type type
     * @return performance data
     */
    List<PerformanceData> performances(String vmwareId, PerformanceReq vmPerformanceReq, String type);
}
