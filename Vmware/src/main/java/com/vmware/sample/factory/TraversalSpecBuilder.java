/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.factory;

import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TraversalSpec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Traversal spec Builder
 *
 * @since 2020-09-15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraversalSpecBuilder {
    /**
     * Instance
     */
    public static final TraversalSpecBuilder TRAVERSAL_SPEC_BUILDER = new TraversalSpecBuilder();

    /**
     * Get instance
     *
     * @return instance
     */
    public static TraversalSpecBuilder getInstance() {
        return TRAVERSAL_SPEC_BUILDER;
    }

    private String type;
    private String path;
    private boolean skip;
    private String name;
    private List<SelectionSpec> selectionSpecs;

    /**
     * Type builder
     *
     * @param type type
     * @return builder
     */
    public TraversalSpecBuilder type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Path builder
     *
     * @param path path
     * @return builder
     */
    public TraversalSpecBuilder path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Skip builder
     *
     * @param skip skip
     * @return builder
     */
    public TraversalSpecBuilder skip(boolean skip) {
        this.skip = skip;
        return this;
    }

    /**
     * Name builder
     *
     * @param name name
     * @return builder
     */
    public TraversalSpecBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Selection builder
     *
     * @param selectionSpecs selection specs
     * @return builder
     */
    public TraversalSpecBuilder selectionSpecs(List<SelectionSpec> selectionSpecs) {
        this.selectionSpecs = selectionSpecs;
        return this;
    }

    /**
     * Build
     *
     * @return traversalSpec
     */
    public TraversalSpec build() {
        TraversalSpec traversalSpec = new TraversalSpec();
        traversalSpec.setType(this.type);
        traversalSpec.setPath(this.path);
        traversalSpec.setSkip(this.skip);
        traversalSpec.setName(this.name);
        traversalSpec.getSelectSet()
            .addAll(this.selectionSpecs == null ? Collections.emptyList() : this.selectionSpecs);
        return traversalSpec;
    }
}
