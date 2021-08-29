/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.controller.LibraryItemController;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.item.ItemCreate;
import com.vmware.sample.model.library.item.ItemFindSpec;
import com.vmware.sample.service.LibraryItemService;

import com.vmware.content.library.ItemModel;
import com.vmware.content.library.ItemTypes;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Library item controller API
 *
 * @author wWX627822
 * @since 2020-10-22
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/v1/api")
public class LibraryItemAPIController implements LibraryItemController {
    private final LibraryItemService libraryItemService;

    public LibraryItemAPIController(@Qualifier("library-item-api-service") LibraryItemService libraryItemService) {
        this.libraryItemService = libraryItemService;
    }

    @Override
    public RestResult<String> create(String vmwareId, ItemCreate itemCreate) {
        ItemModel itemModel = new ItemModel();
        itemModel.setLibraryId(itemCreate.getLibraryId());
        itemModel.setDescription(itemCreate.getDescription());
        itemModel.setName(itemCreate.getName());
        itemModel.setType(itemCreate.getType());
        return RestResult.success(libraryItemService.create(vmwareId, itemModel));
    }

    @Override
    public RestResult<List<String>> find(String vmwareId, ItemFindSpec itemFindSpec) {
        ItemTypes.FindSpec findSpec = new ItemTypes.FindSpec();
        findSpec.setLibraryId(itemFindSpec.getLibraryId());
        findSpec.setName(itemFindSpec.getName());
        findSpec.setType(itemFindSpec.getType());
        return RestResult.success(libraryItemService.find(vmwareId, findSpec));
    }

    @Override
    public RestResult<Void> delete(String vmwareId, String itemId) {
        libraryItemService.delete(vmwareId, itemId);
        return RestResult.success(null);
    }

    @Override
    public RestResult<ItemModel> get(String vmwareId, String itemId) {
        return RestResult.success(libraryItemService.get(vmwareId, itemId));
    }
}
