/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Select virtual machine get Net info
 *
 * @since 2020-09-15
 */
@Setter
@Getter
public class NetToInfo {
    /**
     * ip list
     */
    private List<String> ip;

    /**
     * name
     */
    private String name;
}
