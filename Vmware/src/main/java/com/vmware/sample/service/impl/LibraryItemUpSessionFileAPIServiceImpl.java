/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.service.LibraryItemUpSessionFileService;

import com.vmware.content.library.item.updatesession.File;
import com.vmware.content.library.item.updatesession.FileTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Library item up session file api service implement
 *
 * @since 2020-10-15
 */
@Slf4j
@Service("library-item-up-session-file-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryItemUpSessionFileAPIServiceImpl implements LibraryItemUpSessionFileService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public FileTypes.Info add(String vmwareId, String sessionId, FileTypes.AddSpec addSpec) {
        File file = vmwareAPIClient.getStubConfiguration(vmwareId, File.class);
        return file.add(sessionId, addSpec);
    }

    @Override
    public FileTypes.Info get(String vmwareId, String updateSessionFileId, String fileName) {
        File file = vmwareAPIClient.getStubConfiguration(vmwareId, File.class);
        return file.get(updateSessionFileId, fileName);
    }

    @Override
    public FileTypes.ValidationResult validate(String vmwareId, String sessionId) {
        File file = vmwareAPIClient.getStubConfiguration(vmwareId, File.class);
        return file.validate(sessionId);
    }

    @Override
    public List<FileTypes.Info> list(String vmwareId, String sessionId) {
        File file = vmwareAPIClient.getStubConfiguration(vmwareId, File.class);
        return file.list(sessionId);
    }
}
