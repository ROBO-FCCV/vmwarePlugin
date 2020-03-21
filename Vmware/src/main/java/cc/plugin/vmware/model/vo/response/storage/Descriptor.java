/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

import io.swagger.annotations.ApiModelProperty;

/**
 * HostDiskDimensionsLba
 *
 * @since 2019 -09-16
 */
public class Descriptor {

    @ApiModelProperty(value = "ID", example = "naa.6ac751de91b7b00024cc07e60bfe84e6", required = true)
    private String id;
    @ApiModelProperty(value = "特性", example = "highQuality", required = true)
    private String quality;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets quality.
     *
     * @return the quality
     */
    public String getQuality() {
        return quality;
    }

    /**
     * Sets quality.
     *
     * @param quality the quality
     */
    public void setQuality(String quality) {
        this.quality = quality;
    }
}
