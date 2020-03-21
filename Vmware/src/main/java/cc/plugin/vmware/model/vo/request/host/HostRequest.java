/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.host;

import cc.plugin.vmware.validation.ListString;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import javax.validation.constraints.Size;

/**
 * host实体类
 *
 * @since 2019 -09-09
 */
public class HostRequest {

    /**
     * 主机名称或者ip
     */
    @ApiModelProperty(value = "主机ID列表", example = "host-1", required = true)
    @Size(min = 1)
    @ListString(message = "Illegal input params")
    private List<String> hostNames;

    /**
     * Gets host names.
     *
     * @return the host names
     */
    public List<String> getHostNames() {
        return hostNames;
    }

    /**
     * Sets host names.
     *
     * @param hostNames the host names
     */
    public void setHostNames(List<String> hostNames) {
        this.hostNames = hostNames;
    }
}
