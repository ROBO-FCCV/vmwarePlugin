/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.service.LibraryItemUpSessionService;

import com.vmware.content.library.item.UpdateSession;
import com.vmware.content.library.item.UpdateSessionModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Library item up session api service
 *
 * @since 2020-10-15
 */
@Slf4j
@Service("library-item-up-session-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryItemUpSessionAPIServiceImpl implements LibraryItemUpSessionService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public String create(String vmwareId, UpdateSessionModel updateSessionModel) {
        UpdateSession updateSession = vmwareAPIClient.getStubConfiguration(vmwareId, UpdateSession.class);
        return updateSession.create(UUID.randomUUID().toString(), updateSessionModel);
    }

    @Override
    public void complete(String vmwareId, String sessionId) {
        UpdateSession updateSession = vmwareAPIClient.getStubConfiguration(vmwareId, UpdateSession.class);
        updateSession.complete(sessionId);
    }

    @Override
    public void delete(String vmwareId, String sessionId) {
        UpdateSession updateSession = vmwareAPIClient.getStubConfiguration(vmwareId, UpdateSession.class);
        updateSession.delete(sessionId);
    }

    @Override
    public void cancel(String vmwareId, String sessionId) {
        UpdateSession updateSession = vmwareAPIClient.getStubConfiguration(vmwareId, UpdateSession.class);
        updateSession.cancel(sessionId);
    }

    @Override
    public void fail(String vmwareId, String sessionId, String failedMsg) {
        UpdateSession updateSession = vmwareAPIClient.getStubConfiguration(vmwareId, UpdateSession.class);
        updateSession.fail(sessionId, failedMsg);
    }

    @Override
    public UpdateSessionModel get(String vmwareId, String sessionId) {
        UpdateSession updateSession = vmwareAPIClient.getStubConfiguration(vmwareId, UpdateSession.class);
        return updateSession.get(sessionId);
    }
}
