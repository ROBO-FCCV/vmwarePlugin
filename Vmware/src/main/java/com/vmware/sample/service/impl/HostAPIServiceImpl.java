/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.host.HostBasic;
import com.vmware.sample.model.host.HostBusAdapterVo;
import com.vmware.sample.service.HostService;

import com.vmware.vcenter.Host;
import com.vmware.vcenter.HostTypes;
import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.HostSystemConnectionState;
import com.vmware.vim25.ManagedObjectReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Host API Service
 *
 * @since 2020-09-14
 */
@Slf4j
@Service("host-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class HostAPIServiceImpl implements HostService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public HostBasic getHost(String vmwareId, String hostId) {
        Host stub = vmwareAPIClient.getStubConfiguration(vmwareId, Host.class);
        HostTypes.FilterSpec.Builder bolder = new HostTypes.FilterSpec.Builder();
        bolder.setHosts(Collections.singleton(hostId));
        List<HostTypes.Summary> list = stub.list(bolder.build());
        HostTypes.Summary summary = list.get(0);
        HostBasic hostInfo = new HostBasic();
        hostInfo.setMoId(summary.getHost());
        hostInfo.setStatus(HostSystemConnectionState.valueOf(summary.getConnectionState().name()));
        hostInfo.setIpAddress(summary.getName());
        hostInfo.setName(summary.getName());
        return hostInfo;
    }

    @Override
    public List<HostBasic> list(String vmwareId) {
        Host stub = vmwareAPIClient.getStubConfiguration(vmwareId, Host.class);
        HostTypes.FilterSpec.Builder bolder = new HostTypes.FilterSpec.Builder();
        List<HostTypes.Summary> list = stub.list(bolder.build());
        List<HostBasic> hostInfos = new ArrayList<>();
        for (HostTypes.Summary summary : list) {
            HostBasic hostInfo = new HostBasic();
            hostInfo.setMoId(summary.getHost());
            hostInfo.setStatus(HostSystemConnectionState.valueOf(summary.getConnectionState().name()));
            hostInfo.setIpAddress(summary.getName());
            hostInfo.setName(summary.getName());
            hostInfos.add(hostInfo);
        }
        return hostInfos;
    }

    @Override
    public List<HostScsiDisk> availableDisks(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public List<String> queryStorageScsiLun(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String rescanHba(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public HostBusAdapterVo busAdapters(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public ManagedObjectReference resourcePool(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String querySerialNumber(String vmwareId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
