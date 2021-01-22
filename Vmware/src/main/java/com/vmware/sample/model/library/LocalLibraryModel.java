/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Local library
 *
 * @since 2020-10-14
 */
@Getter
@Setter
public class LocalLibraryModel {
    @NotBlank
    private String name;
    @NotEmpty
    private List<StorageBacking> storageBackings;

    /**
     * Storage backing
     *
     * @since 2020-10-14
     */
    @Getter
    @Setter
    public static class StorageBacking {
        @NotBlank
        @Pattern(regexp = "datastore-\\d+")
        private String datastoreId;
    }
}
