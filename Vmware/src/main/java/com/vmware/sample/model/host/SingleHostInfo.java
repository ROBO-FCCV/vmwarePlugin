/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.host;

import lombok.Getter;
import lombok.Setter;

/**
 * Single host
 *
 * @since 2020-11-02
 */
@Getter
@Setter
public class SingleHostInfo extends HostInfo {
    private HostInfo hostInfo;
}
