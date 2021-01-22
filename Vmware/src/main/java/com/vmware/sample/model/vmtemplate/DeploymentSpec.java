/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vmtemplate;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Deployment target create
 *
 * @since 2020-10-16
 */
@Getter
@Setter
public class DeploymentSpec {
    @NotBlank
    private String resourcePoolId;
    @Valid
    @NotNull
    private ResourcePoolDeploySpec resourcePoolDeploySpec;
}
