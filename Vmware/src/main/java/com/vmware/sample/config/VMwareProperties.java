/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import com.vmware.sample.factory.YamlPropertySourceFactory;
import com.vmware.sample.model.PluginUser;
import com.vmware.sample.model.VMware;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * VMware properties
 *
 * @since 2020-09-16
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vmware")
@PropertySource(value = {"classpath:vmware.yml", "classpath:user.yml"}, factory = YamlPropertySourceFactory.class)
public class VMwareProperties {
    private Map<String, VMware> configs = new HashMap<>();
    private PluginUser user;
}
