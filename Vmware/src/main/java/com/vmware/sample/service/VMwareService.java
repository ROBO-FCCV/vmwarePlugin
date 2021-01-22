/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.sample.model.VMware;

import java.util.List;

/**
 * VMware service
 *
 * @since 2020-09-25
 */
public interface VMwareService {
    /**
     * Add VMware and dynamic bean
     *
     * @param vmware vmware
     * @return VMware id
     */
    String add(VMware vmware);

    /**
     * Querying Information About a Single VMware
     *
     * @param vmwareId VMware id
     * @return VMware Info
     */
    VMware getVMware(String vmwareId);

    /**
     * Query the connected VMware.
     *
     * @return VMware
     */
    List<VMware> list();

    /**
     * Deleting the connected VMware
     *
     * @param vmwareId VMware ID
     * @return VMware ID
     */
    String del(String vmwareId);

    /**
     * Modifying VMware Information
     *
     * @param vmwareId VMware ID
     * @param vMware vmware
     * @return VMware ID
     */
    String modify(String vmwareId, VMware vMware);

    /**
     * Find vmware by id
     *
     * @param vmwareId vmware id
     * @return vmware
     */
    VMware get(String vmwareId);
}
