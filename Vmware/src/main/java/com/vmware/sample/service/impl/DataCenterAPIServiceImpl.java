/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.model.datacenter.DataCenterInfo;
import com.vmware.sample.service.DataCenterService;

import com.vmware.vcenter.Datacenter;
import com.vmware.vcenter.DatacenterTypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Datacenter api service
 *
 * @since 2020-10-10
 */
@Slf4j
@Service("datacenter-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DataCenterAPIServiceImpl implements DataCenterService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public List<DataCenterInfo> dataCenters(String vmwareId) {
        Datacenter datacenter = vmwareAPIClient.getStubConfiguration(vmwareId, Datacenter.class);
        DatacenterTypes.FilterSpec builder = new Datacenter.FilterSpec.Builder().build();
        List<Datacenter.Summary> list = datacenter.list(builder);
        List<DataCenterInfo> dataCenterInfos = new ArrayList<>();
        for (DatacenterTypes.Summary summary : list) {
            DataCenterInfo dataCenterInfo = new DataCenterInfo();
            dataCenterInfo.setId(summary.getDatacenter());
            dataCenterInfo.setName(summary.getName());
            dataCenterInfos.add(dataCenterInfo);
        }
        return dataCenterInfos;
    }
}
