/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.network;

import lombok.Getter;
import lombok.Setter;

/**
 * Network info
 *
 * @since 2020-09-22
 */
@Getter
@Setter
public class NetworkInfo {
    private String vlanId;
    private String name;
    private String type;
}
