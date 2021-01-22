/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;

import com.vmware.vapi.bindings.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract api service
 *
 * @since 2020-09-25
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VmwareAPIClient {
    private final ConcurrentHashMap<String, VMwareAPI> vMwareAPIMap;

    /**
     * Get stub service
     *
     * @param vmwareId vmware id
     * @param clazz class
     * @param <T> stub service class
     * @return stub service
     */
    protected <T extends Service> T getStubConfiguration(String vmwareId, Class<T> clazz) {
        if (!vMwareAPIMap.containsKey(vmwareId)) {
            throw new PluginException(RestCodeEnum.VMWARE_NOT_EXISTED);
        }
        VMwareAPI vMwareAPI = vMwareAPIMap.get(vmwareId);
        boolean result = vMwareAPI.checkSession();
        if (!result) {
            log.error("Session was empire.Maybe password was changed by remote.");
            throw new PluginException(RestCodeEnum.CONNECTION_EXCEPTION);
        }
        return vMwareAPI.getStubConfiguration(clazz);
    }
}
