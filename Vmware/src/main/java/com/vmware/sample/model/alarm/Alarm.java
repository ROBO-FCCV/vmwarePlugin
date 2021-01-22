/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.alarm;

import com.vmware.vim25.ManagedEntityStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * Alarm model
 *
 * @since 2020-09-15
 */
@Getter
@Setter
public class Alarm {
    private String key;
    private String description;
    private String alarmName;
    private String alarmId;
    private Boolean acknowledged;
    private Long acknowledgedTime;
    private String acknowledgedByUser;
    private String objectName;
    private String objectId;
    private ManagedEntityStatus overallStatus;
    private Long time;
}
