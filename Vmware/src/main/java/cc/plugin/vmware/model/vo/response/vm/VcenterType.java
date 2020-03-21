/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * 功能描述
 *
 * @since 2019 -10-15
 */
public class VcenterType {

    @ApiModelProperty(value = "系统类型", example = "Windows, Linux, Other", required = true)
    private String type;
    @ApiModelProperty(value = "系统型号", example = "CentOS 7 (64 bit)", required = true)
    private SystemType systemType;

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
     * Gets system type.
     *
     * @return the system type
     */
    public SystemType getSystemType() {
        return systemType;
    }

    /**
     * Sets system type.
     *
     * @param systemType the system type
     */
    public void setSystemType(SystemType systemType) {
        this.systemType = systemType;
    }
}
