/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vcenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.unit.DataSize;

/**
 * Vcenter basic info
 *
 * @since 2020-09-16
 */
@Getter
@Setter
public class VcenterBasicInfo {
    private long totalCpu;
    private long usedCpu;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize totalMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize usedMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize totalDatastoreSpace;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeDatastoreSpace;
}
