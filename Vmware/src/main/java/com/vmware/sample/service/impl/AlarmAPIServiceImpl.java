/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.alarm.Alarm;
import com.vmware.sample.service.AlarmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Alarm api service
 *
 * @since 2020-09-27
 */
@Slf4j
@Service("alarm-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AlarmAPIServiceImpl implements AlarmService {
    @Override
    public List<Alarm> currentAlarms(String vmwareId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
