/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library.item;

import lombok.Getter;
import lombok.Setter;

/**
 * Item find spec
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class ItemFindSpec {
    private String name;
    private String libraryId;
    private String type;
    private boolean cached = true;
}
