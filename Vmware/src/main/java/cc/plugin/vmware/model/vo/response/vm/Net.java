/*
 * Copyright (c). 2020-2020. All rights reserved.
 */
package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -10-28
 */
public class Net {
    @ApiModelProperty(value = "IP地址", example = "192.0.2.0", required = true)
    private List<String> ip;
    @ApiModelProperty(value = "网卡名称", example = "VM Network", required = true)
    private String name;

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public List<String> getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     * @return the ip
     */
    public Net setIp(List<String> ip) {
        this.ip = ip;
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
    public Net setName(String name) {
        this.name = name;
        return this;
    }
}
