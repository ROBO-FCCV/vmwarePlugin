/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vmtemplate;

import com.vmware.vapi.bindings.Structure;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Resource pool deploy spec
 *
 * @since 2020-10-16
 */
@Getter
@Setter
public class ResourcePoolDeploySpec {
    @NotBlank
    private String name;
    private String annotation;
    private boolean acceptAllEULA = true;
    private Map<String, String> networkMappings;
    private Map<String, StorageGroupMapping> storageMappings;
    @Pattern(regexp = "thin|thick|eagerZeroedThick")
    private String storageProvisioning;
    private String storageProfileId;
    private String locale;
    private List<String> flags;
    private List<Structure> additionalParameters;
    private String defaultDatastoreId;

    /**
     * Storage group mapping
     *
     * @since 2020-10-16
     */
    @Getter
    @Setter
    public static class StorageGroupMapping {
        @Pattern(regexp = "DATASTORE|STORAGE_PROFILE")
        private String type;
        private String datastoreId;
        private String storageProfileId;
        @Pattern(regexp = "thin|thick|eagerZeroedThick")
        private String provisioning;
    }
}
