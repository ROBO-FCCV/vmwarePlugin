
/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 功能描述
 *
 * @since 2019 -10-10
 */
public class PortGroupUrn {
    @ApiModelProperty(value = "端口组的秘钥", example = "", required = true)
    @NotEmpty
    private String portgroupKey;

    @ApiModelProperty(value = "id", example = "", required = true)
    @NotEmpty
    private String portgroupId;

    @ApiModelProperty(value = "类型", example = "", required = true)
    @NotEmpty
    private String portgroupType;

    /**
     * Gets portgroup key.
     *
     * @return the portgroup key
     */
    public String getPortgroupKey() {
        return portgroupKey;
    }

    /**
     * Sets portgroup key.
     *
     * @param portgroupKey the portgroup key
     */
    public void setPortgroupKey(String portgroupKey) {
        this.portgroupKey = portgroupKey;
    }

    /**
     * Gets portgroup id.
     *
     * @return the portgroup id
     */
    public String getPortgroupId() {
        return portgroupId;
    }

    /**
     * Sets portgroup id.
     *
     * @param portgroupId the portgroup id
     */
    public void setPortgroupId(String portgroupId) {
        this.portgroupId = portgroupId;
    }

    /**
     * Gets portgroup type.
     *
     * @return the portgroup type
     */
    public String getPortgroupType() {
        return portgroupType;
    }

    /**
     * Sets portgroup type.
     *
     * @param portgroupType the portgroup type
     */
    public void setPortgroupType(String portgroupType) {
        this.portgroupType = portgroupType;
    }
}
