/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.service.LibraryItemUpSessionFileService;

import com.vmware.content.library.item.updatesession.FileTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Library item up session file service implement
 *
 * @since 2020-10-15
 */
@Slf4j
@Service("library-item-up-session-file-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryItemUpSessionFileSDKServiceImpl implements LibraryItemUpSessionFileService {
    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public FileTypes.Info add(String vmwareId, String sessionId, FileTypes.AddSpec addSpec) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public FileTypes.Info get(String vmwareId, String updateSessionFileId, String fileName) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public FileTypes.ValidationResult validate(String vmwareId, String sessionId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public List<FileTypes.Info> list(String vmwareId, String sessionId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }
}
