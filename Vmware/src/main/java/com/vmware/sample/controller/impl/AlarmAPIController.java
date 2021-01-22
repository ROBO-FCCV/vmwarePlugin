/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.AlarmController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.alarm.Alarm;
import com.vmware.sample.service.AlarmService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Alarm api controller
 *
 * @since 2020-09-29
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class AlarmAPIController implements AlarmController {
    private final AlarmService alarmService;

    public AlarmAPIController(@Qualifier("alarm-api-service") AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @Override
    public RestResult<List<Alarm>> queryAlarms(String vmwareId) {
        return RestResult.success(alarmService.currentAlarms(vmwareId));
    }
}
