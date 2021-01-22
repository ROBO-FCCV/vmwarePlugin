/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Start application
 *
 * @since 2020-09-27
 */
@Slf4j
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Application extends SpringBootServletInitializer {
    /**
     * Run program
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
