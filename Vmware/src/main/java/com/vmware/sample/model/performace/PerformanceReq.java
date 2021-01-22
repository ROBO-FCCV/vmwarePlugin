/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.performace;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Performance req
 *
 * @since 2020-09-29
 */
@Getter
@Setter
public class PerformanceReq {
    private List<String> ids;
    private Map<String, String> metricIds;
}
