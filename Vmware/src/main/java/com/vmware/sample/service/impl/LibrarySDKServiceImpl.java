/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.service.LibraryService;

import com.vmware.content.LibraryModel;
import com.vmware.content.LibraryTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Library sdk service implement
 *
 * @since 2020-10-21
 */
@Slf4j
@Service("library-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibrarySDKServiceImpl implements LibraryService {
    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public List<String> queryLibrary(String vmwareId, LibraryTypes.FindSpec findSpec) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public LibraryModel get(String vmwareId, String libraryId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public String createLocalLibrary(String vmwareId, LibraryModel libraryModel) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }
}
