/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library.upsession;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Item up session create spec
 *
 * @since 2020-10-15
 */
@Setter
@Getter
public class UpSessionCreate {
    @NotBlank
    private String libraryItemId;
}
