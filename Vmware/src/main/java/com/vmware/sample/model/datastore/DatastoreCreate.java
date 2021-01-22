/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.datastore;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * Datastore
 *
 * @since 2020-09-21
 */
@Getter
@Setter
public class DatastoreCreate {
    @NotBlank
    @Length(max = 80)
    private String datastoreName;
    @NotBlank
    private String key;
}
