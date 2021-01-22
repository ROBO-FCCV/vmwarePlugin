/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.vcenter.VcenterBasicInfo;

/**
 * Vcenter interface
 *
 * @since 2020-09-16
 */
public interface VcenterService {
    /**
     * Query vcenter basic info
     *
     * @param vmwareId vmware id
     * @return vcenter basic info
     */
    VcenterBasicInfo queryVcenterBasicInfo(String vmwareId);
}
