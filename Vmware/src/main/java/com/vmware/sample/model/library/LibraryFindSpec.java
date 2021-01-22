/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Library find spec
 *
 * @since 2020-10-13
 */
@Getter
@Setter
public class LibraryFindSpec {
    @NotBlank
    private String name;
    @Pattern(regexp = "LOCAL|SUBSCRIBED")
    private String type = "LOCAL";
}
