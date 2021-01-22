/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.validation.constraints.NotEmpty;

/**
 * Plugin user
 *
 * @since 2020-09-16
 */
@Getter
@Setter
public class PluginUser {
    private String username = "admin";
    @NotEmpty
    private char[] password;
    private String[] roles = new String[] {"admin"};
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration empire = Duration.of(1L, ChronoUnit.HOURS);
}
