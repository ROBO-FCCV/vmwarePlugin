/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import io.swagger.annotations.ApiModelProperty;

/**
 * Network返回结果
 *
 * @since 2019 -09-10
 */
public class NetworkVo {
    @ApiModelProperty(value = "网络ID", example = "network-11", required = true)
    private String vlanId;
    @ApiModelProperty(value = "网络名称", example = "VM Network", required = true)
    private String name;
    @ApiModelProperty(value = "Urn", example = "VM Network", required = true)
    private String urn;

    /**
     * Gets vlan id.
     *
     * @return the vlan id
     */
    public String getVlanId() {
        return vlanId;
    }

    /**
     * Sets vlan id.
     *
     * @param vlanId the vlan id
     * @return the vlan id
     */
    public NetworkVo setVlanId(String vlanId) {
        this.vlanId = vlanId;
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
    public NetworkVo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets urn.
     *
     * @return the urn
     */
    public String getUrn() {
        return urn;
    }

    /**
     * Sets urn.
     *
     * @param urn the urn
     * @return the urn
     */
    public NetworkVo setUrn(String urn) {
        this.urn = urn;
        return this;
    }
}
