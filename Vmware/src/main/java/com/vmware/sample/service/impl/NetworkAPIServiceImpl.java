/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.network.NetworkInfo;
import com.vmware.sample.service.NetworkService;

import com.vmware.vcenter.Network;
import com.vmware.vcenter.NetworkTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Network api service
 *
 * @since 2020-09-22
 */
@Slf4j
@Service("network-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class NetworkAPIServiceImpl implements NetworkService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public List<NetworkInfo> list(String vmwareId, String dataCenterId) {
        Network stubConfiguration = vmwareAPIClient.getStubConfiguration(vmwareId, Network.class);
        NetworkTypes.FilterSpec build = new NetworkTypes.FilterSpec.Builder().build();
        build.setDatacenters(Collections.singleton(dataCenterId));
        build.setTypes(Collections.singleton(NetworkTypes.Type.STANDARD_PORTGROUP));
        List<NetworkTypes.Summary> list = stubConfiguration.list(build);
        List<NetworkInfo> networkInfos = new ArrayList<>();
        for (NetworkTypes.Summary summary : list) {
            NetworkInfo networkInfo = new NetworkInfo();
            networkInfo.setName(summary.getName());
            networkInfo.setVlanId(summary.getNetwork());
            networkInfo.setType(summary.getType().name());
            networkInfos.add(networkInfo);
        }
        return networkInfos;
    }

    @Override
    public List<NetworkInfo> listByHost(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public List<NetworkInfo> listByDomain(String vmwareId, String domainId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
