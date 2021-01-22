/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.cluster;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.unit.DataSize;

/**
 * Cluster basic information
 *
 * @since 2020-09-15
 */
@Getter
@Setter
public class ClusterBasic {
    private String name;
    private String resourcePool;
    private long totalCpu;
    private long usedCpu;
    private boolean drs;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize totalMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeDatastoreSpace;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize totalDatastoreSpace;
}
