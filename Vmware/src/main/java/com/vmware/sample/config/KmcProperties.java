/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import com.vmware.sample.util.KmcUtils;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kmc properties
 *
 * @since 2020-11-10
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com/huawei/kmc")
public class KmcProperties {
    private boolean enabled = false;

    /**
     * Kmc utils
     *
     * @return com.huawei.kmc utils
     */
    @Bean
    @ConditionalOnProperty(name = "com.huawei.kmc.enabled", havingValue = "true")
    public KmcUtils kmcUtils() {
        return new KmcUtils();
    }
}
