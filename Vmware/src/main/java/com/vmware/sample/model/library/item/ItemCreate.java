/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library.item;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Library item create spec
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class ItemCreate {
    @NotBlank
    private String libraryId;
    private String description;
    @NotBlank
    @Length(max = 80)
    private String name;
    @Pattern(regexp = "file|ovf")
    private String type;
}
