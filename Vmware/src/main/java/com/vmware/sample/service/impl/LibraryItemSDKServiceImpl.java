/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.service.LibraryItemService;

import com.vmware.content.library.ItemModel;
import com.vmware.content.library.ItemTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Library item sdk service implement
 *
 * @since 2020-10-21
 */
@Slf4j
@Service("library-item-sdk-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryItemSDKServiceImpl implements LibraryItemService {
    private final VmwareSDKClient vmwareSDKClient;

    @Override
    public String create(String vmwareId, ItemModel itemModel) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public List<String> find(String vmwareId, ItemTypes.FindSpec findSpec) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public void delete(String vmwareId, String itemId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }

    @Override
    public ItemModel get(String vmwareId, String itemId) {
        throw new PluginException(RestCodeEnum.SDK_NONE_IMPLEMENT);
    }
}
