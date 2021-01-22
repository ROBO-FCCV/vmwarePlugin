/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.datastore;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Getter;
import lombok.Setter;

import org.springframework.util.unit.DataSize;

/**
 * Datastore basic
 *
 * @since 2020-09-25
 */
@Getter
@Setter
public class DatastoreBasic {
    private String modId;
    private String name;
    private String type;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize freeSpace;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize capacity;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize usedSpace;
    @JsonSerialize(using = ToStringSerializer.class)
    private DataSize uncommitted;
    private boolean multipleHostAccess;
    private boolean accessible;
    private boolean maintenanceMode;
    private String url;
}
