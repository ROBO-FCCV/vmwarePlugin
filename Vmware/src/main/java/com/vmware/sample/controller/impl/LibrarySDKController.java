/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.LibraryController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.LibraryFindSpec;
import com.vmware.sample.model.library.LocalLibraryModel;
import com.vmware.sample.service.LibraryService;

import com.vmware.content.LibraryModel;
import com.vmware.content.LibraryTypes;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Library sdk controller
 *
 * @since 2020-10-15
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/sdk")
public class LibrarySDKController implements LibraryController {
    private final LibraryService libraryService;

    public LibrarySDKController(@Qualifier("library-sdk-service") LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Override
    public RestResult<List<String>> find(String vmwareId, LibraryFindSpec libraryFindSpec) {
        LibraryTypes.FindSpec findSpec = new LibraryTypes.FindSpec();
        findSpec.setName(libraryFindSpec.getName());
        findSpec.setType(LibraryModel.LibraryType.valueOf(libraryFindSpec.getType()));
        return RestResult.success(libraryService.queryLibrary(vmwareId, findSpec));
    }

    @Override
    public RestResult<LibraryModel> get(String vmwareId, String libraryId) {
        return RestResult.success(libraryService.get(vmwareId, libraryId));
    }

    @Override
    public RestResult<String> createLocalLibrary(String vmwareId, LocalLibraryModel localLibraryModel) {
        LibraryModel libraryModel = new LibraryModel();
        BeanUtils.copyProperties(localLibraryModel, libraryModel);
        return RestResult.success(libraryService.createLocalLibrary(vmwareId, libraryModel));
    }
}
