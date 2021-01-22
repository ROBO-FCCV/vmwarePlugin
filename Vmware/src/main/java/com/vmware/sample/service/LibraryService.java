/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.content.LibraryModel;
import com.vmware.content.LibraryTypes;

import java.util.List;

/**
 * Library service
 *
 * @since 2020-10-13
 */
public interface LibraryService {
    /**
     * Query library
     *
     * @param vmwareId vmware id
     * @param findSpec findSpec
     * @return library
     */
    List<String> queryLibrary(String vmwareId, LibraryTypes.FindSpec findSpec);

    /**
     * Get library
     *
     * @param vmwareId vmware id
     * @param libraryId library id
     * @return library
     */
    LibraryModel get(String vmwareId, String libraryId);

    /**
     * Create local library
     *
     * @param vmwareId vmware id
     * @param libraryModel library model
     * @return library id
     */
    String createLocalLibrary(String vmwareId, LibraryModel libraryModel);
}
