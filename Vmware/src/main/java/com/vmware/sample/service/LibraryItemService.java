/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service;

import com.vmware.content.library.ItemModel;
import com.vmware.content.library.ItemTypes;

import java.util.List;

/**
 * Library item service
 *
 * @since 2020-10-14
 */
public interface LibraryItemService {
    /**
     * Create item
     *
     * @param vmwareId vmware id
     * @param itemModel item model
     * @return item id
     */
    String create(String vmwareId, ItemModel itemModel);

    /**
     * Find item
     *
     * @param vmwareId vmware id
     * @param findSpec item find spec
     * @return item ids
     */
    List<String> find(String vmwareId, ItemTypes.FindSpec findSpec);

    /**
     * Delete item
     *
     * @param vmwareId vmware id
     * @param itemId item id
     */
    void delete(String vmwareId, String itemId);

    /**
     * Get item
     *
     * @param vmwareId vmware id
     * @param itemId item id
     * @return item info
     */
    ItemModel get(String vmwareId, String itemId);
}
