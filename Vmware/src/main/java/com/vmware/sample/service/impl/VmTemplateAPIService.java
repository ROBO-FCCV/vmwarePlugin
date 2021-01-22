/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.service.VmTemplateService;

import com.vmware.vcenter.ovf.LibraryItem;
import com.vmware.vcenter.ovf.LibraryItemTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Vm template api service implement
 *
 * @since 2020-10-16
 */
@Slf4j
@Service("vm-template-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VmTemplateAPIService implements VmTemplateService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public LibraryItemTypes.DeploymentResult deploy(String vmwareId, String libraryItemId,
        LibraryItemTypes.DeploymentTarget deploymentTarget,
        LibraryItemTypes.ResourcePoolDeploymentSpec resourcePoolDeploymentSpec) {
        LibraryItem libraryItem = vmwareAPIClient.getStubConfiguration(vmwareId, LibraryItem.class);
        return libraryItem.deploy(UUID.randomUUID().toString(), libraryItemId, deploymentTarget,
            resourcePoolDeploymentSpec);
    }
}
