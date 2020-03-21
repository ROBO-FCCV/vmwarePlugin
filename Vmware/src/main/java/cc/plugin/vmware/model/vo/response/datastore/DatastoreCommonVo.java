/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.datastore;

import io.swagger.annotations.ApiModelProperty;

/**
 * Vm查询返回结果
 *
 * @since 2019 -09-10
 */
public class DatastoreCommonVo {

    @ApiModelProperty(value = "数据存储类型", example = "Datastore", required = true)
    private String type;
    @ApiModelProperty(value = "数据存储ID", example = "datastore-262", required = true)
    private String value;

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
