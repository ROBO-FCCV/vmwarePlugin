/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.service.LibraryItemService;

import com.vmware.content.library.Item;
import com.vmware.content.library.ItemModel;
import com.vmware.content.library.ItemTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Library item api service implement
 *
 * @since 2020-10-21
 */
@Slf4j
@Service("library-item-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LibraryItemAPIServiceImpl implements LibraryItemService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public String create(String vmwareId, ItemModel itemModel) {
        Item stubConfiguration = vmwareAPIClient.getStubConfiguration(vmwareId, Item.class);
        return stubConfiguration.create(UUID.randomUUID().toString(), itemModel);
    }

    @Override
    public List<String> find(String vmwareId, ItemTypes.FindSpec findSpec) {
        Item item = vmwareAPIClient.getStubConfiguration(vmwareId, Item.class);
        return item.find(findSpec);
    }

    @Override
    public void delete(String vmwareId, String itemId) {
        Item item = vmwareAPIClient.getStubConfiguration(vmwareId, Item.class);
        item.delete(itemId);
    }

    @Override
    public ItemModel get(String vmwareId, String itemId) {
        Item item = vmwareAPIClient.getStubConfiguration(vmwareId, Item.class);
        return item.get(itemId);
    }
}
