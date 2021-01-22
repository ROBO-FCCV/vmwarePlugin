/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.config.VMwareProperties;
import com.vmware.sample.model.PluginUser;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom User Implementation
 *
 * @since 2020-09-14
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VMwareUserDetailServiceImpl implements UserDetailsService {
    private final VMwareProperties vMwareProperties;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PluginUser user = vMwareProperties.getUser();
        if (user.getUsername().equals(username)) {
            return User.builder()
                .username(user.getUsername())
                .password(String.valueOf(user.getPassword()))
                .roles(user.getRoles())
                .build();
        }
        throw new UsernameNotFoundException("User Not found");
    }
}
