/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.performace;

import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetric;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 功能描述
 *
 * @auther y00576814
 * @since 2021-01-04
 */
@Getter
@Builder
public class AssembleReq {
    /**
     * vmware id
     */
    private final String vmwareId;

    /**
     * type
     */
    private final String type;

    /**
     * disk uasge
     */
    private final boolean diskUsage;

    /**
     * performance entity
     */
    private final List<PerfEntityMetric> perfEntityMetricBases;

    /**
     * performance count info
     */
    private final List<PerfCounterInfo> perfCounterInfos;

    /**
     * performance request
     */
    private final PerformanceReq performanceReq;
}
