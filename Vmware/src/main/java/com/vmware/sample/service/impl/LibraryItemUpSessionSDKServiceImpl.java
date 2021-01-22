/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.service.LibraryItemUpSessionService;

import com.vmware.content.library.item.UpdateSessionModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Library item up session sdk service
 *
 * @since 2020-10-15
 */
@Slf4j
@Service("library-item-up-session-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryItemUpSessionSDKServiceImpl implements LibraryItemUpSessionService {
    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public String create(String vmwareId, UpdateSessionModel updateSessionModel) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public void complete(String vmwareId, String sessionId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public void delete(String vmwareId, String sessionId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public void cancel(String vmwareId, String sessionId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public void fail(String vmwareId, String sessionId, String failedMsg) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public UpdateSessionModel get(String vmwareId, String sessionId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }
}
