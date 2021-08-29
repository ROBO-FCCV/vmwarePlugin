/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.consts.VMConstants;
import com.vmware.sample.consts.VMwareConstants;
import com.vmware.sample.factory.ManagedObjectReferenceBuilder;
import com.vmware.sample.model.datastore.DatastoreBasic;
import com.vmware.sample.model.performace.AssembleReq;
import com.vmware.sample.model.performace.PerformanceData;
import com.vmware.sample.model.performace.PerformanceReq;
import com.vmware.sample.service.DatastoreService;
import com.vmware.sample.service.PerformanceService;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.VirtualMachineStorageSummary;
import com.vmware.vim25.VirtualMachineSummary;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Performance sdk service
 *
 * @since 2020-09-28
 */
@Slf4j
@Service("performance-sdk-service")
public class PerformanceSDKServiceImpl implements PerformanceService {
    private static final String DISK_USAGE = "disk_usage";
    private static final int MAX_SAMPLE = 1;
    private static final int INTERVAL_TIME = 20;
    private static final String PERCENT_SIGN = "%";
    private static final String AVERAGE = "AVERAGE";
    private static final String UNDERLINE = "_";
    private static final String KBPS = "KBps";
    private static final String KB_PERCENT = "KB/s";

    private final DatastoreService datastoreService;
    private final VmwareSDKClient vmwareSDKClient;

    public PerformanceSDKServiceImpl(VmwareSDKClient vmwareSDKClient,
        @Qualifier("datastore-sdk-service") DatastoreService datastoreService) {
        this.vmwareSDKClient = vmwareSDKClient;
        this.datastoreService = datastoreService;
    }

    @Override
    public List<PerformanceData> performances(String vmwareId, PerformanceReq performanceReq, String type) {
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        ManagedObjectReference perfManager = sdkInstance.getServiceContent().getPerfManager();
        try {
            List<PerfQuerySpec> perfQuerySpecs = new ArrayList<>();
            for (String id : performanceReq.getIds()) {
                PerfQuerySpec perfQuerySpec = new PerfQuerySpec();
                perfQuerySpec.setEntity(ManagedObjectReferenceBuilder.getInstance().type(type).value(id).build());
                // Max samples. Query only one sample as the current performance indicator.
                perfQuerySpec.setMaxSample(MAX_SAMPLE);
                perfQuerySpec.setIntervalId(INTERVAL_TIME);
                perfQuerySpecs.add(perfQuerySpec);
            }
            List<PerfEntityMetric> perfEntityMetricBases = sdkInstance.getVimPort()
                .queryPerf(perfManager, perfQuerySpecs)
                .stream()
                .map(item -> (PerfEntityMetric) item)
                .collect(Collectors.toList());
            Set<Integer> counterIds = perfEntityMetricBases.stream()
                .flatMap(item -> item.getValue().stream())
                .map(item -> item.getId().getCounterId())
                .collect(Collectors.toSet());
            List<PerfCounterInfo> perfCounterInfoResult = sdkInstance.getVimPort()
                .queryPerfCounter(perfManager, new ArrayList<>(counterIds));
            boolean diskUsage = performanceReq.getMetricIds().containsKey(DISK_USAGE);
            performanceReq.getMetricIds().remove(DISK_USAGE);
            List<PerfCounterInfo> perfCounterInfos = perfCounterInfoResult.stream()
                .filter(item -> performanceReq.getMetricIds()
                    .containsKey(item.getGroupInfo().getKey() + UNDERLINE + item.getNameInfo().getKey())
                    && item.getRollupType().name().equalsIgnoreCase(AVERAGE))
                .sorted(Comparator.comparingInt(PerfCounterInfo::getKey))
                .collect(Collectors.toList());
            AssembleReq assembleReq = AssembleReq.builder()
                .vmwareId(vmwareId)
                .type(type)
                .diskUsage(diskUsage)
                .perfEntityMetricBases(perfEntityMetricBases)
                .perfCounterInfos(perfCounterInfos)
                .performanceReq(performanceReq)
                .build();
            return assembleData(assembleReq);
        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
            log.error("RuntimeFaultFaultMsg", SensitiveExceptionUtils.hideSensitiveInfo(runtimeFaultFaultMsg));
        }
        return Collections.emptyList();
    }

    private List<PerformanceData> assembleData(AssembleReq assembleReq) {
        Map<Integer, PerfCounterInfo> collect = assembleReq.getPerfCounterInfos()
            .stream()
            .collect(Collectors.toMap(PerfCounterInfo::getKey, item -> item, (first, second) -> second));
        List<PerformanceData> performanceDataList = new ArrayList<>();
        for (PerfEntityMetric perfEntityMetric : assembleReq.getPerfEntityMetricBases()) {
            PerformanceData performanceData = new PerformanceData();
            performanceData.setObjectId(perfEntityMetric.getEntity().getValue());
            List<PerformanceData.Indicator> indicators = new ArrayList<>();
            calculateMetricValue(assembleReq.getPerformanceReq(), collect, perfEntityMetric, indicators);
            // The disk usage is not used in the performance data, which needs to be calculated separately.
            if (assembleReq.isDiskUsage()) {
                calculateDiskUsage(assembleReq.getVmwareId(), assembleReq.getType(), performanceData.getObjectId(),
                    indicators);
            }
            performanceData.setIndicators(indicators);
            performanceDataList.add(performanceData);
        }
        return performanceDataList;
    }

    private void calculateMetricValue(PerformanceReq performanceReq, Map<Integer, PerfCounterInfo> collect,
        PerfEntityMetric perfEntityMetric, List<PerformanceData.Indicator> indicators) {
        for (PerfMetricIntSeries perfMetricSeries : perfEntityMetric.getValue()
            .stream()
            .map(item -> (PerfMetricIntSeries) item)
            .collect(Collectors.toList())) {
            if (collect.containsKey(perfMetricSeries.getId().getCounterId()) && StringUtils.isEmpty(
                perfMetricSeries.getId().getInstance())) {
                PerformanceData.Indicator indicator = new PerformanceData.Indicator();
                PerfCounterInfo perfCounterInfo = collect.get(perfMetricSeries.getId().getCounterId());
                indicator.setMetricName(performanceReq.getMetricIds()
                    .get(perfCounterInfo.getGroupInfo().getKey() + UNDERLINE + perfCounterInfo.getNameInfo().getKey()));
                indicator.setUnit(perfCounterInfo.getUnitInfo().getLabel().equals(KBPS)
                    ? KB_PERCENT
                    : perfCounterInfo.getUnitInfo().getLabel());
                setValue(indicator, perfMetricSeries, perfCounterInfo);
                indicators.add(indicator);
            }
        }
    }

    private void setValue(PerformanceData.Indicator indicator, PerfMetricIntSeries perfMetricSeries,
        PerfCounterInfo perfCounterInfo) {
        if (StringUtils.equals(perfCounterInfo.getUnitInfo().getLabel(), PERCENT_SIGN)) {
            indicator.setValue(BigDecimal.valueOf(perfMetricSeries.getValue().get(0))
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        } else {
            indicator.setValue(BigDecimal.valueOf(perfMetricSeries.getValue().get(0)));
        }
    }

    private void calculateDiskUsage(String vmwareId, String type, String id,
        List<PerformanceData.Indicator> indicators) {
        if (type.equals(VMConstants.HOST_SYSTEM)) {
            calculateHostDiskUsage(vmwareId, id, indicators);
        } else {
            calculateVmDiskUsage(vmwareId, id, indicators);
        }
    }

    private void calculateVmDiskUsage(String vmwareId, String id, List<PerformanceData.Indicator> indicators) {
        // The vm disk_usage indicates the storage usage of the vm.
        VMwareSDK sdkInstance = vmwareSDKClient.getSDKInstance(vmwareId);
        List<ObjectContent> objectContents = vmwareSDKClient.retrieveProperties(sdkInstance,
            ManagedObjectReferenceBuilder.getInstance().type(VMConstants.VIRTUAL_MACHINE).value(id).build(),
            Collections.singletonList(VMwareConstants.SUMMARY));
        Object dynamicProperty = objectContents.get(0).getPropSet().get(0).getVal();
        if (dynamicProperty instanceof VirtualMachineSummary) {
            VirtualMachineSummary virtualMachineSummary = (VirtualMachineSummary) dynamicProperty;
            VirtualMachineStorageSummary virtualMachineStorageSummary = virtualMachineSummary.getStorage();
            BigDecimal diskUsage = new BigDecimal(virtualMachineStorageSummary.getCommitted()).multiply(
                new BigDecimal(100))
                .divide(new BigDecimal(
                        virtualMachineStorageSummary.getUncommitted() + virtualMachineStorageSummary.getCommitted()), 2,
                    RoundingMode.HALF_UP);
            PerformanceData.Indicator indicator = new PerformanceData.Indicator();
            indicator.setMetricName(DISK_USAGE);
            indicator.setUnit(PERCENT_SIGN);
            indicator.setValue(diskUsage);
            indicators.add(indicator);
        }
    }

    private void calculateHostDiskUsage(String vmwareId, String id, List<PerformanceData.Indicator> indicators) {
        // The host disk_usage indicates the datastore usage of the host.
        List<DatastoreBasic> datastores = datastoreService.datastore(vmwareId, id);
        BigDecimal capacity = new BigDecimal(0L);
        BigDecimal freeSpace = new BigDecimal(0L);
        BigDecimal diskUsage = new BigDecimal(0);
        for (DatastoreBasic datastoreBasic : datastores) {
            capacity = capacity.add(BigDecimal.valueOf(datastoreBasic.getCapacity().toBytes()));
            freeSpace = freeSpace.add(BigDecimal.valueOf(datastoreBasic.getFreeSpace().toBytes()));
        }
        BigDecimal useStore = capacity.subtract(freeSpace);
        if (capacity.longValue() > 0) {
            diskUsage = useStore.multiply(BigDecimal.valueOf(100)).divide(capacity, 2, RoundingMode.HALF_UP);
        }
        PerformanceData.Indicator indicator = new PerformanceData.Indicator();
        indicator.setMetricName(DISK_USAGE);
        indicator.setUnit(PERCENT_SIGN);
        indicator.setValue(diskUsage);
        indicators.add(indicator);
    }
}
