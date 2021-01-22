/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.item.ItemCreate;
import com.vmware.sample.model.library.item.ItemFindSpec;

import com.vmware.content.library.ItemModel;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Library item controller
 *
 * @since 2020-10-15
 */
@Validated
public interface LibraryItemController {
    /**
     * Create library item
     *
     * @param vmwareId vmware id
     * @param itemModel item model
     * @return item id
     */
    @PostMapping("/{vmwareId}/library/item")
    RestResult<String> create(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
            @RequestBody @Valid ItemCreate itemModel);

    /**
     * Find items
     *
     * @param vmwareId vmware id
     * @param itemFindSpec item find spec
     * @return item ids
     */
    @PostMapping("/{vmwareId}/library/item/action/query")
    RestResult<List<String>> find(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid ItemFindSpec itemFindSpec);

    /**
     * Delete item
     *
     * @param vmwareId vmware id
     * @param itemId item id
     * @return result
     */
    @DeleteMapping("/{vmwareId}/library/item/{itemId}")
    RestResult<Void> delete(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String itemId);

    /**
     * Get a library item
     *
     * @param vmwareId vmware id
     * @param itemId item id
     * @return library item
     */
    @GetMapping("/{vmwareId}/library/item/{itemId}")
    RestResult<ItemModel> get(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String itemId);
}
