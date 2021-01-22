/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.network;

import lombok.Getter;
import lombok.Setter;

/**
 * Port group urn
 *
 * @since 2020-11-06
 */
@Getter
@Setter
public class PortGroupUrn {
    private String portgroupKey;
    private String portgroupId;
    private String portgroupType;
}
