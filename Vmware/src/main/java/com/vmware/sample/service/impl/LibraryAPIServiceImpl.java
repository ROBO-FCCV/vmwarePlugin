/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.service.LibraryService;

import com.vmware.content.Library;
import com.vmware.content.LibraryModel;
import com.vmware.content.LibraryTypes;
import com.vmware.content.LocalLibrary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Library api service implement
 *
 * @since 2020-10-21
 */
@Slf4j
@Service("library-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryAPIServiceImpl implements LibraryService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public List<String> queryLibrary(String vmwareId, LibraryTypes.FindSpec findSpec) {
        Library library = vmwareAPIClient.getStubConfiguration(vmwareId, Library.class);
        return library.find(findSpec);
    }

    @Override
    public LibraryModel get(String vmwareId, String libraryId) {
        LocalLibrary library = vmwareAPIClient.getStubConfiguration(vmwareId, LocalLibrary.class);
        return library.get(libraryId);
    }

    @Override
    public String createLocalLibrary(String vmwareId, LibraryModel libraryModel) {
        LocalLibrary localLibrary = vmwareAPIClient.getStubConfiguration(vmwareId, LocalLibrary.class);
        return localLibrary.create(UUID.randomUUID().toString(), libraryModel);
    }
}
