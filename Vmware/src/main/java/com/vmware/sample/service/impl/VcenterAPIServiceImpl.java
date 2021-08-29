/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.vcenter.VcenterBasicInfo;
import com.vmware.sample.service.VcenterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * vcenter api service implement
 *
 * @since 2020-10-08
 */
@Slf4j
@Service("vcenter-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VcenterAPIServiceImpl implements VcenterService {
    @Override
    public VcenterBasicInfo queryVcenterBasicInfo(String vmwareId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
