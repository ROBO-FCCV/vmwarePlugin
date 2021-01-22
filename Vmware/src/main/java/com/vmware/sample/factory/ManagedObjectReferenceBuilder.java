/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.factory;

import com.vmware.vim25.ManagedObjectReference;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Managed object reference Builder
 *
 * @since 2020-09-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ManagedObjectReferenceBuilder {
    /**
     * Instance
     */
    public static final ManagedObjectReferenceBuilder MANAGED_OBJECT_REFERENCE_BUILDER
        = new ManagedObjectReferenceBuilder();

    /**
     * Get instance
     *
     * @return instance
     */
    public static ManagedObjectReferenceBuilder getInstance() {
        return MANAGED_OBJECT_REFERENCE_BUILDER;
    }

    private String type;
    private String value;

    /**
     * Type builder
     *
     * @param type type
     * @return builder
     */
    public ManagedObjectReferenceBuilder type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Value builder
     *
     * @param value value
     * @return builder
     */
    public ManagedObjectReferenceBuilder value(String value) {
        this.value = value;
        return this;
    }

    /**
     * Build
     *
     * @return managedObjectReference
     */
    public ManagedObjectReference build() {
        ManagedObjectReference managedObjectReference = new ManagedObjectReference();
        managedObjectReference.setType(this.type);
        managedObjectReference.setValue(this.value);
        return managedObjectReference;
    }
}
