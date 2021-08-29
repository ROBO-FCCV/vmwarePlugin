/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.service.VmTemplateService;

import com.vmware.vcenter.ovf.LibraryItemTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Vm template api service implement
 *
 * @since 2020-10-16
 */
@Slf4j
@Service("vm-template-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VmTemplateSDKService implements VmTemplateService {
    @Override
    public LibraryItemTypes.DeploymentResult deploy(String vmwareId, String libraryItemId,
        LibraryItemTypes.DeploymentTarget deploymentTarget,
        LibraryItemTypes.ResourcePoolDeploymentSpec resourcePoolDeploymentSpec) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }
}
