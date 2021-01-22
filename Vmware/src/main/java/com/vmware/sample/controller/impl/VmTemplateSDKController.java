/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.VmTemplateController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.vmtemplate.DeploymentSpec;
import com.vmware.sample.service.VmTemplateService;

import com.vmware.vcenter.ovf.LibraryItemTypes;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Vm template Sdk controller
 *
 * @since 2020-10-21
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/sdk")
public class VmTemplateSDKController extends AbstractVMwareController implements VmTemplateController {
    private final VmTemplateService vmTemplateService;

    public VmTemplateSDKController(@Qualifier("vm-template-sdk-service") VmTemplateService vmTemplateService) {
        this.vmTemplateService = vmTemplateService;
    }

    @Override
    public RestResult<LibraryItemTypes.DeploymentResult> deploy(String vmwareId, String libraryItemId, DeploymentSpec deploymentSpec) {
        return RestResult.success(deployOvf(vmTemplateService, vmwareId, libraryItemId, deploymentSpec));
    }
}
