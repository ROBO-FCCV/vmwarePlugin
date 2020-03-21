/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.host;

import io.swagger.annotations.ApiModelProperty;

/**
 * host返回结果
 *
 * @since 2019 -09-10
 */
public class ImportResourceVo {
    @ApiModelProperty(value = "数据存储ID", example = "datastore-262", required = true)
    private String dataStoreId;
    @ApiModelProperty(value = "资源池ID", example = "resgroup-113", required = true)
    private String resourcePoolId;

    /**
     * Gets data store id.
     *
     * @return the data store id
     */
    public String getDataStoreId() {
        return dataStoreId;
    }

    /**
     * Sets data store id.
     *
     * @param dataStoreId the data store id
     */
    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
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
     * Sets resource pool id.
     *
     * @param resourcePoolId the resource pool id
     */
    public void setResourcePoolId(String resourcePoolId) {
        this.resourcePoolId = resourcePoolId;
    }
}
