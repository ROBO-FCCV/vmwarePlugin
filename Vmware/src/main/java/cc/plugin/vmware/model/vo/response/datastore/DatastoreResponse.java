/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.datastore;

import io.swagger.annotations.ApiModelProperty;

/**
 * The type Datastore response.
 *
 * @since 2019 -10-15
 */
public class DatastoreResponse {
    @ApiModelProperty(value = "数据存储标识", example = "datastore-10", required = true)
    private String datastoreId;

    @ApiModelProperty(value = "数据存储名称", example = "datastore1", required = true)
    private String name;

    @ApiModelProperty(value = "数据存储状态", example = "GREEN", required = true)
    private String status;

    @ApiModelProperty(value = "数据存储总容量（Byte）", example = "230854492160", required = true)
    private long totalSize;

    @ApiModelProperty(value = "数据存储空闲容量（Byte）", example = "31091326976", required = true)
    private long freeSize;

    /**
     * Gets datastore id.
     *
     * @return the datastore id
     */
    public String getDatastoreId() {
        return datastoreId;
    }

    /**
     * Sets datastore id.
     *
     * @param datastoreId the datastore id
     * @return the datastore id
     */
    public DatastoreResponse setDatastoreId(String datastoreId) {
        this.datastoreId = datastoreId;
        return this;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public DatastoreResponse setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     * @return the status
     */
    public DatastoreResponse setStatus(String status) {
        this.status = status;
        return this;
    }

    /**
     * Gets total size.
     *
     * @return the total size
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Sets total size.
     *
     * @param totalSize the total size
     * @return the total size
     */
    public DatastoreResponse setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    /**
     * Gets free size.
     *
     * @return the free size
     */
    public long getFreeSize() {
        return freeSize;
    }

    /**
     * Sets free size.
     *
     * @param freeSize the free size
     * @return the free size
     */
    public DatastoreResponse setFreeSize(long freeSize) {
        this.freeSize = freeSize;
        return this;
    }
}
