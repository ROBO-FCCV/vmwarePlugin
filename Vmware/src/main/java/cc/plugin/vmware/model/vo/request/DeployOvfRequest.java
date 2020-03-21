/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 功能描述
 *
 * @since 2019 -09-16
 */
public class DeployOvfRequest {
    @ApiModelProperty(value = "item id", example = "db4d232c-b013-4f14-851b-409a11778b6a", required = true)
    @NotEmpty
    private String itemId;
    @ApiModelProperty(value = "资源池id", example = "resgroup-22",required = true)
    @NotEmpty
    private String resourcePoolId;
    @ApiModelProperty(value = "资源模板名称", example = "VcTransfer")
    @NotEmpty
    private String entityName;
    @ApiModelProperty(value = "数据存储ID", example = "datastore-10", required = true)
    @NotEmpty
    private String datastoreId;

    /**
     * Gets item id.
     *
     * @return the item id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Gets resource pool id.
     *
     * @return the resource pool id
     */
    public String getResourcePoolId() {
        return resourcePoolId;
    }

    /**
     * Gets entity name.
     *
     * @return the entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Gets datastore id.
     *
     * @return the datastore id
     */
    public String getDatastoreId() {
        return datastoreId;
    }

    /**
     * Sets item id.
     *
     * @param itemId the item id
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Sets resource pool id.
     *
     * @param resourcePoolId the resource pool id
     */
    public void setResourcePoolId(String resourcePoolId) {
        this.resourcePoolId = resourcePoolId;
    }

    /**
     * Sets entity name.
     *
     * @param entityName the entity name
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Sets datastore id.
     *
     * @param datastoreId the datastore id
     */
    public void setDatastoreId(String datastoreId) {
        this.datastoreId = datastoreId;
    }
}
