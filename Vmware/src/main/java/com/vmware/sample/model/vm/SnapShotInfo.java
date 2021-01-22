/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Virtual machine snapshot
 *
 * @since 2020-09-17
 */
@Setter
@Getter
public class SnapShotInfo {
    /**
     * virtual machine vmId
     */
    @NotBlank
    private String vmId;

    /**
     * virtual machine description
     */
    private String description;

    /**
     * snapshot name
     */
    @NotBlank
    @Length(max = 80)
    private String name;
}
