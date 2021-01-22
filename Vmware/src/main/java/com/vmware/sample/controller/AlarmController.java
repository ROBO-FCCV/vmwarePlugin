/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.alarm.Alarm;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Alarm controller
 *
 * @since 2020-09-27
 */
@Validated
public interface AlarmController {
    /**
     * Query current alarms
     *
     * @param vmwareId vmware id
     * @return current alarms
     */
    @GetMapping(value = "/{vmwareId}/alarms")
    RestResult<List<Alarm>> queryAlarms(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId);
}
