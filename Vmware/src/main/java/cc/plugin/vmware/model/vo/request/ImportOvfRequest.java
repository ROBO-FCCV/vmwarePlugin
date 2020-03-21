/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * 功能描述
 *
 * @since 2019 -09-16
 */
public class ImportOvfRequest {
    @ApiModelProperty(value = "主机名称", example = "192.0.2.0", required = true)
    @NotEmpty
    private String host;
    @ApiModelProperty(value = "Ovf url", example = "http://192.0.2.0/DNS_EulerSP3_VCenter.ovf")
    private String ovfUrl;
    @ApiModelProperty(value = "Ovf文件名称", example = "test5")
    @NotEmpty
    private String itemName;
    @ApiModelProperty(value = "Ovf文件全名", example = "test5.ovf")
    private String itemOvfName;
    @ApiModelProperty(value = "数据存储ID", example = "datastore-10", required = true)
    @NotEmpty
    private String datastoreId;

    @ApiModelProperty(value = "磁盘数", example = "2")
    private String diskNum;

    @ApiModelProperty(value = "导入路径", example = "/home/guest/VcTransfer")
    private String path;

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets host.
     *
     * @param host the host
     * @return the host
     */
    public ImportOvfRequest setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Gets ovf url.
     *
     * @return the ovf url
     */
    public String getOvfUrl() {
        return ovfUrl;
    }

    /**
     * Sets ovf url.
     *
     * @param ovfUrl the ovf url
     * @return the ovf url
     */
    public ImportOvfRequest setOvfUrl(String ovfUrl) {
        this.ovfUrl = ovfUrl;
        return this;
    }

    /**
     * Gets item name.
     *
     * @return the item name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets item name.
     *
     * @param itemName the item name
     * @return the item name
     */
    public ImportOvfRequest setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    /**
     * Gets item ovf name.
     *
     * @return the item ovf name
     */
    public String getItemOvfName() {
        return itemOvfName;
    }

    /**
     * Sets item ovf name.
     *
     * @param itemOvfName the item ovf name
     * @return the item ovf name
     */
    public ImportOvfRequest setItemOvfName(String itemOvfName) {
        this.itemOvfName = itemOvfName;
        return this;
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
     * Sets datastore id.
     *
     * @param datastoreId the datastore id
     * @return the datastore id
     */
    public ImportOvfRequest setDatastoreId(String datastoreId) {
        this.datastoreId = datastoreId;
        return this;
    }

    /**
     * Sets disk num.
     *
     * @param diskNum the disk num
     */
    public void setDiskNum(String diskNum) {
        this.diskNum = diskNum;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets disk num.
     *
     * @return the disk num
     */
    public String getDiskNum() {
        return diskNum;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }
}
