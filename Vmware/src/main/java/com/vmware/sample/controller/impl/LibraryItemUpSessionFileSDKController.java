/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.LibraryItemUpSessionFileController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.file.UpSessionFileCreate;
import com.vmware.sample.service.LibraryItemUpSessionFileService;

import com.vmware.content.library.item.updatesession.FileTypes;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Library item up session file api controller
 *
 * @since 2020-10-15
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/sdk")
public class LibraryItemUpSessionFileSDKController extends AbstractVMwareController
    implements LibraryItemUpSessionFileController {
    private final LibraryItemUpSessionFileService libraryItemUpSessionFileService;

    public LibraryItemUpSessionFileSDKController(@Qualifier("library-item-up-session-file-sdk-service")
        LibraryItemUpSessionFileService libraryItemUpSessionFileService) {
        this.libraryItemUpSessionFileService = libraryItemUpSessionFileService;
    }

    @Override
    public RestResult<FileTypes.Info> add(String vmwareId, String sessionId, UpSessionFileCreate upSessionFileCreate) {
        return RestResult.success(
            super.addLibraryItemFile(libraryItemUpSessionFileService, vmwareId, sessionId, upSessionFileCreate));
    }

    @Override
    public RestResult<FileTypes.Info> get(String vmwareId, String sessionId, String fileName) {
        return RestResult.success(libraryItemUpSessionFileService.get(vmwareId, sessionId, fileName));
    }

    @Override
    public RestResult<FileTypes.ValidationResult> validate(String vmwareId, String sessionId) {
        return RestResult.success(libraryItemUpSessionFileService.validate(vmwareId, sessionId));
    }

    @Override
    public RestResult<List<FileTypes.Info>> list(String vmwareId, String sessionId) {
        return RestResult.success(libraryItemUpSessionFileService.list(vmwareId, sessionId));
    }
}
