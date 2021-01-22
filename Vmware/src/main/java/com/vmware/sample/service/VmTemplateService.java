/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.vcenter.ovf.LibraryItemTypes;

/**
 * Vm template service
 *
 * @since 2020-10-16
 */
public interface VmTemplateService {
    /**
     * Using library item deploy vm
     *
     * @param vmwareId vmware id
     * @param libraryItemId library item id
     * @param deploymentTarget deployment target
     * @param resourcePoolDeploymentSpec resource poll deployment spec
     * @return vm id
     */
    LibraryItemTypes.DeploymentResult deploy(String vmwareId, String libraryItemId,
        LibraryItemTypes.DeploymentTarget deploymentTarget,
        LibraryItemTypes.ResourcePoolDeploymentSpec resourcePoolDeploymentSpec);
}
