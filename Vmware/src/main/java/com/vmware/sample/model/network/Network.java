/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.network;

import com.vmware.sample.consts.Constants;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Network
 *
 * @since 2020-11-06
 */
@Getter
@Setter
public class Network {
    private String netmask;
    private String gateway;
    private String ipAddress;
    private String macAddress;
    private String switchUuid;
    private PortGroupUrn portGroupUrn;
    private String type;
    private List<String> dns;

    /**
     * 判断是否为管理网卡
     *
     * @return true:管理网卡；false：非管理网卡
     */
    public boolean isManageTypeNetwork() {
        return Constants.MANAGE_TYPE_NETWORK.equals(type);
    }
}
