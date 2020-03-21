/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import com.vmware.connection.helpers.builders.TraversalSpecBuilder;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.TraversalSpec;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The type Spec factory.
 *
 * @since 2019 -09-19
 */
public class SpecFactory {

    /**
     * Instantiates a new Spec factory.
     */
    public SpecFactory() {
    }

    /**
     * Create traversal spec traversal spec.
     *
     * @param name the name
     * @param type the type
     * @param path the path
     * @param skip the skip
     * @param specs the specs
     * @return the traversal spec
     */
    public static TraversalSpec createTraversalSpec(String name, String type, String path, boolean skip,
        List<SelectionSpec> specs) {
        TraversalSpec ts = createTraversalSpec(name, type, path, skip);
        if (Objects.nonNull(specs)) {
            ts.getSelectSet().addAll(specs);
        }
        return ts;
    }

    /**
     * Create traversal spec traversal spec.
     *
     * @param name the name
     * @param type the type
     * @param path the path
     * @param skip the skip
     * @return the traversal spec
     */
    public static TraversalSpec createTraversalSpec(String name, String type, String path, boolean skip) {
        TraversalSpec ts = new TraversalSpecBuilder().name(name).skip(skip).type(type).path(type);
        return ts;
    }

    /**
     * Create selection spec selection spec.
     *
     * @param name the name
     * @return the selection spec
     */
    public static SelectionSpec createSelectionSpec(String name) {
        SelectionSpec spec;
        spec = new SelectionSpec();
        spec.setName(name);
        return spec;
    }

    /**
     * Create property spec property spec.
     *
     * @param type the type
     * @param skip the skip
     * @param selection the selection
     * @return the property spec
     */
    public static PropertySpec createPropertySpec(String type, boolean skip, String... selection) {
        PropertySpec spec = new PropertySpec();
        spec.setAll(skip);
        spec.setType(type);
        if (Objects.nonNull(selection)) {
            spec.getPathSet().addAll(Arrays.asList(selection));
        }
        return spec;
    }

    /**
     * Create object spec object spec.
     *
     * @param mor the mor
     * @param skip the skip
     * @param specs the specs
     * @return the object spec
     */
    public static ObjectSpec createObjectSpec(ManagedObjectReference mor, boolean skip, SelectionSpec... specs) {
        ObjectSpec os = new ObjectSpec();
        os.setObj(mor);
        os.setSkip(skip);
        if (Objects.nonNull(specs)) {
            os.getSelectSet().addAll(Arrays.asList(specs));
        }
        return os;
    }
}