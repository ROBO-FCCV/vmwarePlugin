/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * Network basic info
 *
 * @since 2020-10-23
 */
@Getter
@Setter
public class NetworkBasic {
    /**
     * Network type
     */
    private String type;
    /**
     * Network name
     */
    private String name;
    /**
     * connected
     */
    private boolean connected;
}
