/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.config;

import com.vmware.sample.filter.JWTAuthorFilter;
import com.vmware.sample.service.impl.VMwareUserDetailServiceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * VMware security config
 *
 * @since 2020-09-16
 */
@Configuration
@EnableAsync
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VMwareSecurityConfig extends WebSecurityConfigurerAdapter {
    private final VMwareUserDetailServiceImpl vMwareUserDetailService;
    private final JWTAuthorFilter jwtAuthorFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(vMwareUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .exceptionHandling()
            .authenticationEntryPoint(
                (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized"))
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/login", "/version", "/init")
            .permitAll()
            .anyRequest()
            .authenticated();
        http.addFilterBefore(jwtAuthorFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
