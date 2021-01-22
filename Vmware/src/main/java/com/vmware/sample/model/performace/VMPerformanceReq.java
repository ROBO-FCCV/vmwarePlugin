/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.performace;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Virtual machine performance request
 *
 * @since 2020-09-28
 */
@Getter
@Setter
public class VMPerformanceReq extends PerformanceReq {
    @NotEmpty
    private List<@Pattern(regexp = "vm-\\d+") String> ids;
    @NotEmpty
    private Map<String, String> metricIds;
}
