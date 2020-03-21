/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.ConnectInfo;
import cc.plugin.vmware.model.vo.request.PerformRequest;
import cc.plugin.vmware.model.vo.request.host.VcenterEsxi;
import cc.plugin.vmware.model.vo.request.vm.VcenterVm;
import cc.plugin.vmware.model.vo.response.PerfDataVo;
import cc.plugin.vmware.service.PerformService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.HostConnectInfo;
import com.vmware.vim25.HostDatastoreConnectInfo;
import com.vmware.vim25.VirtualMachineSummary;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Perform service.
 *
 * @since 2019 -09-19
 */
@Service
public class PerformServiceImpl implements PerformService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    private static final int MAXINDEX = 10000;
    private static final int REALTIME = 20;
    private static final String RESOURCE = "host";
    private static final int COLLECTTIME = 15;
    private static final String RESOURCE_VM = "VM";
    private static final String HOST_SYSTEM = "HostSystem";
    private static final String DISK_USAGE = "disk_usage";
    private static final String PERCENT = "%";
    private static final int CONST_NUM_1024 = 1024;
    private static final double DOUBLE_1024 = 1024.0;
    private static final int CONST_NUM_100 = 100;
    private static final int SCALE_2 = 2;

    @Override
    public List<PerfDataVo> getPerformance(String vmwareId, PerformRequest performRequest, boolean isHost)
        throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg, CustomException {
        List<PerfDataVo> perfDataVos = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceUtil svc;
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType vimPortType = serviceConnection.getVimPort();

        if (serviceContent != null) {
            List<Integer> perfCounterMetricIds = new ArrayList<Integer>();
            for (int index = 0; index < MAXINDEX; index++) {
                perfCounterMetricIds.add(index);
            }
            for (VcenterEsxi esxi : performRequest.getVcenterEsxis()) {
                perfDataVos.addAll(getPerfData(new ConnectInfo(ecb, serviceContent, svc, vimPortType), esxi,
                    perfCounterMetricIds, performRequest.getMetricIds(), isHost));
            }
        }
        return perfDataVos;
    }

    private List<PerfDataVo> getPerfData(ConnectInfo connectInfo,VcenterEsxi esxi, List<Integer> perfCounterMetricIds,
        Map<String,String> metricIds, boolean isHost) throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        ManagedObjectReference rootFolder = connectInfo.getServiceContent().getRootFolder();
        ManagedObjectReference hostRel = connectInfo.getSvc()
            .getDecendentMoRef(rootFolder, HOST_SYSTEM, esxi.getHostName());
        if (hostRel == null) {
            return new ArrayList<>();
        }
        PerfQuerySpec spec = new PerfQuerySpec();
        // 间隔20秒计时器，实时数据
        spec.setIntervalId(new Integer(REALTIME));
        spec.setMaxSample(COLLECTTIME);
        spec.setEntity(hostRel);
        List<PerfQuerySpec> specs = new ArrayList<>(1);
        specs.add(spec);
        ManagedObjectReference performance = connectInfo.getServiceContent().getPerfManager();
        List<PerfCounterInfo> perfCounts = connectInfo.getVimPortType()
            .queryPerfCounter(performance, perfCounterMetricIds);
        List<PerfEntityMetricBase> perfs = connectInfo.getVimPortType().queryPerf(performance, specs);
        if (perfs == null || perfs.size() == 0) {
            return new ArrayList<>();
        }

        Map<Integer, PerfDataVo> perfDataMap = getPerfDataMap(esxi, metricIds, perfCounts, isHost);
        if (isHost) {
            List<PerfDataVo> hostPerfDatas = getAllDataVos(perfDataMap, perfs);
            PerfDataVo perfDataVo = getDiskUsage(connectInfo, esxi);
            if (perfDataVo != null) {
                hostPerfDatas.add(perfDataVo);
            }
            return hostPerfDatas;
        } else {
            List<PerfDataVo> vmPerfDatas = getVmPerfData(connectInfo.getServiceContent(), connectInfo.getSvc(),
                connectInfo.getVimPortType(), perfDataMap, esxi);
            return vmPerfDatas;
        }
    }

    private Map<Integer, PerfDataVo> getPerfDataMap(VcenterEsxi esxi, Map<String,String> metricIds,
        List<PerfCounterInfo> perfCounts, boolean isHost) {
        Map<Integer, PerfDataVo> perfDataMap = new HashMap<>(1);
        for (PerfCounterInfo counterInfo : perfCounts) {
            // 判断 第三方系统传入的GroupInfo_Key+"_"+NameInfo_Key 状态是否是AVERAGE
            if (!counterInfo.getRollupType().name().equalsIgnoreCase("AVERAGE")) {
                continue;
            }

            String sourceMetricId = counterInfo.getGroupInfo().getKey() + "_" + counterInfo.getNameInfo().getKey();
            if (!metricIds.containsKey(sourceMetricId)) {
                continue;
            }
            String metricId = metricIds.get(sourceMetricId);
            if (!isHost) {
                PerfDataVo vo = new PerfDataVo();
                vo.setObjectName(esxi.getHostName());
                vo.setMetricName(metricId);
                vo.setUnit(counterInfo.getUnitInfo().getLabel());
                vo.setType(RESOURCE);
                // counterInfo 此数据对象类型包含性能计数器的元数据
                perfDataMap.put(counterInfo.getKey(), vo);
            } else {
                if (!DISK_USAGE.equals(sourceMetricId)) {
                    PerfDataVo vo = new PerfDataVo();
                    vo.setUrn(esxi.getId());
                    vo.setId(esxi.getId());
                    vo.setHostName(esxi.getHostName());
                    vo.setHostIp(esxi.getHostIp());
                    vo.setObjectName(esxi.getHostName());
                    vo.setMetricName(metricId);
                    vo.setUnit(counterInfo.getUnitInfo().getLabel());
                    vo.setType(RESOURCE);
                    vo.setDataType(1);
                    perfDataMap.put(counterInfo.getKey(), vo);
                }
            }
        }
        return perfDataMap;
    }

    private List<PerfDataVo> getAllDataVos(Map<Integer, PerfDataVo> perfDataMap, List<PerfEntityMetricBase> perfs) {
        List<PerfDataVo> allDataVos = new ArrayList<>();
        for (PerfMetricSeries perfmetric : ((PerfEntityMetric) perfs.get(0)).getValue()) {
            if (!StringUtils.isEmpty(perfmetric.getId().getInstance())) {
                continue;
            }
            PerfDataVo vo = perfDataMap.get(perfmetric.getId().getCounterId());
            if (vo != null && vo.getDataType() == 1) {
                BigDecimal avgValue = getAvgValue(
                        JSONObject.toJSONString(perfmetric));
                if (avgValue == null) {
                    continue;
                }
                if (PERCENT.equals(vo.getUnit())) {
                    vo.setValue(avgValue.divide(new BigDecimal(CONST_NUM_100)));
                } else {
                    vo.setValue(avgValue);
                }
                allDataVos.add(vo);
            }
        }
        return allDataVos;
    }

    private PerfDataVo getDiskUsage(ConnectInfo connectInfo, VcenterEsxi esxi) throws RuntimeFaultFaultMsg {
        List<HostConnectInfo> connectInfos = getHostConf(connectInfo.getServiceContent(), connectInfo.getSvc(),
            connectInfo.getVimPortType());
        if (connectInfos == null) {
            LOGGER.warn("connectInfos is null!");
            return null;
        }
        DecimalFormat df = new java.text.DecimalFormat("#0.00");
        double allStore = 0.0;
        double freeStore = 0.0;
        double diskUsage = 0.0;
        for (HostConnectInfo connection : connectInfos) {
            if (!esxi.getId()
                    .contains(connection.getHost().getHost().getValue())) {
                continue;
            }
            if (connection.getDatastore() != null) {
                for (HostDatastoreConnectInfo datastore : connection
                        .getDatastore()) {
                    allStore = allStore + datastore.getSummary().getCapacity()
                            / CONST_NUM_1024 / CONST_NUM_1024 / DOUBLE_1024;
                    freeStore = freeStore
                            + datastore.getSummary().getFreeSpace() / CONST_NUM_1024
                            / CONST_NUM_1024 / DOUBLE_1024;
                }
            }
            double useStore = allStore - freeStore;
            if (allStore > 0) {
                diskUsage = useStore / allStore * CONST_NUM_100;
            }
            PerfDataVo vo = new PerfDataVo();
            vo.setUrn(esxi.getId());
            vo.setId(esxi.getId());
            vo.setHostName(esxi.getHostName());
            vo.setHostIp(esxi.getHostIp());
            vo.setObjectName(esxi.getHostName());
            vo.setMetricName(DISK_USAGE);
            vo.setUnit(PERCENT);
            vo.setValue(new BigDecimal(df.format(diskUsage)));
            vo.setType(RESOURCE);
            vo.setDataType(1);
            return vo;
        }
        return null;
    }

    private synchronized List<HostConnectInfo> getHostConf(ServiceContent serviceContent, ServiceUtil svc,
        VimPortType vimPortType) throws RuntimeFaultFaultMsg {
        ManagedObjectReference rootFolder = serviceContent.getRootFolder();

        List<ManagedObjectReference> hostSystems = svc
                .getDecendentMoRefs(rootFolder, HOST_SYSTEM);
        List<HostConnectInfo> conns = new ArrayList<>();
        if (hostSystems != null && hostSystems.size() > 0) {
            for (ManagedObjectReference hostRef : hostSystems) {
                HostConnectInfo connect = vimPortType
                        .queryHostConnectionInfo(hostRef);
                conns.add(connect);
            }
        }
        return conns;
    }

    private BigDecimal getAvgValue(String jsonString) {
        JSONObject obj = JSONObject.parseObject(jsonString);
        JSONArray values = (JSONArray) obj.get("value");
        if (values == null) {
            LOGGER.info("get avgValue is null");
            return null;
        }
        long allCellSum = 0L;
        int nullNumber = 0;
        int size = values.size();
        for (int valuei = 0; valuei < size; valuei++) {
            if (values.getLong(valuei) == null) {
                nullNumber += 1;
                continue;
            }
            allCellSum += values.getLong(valuei);
        }
        if (nullNumber == size) {
            return new BigDecimal(0);
        }
        return new BigDecimal(allCellSum).divide(
                new BigDecimal(size - nullNumber), SCALE_2, BigDecimal.ROUND_HALF_UP);
    }

    private List<PerfDataVo> getVmPerfData(ServiceContent serviceContent, ServiceUtil svc, VimPortType vimPortType,
        Map<Integer, PerfDataVo> perfDataMap, VcenterEsxi esxi) throws RuntimeFaultFaultMsg {
        List<PerfDataVo> allDataVos = new ArrayList<>();
        ManagedObjectReference rootFolder = serviceContent.getRootFolder();
        ManagedObjectReference hostRel = svc.getDecendentMoRef(rootFolder,
            HOST_SYSTEM,
                esxi.getHostName());
        HostConnectInfo connect = vimPortType.queryHostConnectionInfo(hostRel);
        Map<String, BigDecimal> vmStorageUesds = new HashMap<>();
        Map<String, Long> vmMemoryUesds = new HashMap<String, Long>();
        getStorageAndMemory(vmStorageUesds,vmMemoryUesds,connect);
        for (VcenterVm vm : esxi.getVmList()) {
            if (!vmStorageUesds.containsKey(vm.getVmName())) {
                continue;
            }
            List<PerfEntityMetricBase> vmPerfs = getVmPerfs(serviceContent, svc, vm, vimPortType);
            if (CollectionUtils.isEmpty(vmPerfs)) {
                continue;
            }
            getVmPerfIndexPre(vm,vmPerfs,allDataVos,perfDataMap, esxi,vmMemoryUesds);
            PerfDataVo vo = new PerfDataVo();
            vo.setUrn(vm.getId());
            vo.setId(vm.getId());
            vo.setHostName(esxi.getHostName());
            vo.setObjectName(vm.getVmName());
            vo.setMetricName(DISK_USAGE);
            vo.setUnit(PERCENT);
            vo.setValue(vmStorageUesds.get(vm.getVmName()));
            vo.setType(RESOURCE_VM);
            vo.setVmIp(vm.getVmIp());
            allDataVos.add(vo);
        }
        return allDataVos;
    }

    private void getVmPerfIndexPre(VcenterVm vm,List<PerfEntityMetricBase> vmPerfs,List<PerfDataVo> allDataVos,
        Map<Integer, PerfDataVo> perfDataMap, VcenterEsxi esxi,Map<String, Long> vmMemoryUesds){
        for (PerfMetricSeries perfmetric : ((PerfEntityMetric) vmPerfs.get(0)).getValue()) {
            try {
                List<PerfDataVo> datas = getvmperfindexs(perfDataMap, vmMemoryUesds, esxi, vm, perfmetric);
                if (datas != null && datas.size() > 0) {
                    allDataVos.addAll(datas);
                }
            }catch (Exception e){
                LOGGER.error("getvmperfindexs have something wrong");
            }
        }
    }

    private void getStorageAndMemory(Map<String, BigDecimal> vmStorageUesds,
        Map<String, Long> vmMemoryUesds,HostConnectInfo connect){
        for (VirtualMachineSummary summary : connect.getVm()) {
            try {
                BigDecimal storageUsed = new BigDecimal(summary.getStorage().getCommitted())
                    .multiply(new BigDecimal(CONST_NUM_100))
                    .divide(new BigDecimal(summary.getStorage().getUncommitted()
                        + summary.getStorage().getCommitted()),SCALE_2,BigDecimal.ROUND_HALF_UP);
                vmStorageUesds.put(summary.getConfig().getName(), storageUsed);
                vmMemoryUesds.put(summary.getConfig().getName(),
                    new Long(summary.getConfig().getMemorySizeMB()));
            }catch (Exception e){
                LOGGER.error("get storage and memory have something wrong");
            }
        }
    }

    private List<PerfEntityMetricBase> getVmPerfs(ServiceContent serviceContent, ServiceUtil svc, VcenterVm vm,
        VimPortType vimPortType) throws RuntimeFaultFaultMsg {
        List<PerfQuerySpec> specs = new ArrayList<>();
        PerfQuerySpec spec = new PerfQuerySpec();
        // 间隔20秒计时器，实时数据
        spec.setIntervalId(new Integer(REALTIME));
        spec.setMaxSample(COLLECTTIME);
        specs.add(spec);
        ManagedObjectReference rootFolder = serviceContent.getRootFolder();
        ManagedObjectReference vmRel = svc.getDecendentMoRef(rootFolder,
                "VirtualMachine",
                vm.getVmName());
        if (vmRel == null) {
            return null;
        }
        spec.setEntity(vmRel);
        ManagedObjectReference performance = serviceContent.getPerfManager();
        // 获取各种PerfEntityMetric编码的基本类型
        return vimPortType.queryPerf(performance, specs);
    }

    private List<PerfDataVo> getvmperfindexs(Map<Integer, PerfDataVo> perfDataMap, Map<String, Long> vmMemoryUesd,
        VcenterEsxi esxi, VcenterVm vm,PerfMetricSeries perfmetric) {
        List<PerfDataVo> allDataVos = new ArrayList<>();
        if (!StringUtils.isEmpty(perfmetric.getId().getInstance())) {
            return allDataVos;
        }
        if (perfDataMap.containsKey(perfmetric.getId().getCounterId())
                && !DISK_USAGE.equals(
                perfDataMap.get(perfmetric.getId().getCounterId())
                        .getMetricName())) {
            PerfDataVo vo = new PerfDataVo();
            vo.setHostName(esxi.getHostName());
            vo.setObjectName(vm.getVmName());
            vo.setVmIp(vm.getVmIp());
            vo.setMetricName(perfDataMap.get(perfmetric.getId().getCounterId())
                    .getMetricName());
            vo.setUnit(perfDataMap.get(perfmetric.getId().getCounterId())
                    .getUnit());
            vo.setUrn(vm.getId());
            vo.setId(vm.getId());
            BigDecimal avgValue = getAvgValue(
                    JSONObject.toJSONString(perfmetric));
            if (avgValue == null) {
                return null;
            }
            if (PERCENT.equals(vo.getUnit())) {
                vo.setValue(avgValue.divide(new BigDecimal(CONST_NUM_100)));
            } else {
                vo.setValue(avgValue);
            }
            if (vo.getValue() == null) {
                return null;
            }
            if ("memory_used".equals(perfDataMap
                    .get(perfmetric.getId().getCounterId()).getMetricName())) {
                BigDecimal memoryUsed = vo.getValue().divide(new BigDecimal(CONST_NUM_1024))
                        .multiply(new BigDecimal(CONST_NUM_100))
                        .divide(new BigDecimal(vmMemoryUesd.get(vm.getVmName())),SCALE_2,BigDecimal.ROUND_HALF_UP);
                vo.setMetricName("mem_usage");
                vo.setValue(memoryUsed);
                vo.setUnit(PERCENT);
            }
            if ("KBps".equals(vo.getUnit())) {
                vo.setUnit("KB/s");
            }
            vo.setType(RESOURCE_VM);
            allDataVos.add(vo);
        }
        return allDataVos;
    }
}
