/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * RDM info
 *
 * @since 2020-10-13
 */
@Getter
@Setter
public class RDMInfo {
    /**
     * name
     */
    @NotBlank
    private String deviceName;

}
