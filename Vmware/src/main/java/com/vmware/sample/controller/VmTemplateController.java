/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.vmtemplate.DeploymentSpec;

import com.vmware.vcenter.ovf.LibraryItemTypes;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Vm template controller
 *
 * @since 2020-10-16
 */
@Validated
public interface VmTemplateController {
    /**
     * Using ovf template deploy
     *
     * @param vmwareId vmware id
     * @param libraryItemId libraryItemId
     * @param deploymentSpec resourcePollDeploySpec
     * @return deploy result
     */
    @PostMapping("/{vmwareId}/ovf/library-item/{libraryItemId}/action/deploy")
    RestResult<LibraryItemTypes.DeploymentResult> deploy(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String libraryItemId, @RequestBody @Valid DeploymentSpec deploymentSpec);
}
