/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.datastore;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * Datastore实体类
 *
 * @since 2019 -09-10
 */
public class DatastoreInfo {
    /**
     * 主机名称
     */
    @NotEmpty
    @ApiModelProperty(value = "主机名称", example = "host-1", required = true)
    private String hostName;

    /**
     * datacenter名称
     */
    @NotEmpty
    @ApiModelProperty(value = "数据中心名称", example = "datacenter", required = true)
    private String datacenterName;

    /**
     * datastore名称
     */
    @NotEmpty
    @ApiModelProperty(value = "数据存储名称", example = "datastore3", required = true)
    private String datastoreName;

    /**
     * 硬盘连接key
     */
    @NotEmpty
    @ApiModelProperty(value = "硬盘连接key",
        example = "key-vim.host.ScsiDisk-02000000006ac751de91b7b00024cc07e60bfe84e6415641474f20", required = true)
    private String key;

    /**
     * Gets host name.
     *
     * @return the host name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets host name.
     *
     * @param hostName the host name
     * @return the host name
     */
    public DatastoreInfo setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    /**
     * Gets datacenter name.
     *
     * @return the datacenter name
     */
    public String getDatacenterName() {
        return datacenterName;
    }

    /**
     * Sets datacenter name.
     *
     * @param datacenterName the datacenter name
     * @return the datacenter name
     */
    public DatastoreInfo setDatacenterName(String datacenterName) {
        this.datacenterName = datacenterName;
        return this;
    }

    /**
     * Gets datastore name.
     *
     * @return the datastore name
     */
    public String getDatastoreName() {
        return datastoreName;
    }

    /**
     * Sets datastore name.
     *
     * @param datastoreName the datastore name
     * @return the datastore name
     */
    public DatastoreInfo setDatastoreName(String datastoreName) {
        this.datastoreName = datastoreName;
        return this;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     * @return the key
     */
    public DatastoreInfo setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public String toString() {
        return "DatastoreInfo{" + "hostName='" + hostName + '\'' + ", datacenterName='" + datacenterName + '\''
            + ", datastoreName='" + datastoreName + '\'' + '}';
    }
}
