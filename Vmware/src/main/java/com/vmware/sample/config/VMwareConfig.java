/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import com.vmware.sample.model.VMware;
import com.vmware.sample.service.impl.VMwareAPI;
import com.vmware.sample.service.impl.VMwareSDK;
import com.vmware.sample.util.KmcUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private final KmcProperties kmcProperties;
    private final ApplicationContext applicationContext;

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
            decryptPassword(vMware);
            VMwareAPI vMwareAPI = new VMwareAPI(vMware);
            map.put(stringVMwareEntry.getKey(), vMwareAPI);
        }
        return map;
    }

    private void decryptPassword(VMware vMware) {
        if (kmcProperties.isEnabled()) {
            KmcUtils kmcUtils = applicationContext.getBean(KmcUtils.class);
            String decrypt = kmcUtils.decrypt(String.valueOf(vMware.getPassword()));
            vMware.setPassword(decrypt.toCharArray());
        }
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
            decryptPassword(vMware);
            VMwareSDK vMwareSDK = new VMwareSDK(vMware);
            map.put(stringVMwareEntry.getKey(), vMwareSDK);
        }
        return map;
    }
}
