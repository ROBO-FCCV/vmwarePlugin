/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 硬盘
 *
 * @since 2019 -09-09
 */
public class Disk {
    /**
     * 硬盘内存
     */
    @ApiModelProperty(value = "内存", example = "20", required = true)
    private int quantityGb;

    @ApiModelProperty(value = "数据存储Urn", example = "datastore-262", required = false)
    private String datastoreUrn;

    @ApiModelProperty(value = "数据存储Id", example = "datastore-262", required = false)
    private String datastoreId;

    @ApiModelProperty(value = "主机内存", example = "20000", required = false)
    private String isThin;

    /**
     * Gets quantity gb.
     *
     * @return the quantity gb
     */
    public int getQuantityGb() {
        return quantityGb;
    }

    /**
     * Sets quantity gb.
     *
     * @param quantityGb the quantity gb
     */
    public void setQuantityGb(int quantityGb) {
        this.quantityGb = quantityGb;
    }

    /**
     * Gets datastore urn.
     *
     * @return the datastore urn
     */
    public String getDatastoreUrn() {
        return datastoreUrn;
    }

    /**
     * Sets datastore urn.
     *
     * @param datastoreUrn the datastore urn
     */
    public void setDatastoreUrn(String datastoreUrn) {
        this.datastoreUrn = datastoreUrn;
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
     */
    public void setDatastoreId(String datastoreId) {
        this.datastoreId = datastoreId;
    }

    /**
     * Gets is thin.
     *
     * @return the is thin
     */
    public String getIsThin() {
        return isThin;
    }

    /**
     * Sets is thin.
     *
     * @param isThin the is thin
     */
    public void setIsThin(String isThin) {
        this.isThin = isThin;
    }
}
