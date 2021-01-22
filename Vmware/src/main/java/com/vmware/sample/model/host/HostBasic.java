/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.host;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.vmware.vim25.HostSystemPowerState;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.unit.DataSize;

/**
 * Host basic information
 *
 * @since 2019-09-19
 */
@Getter
@Setter
public class HostBasic extends HostInfo {
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize totalMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize usedMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize totalDatastore;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize usedDatastore;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeDatastore;
    private long totalCpu;
    private long usedCpu;
    private long freeCpu;
    private HostSystemPowerState powerState;
    private boolean inQuarantineMode;
}
