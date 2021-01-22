/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.alarm.Alarm;

import java.util.List;

/**
 * Alarm service
 *
 * @since 2020-09-15
 */
public interface AlarmService {
    /**
     * Query vmware alarms
     *
     * @param vmwareId vmware id
     * @return alarms
     */
    List<Alarm> currentAlarms(String vmwareId);
}
