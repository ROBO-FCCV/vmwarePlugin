/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import com.vmware.sample.model.VMware;
import com.vmware.sample.service.impl.VMwareAPI;
import com.vmware.sample.service.impl.VMwareSDK;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VMware config
 *
 * @since 2020-09-27
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VMwareConfig {
    private final VMwareProperties vMwareProperties;

    /**
     * VMwareAPIs
     *
     * @return VMwareAPIs
     */
    @Bean
    public ConcurrentHashMap<String, VMwareAPI> stringVMwareAPIMap() {
        ConcurrentHashMap<String, VMwareAPI> map = new ConcurrentHashMap<>();
        for (Map.Entry<String, VMware> stringVMwareEntry : vMwareProperties.getConfigs().entrySet()) {
            VMware vMware = stringVMwareEntry.getValue();
            VMwareAPI vMwareAPI = new VMwareAPI(vMware);
            map.put(stringVMwareEntry.getKey(), vMwareAPI);
        }
        return map;
    }

    /**
     * VMwareSDKs
     *
     * @return VMwareSDKs
     */
    @Bean
    public ConcurrentHashMap<String, VMwareSDK> stringVMwareSDKMap() {
        ConcurrentHashMap<String, VMwareSDK> map = new ConcurrentHashMap<>();
        for (Map.Entry<String, VMware> stringVMwareEntry : vMwareProperties.getConfigs().entrySet()) {
            VMware vMware = stringVMwareEntry.getValue();
            VMwareSDK vMwareSDK = new VMwareSDK(vMware);
            map.put(stringVMwareEntry.getKey(), vMwareSDK);
        }
        return map;
    }
}
