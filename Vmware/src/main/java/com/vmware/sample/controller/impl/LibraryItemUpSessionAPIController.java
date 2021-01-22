/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.LibraryItemUpSessionController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.upsession.UpSessionCreate;
import com.vmware.sample.service.LibraryItemUpSessionService;

import com.vmware.content.library.item.UpdateSessionModel;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Library item up session api controller
 *
 * @since 2020-10-15
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class LibraryItemUpSessionAPIController implements LibraryItemUpSessionController {
    private final LibraryItemUpSessionService libraryItemUpSessionService;

    public LibraryItemUpSessionAPIController(
        @Qualifier("library-item-up-session-api-service") LibraryItemUpSessionService libraryItemUpSessionService) {
        this.libraryItemUpSessionService = libraryItemUpSessionService;
    }

    @Override
    public RestResult<String> create(String vmwareId, UpSessionCreate updateSession) {
        UpdateSessionModel updateSessionModel = new UpdateSessionModel();
        updateSessionModel.setLibraryItemId(updateSession.getLibraryItemId());
        return RestResult.success(libraryItemUpSessionService.create(vmwareId, updateSessionModel));
    }

    @Override
    public RestResult<Void> complete(String vmwareId, String sessionId) {
        libraryItemUpSessionService.complete(vmwareId, sessionId);
        return RestResult.success(null);
    }

    @Override
    public RestResult<Void> delete(String vmwareId, String sessionId) {
        libraryItemUpSessionService.delete(vmwareId, sessionId);
        return RestResult.success(null);
    }

    @Override
    public RestResult<Void> cancel(String vmwareId, String sessionId) {
        libraryItemUpSessionService.cancel(vmwareId, sessionId);
        return RestResult.success(null);
    }

    @Override
    public RestResult<Void> fail(String vmwareId, String sessionId, String failedMsg) {
        libraryItemUpSessionService.fail(vmwareId, sessionId, failedMsg);
        return RestResult.success(null);
    }

    @Override
    public RestResult<UpdateSessionModel> get(String vmwareId, String sessionId) {
        return RestResult.success(libraryItemUpSessionService.get(vmwareId, sessionId));
    }
}
