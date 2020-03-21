/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.Constants;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.to.ClusterInfoTo;
import cc.plugin.vmware.model.vo.response.cluster.ClusterResourceVo;
import cc.plugin.vmware.model.vo.response.vm.Net;
import cc.plugin.vmware.model.vo.response.vm.VMVo;
import cc.plugin.vmware.service.ClusterService;
import cc.plugin.vmware.util.CommonUtil;

import com.vmware.vim25.ComputeResourceSummary;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostListSummaryQuickStats;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineGuestSummary;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
@Service
public class ClusterServiceImpl implements ClusterService {
    private static final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    @Override
    public ClusterInfoTo getClusterInfo(String vmwareId, String clusterId) throws CustomException {
        logger.info("getClusterResource begin...");
        ClusterInfoTo result = new ClusterInfoTo();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceUtil svc;
        ecb.connect();
        svc = ecb.getServiceUtil();
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference clusterSystem = new ManagedObjectReference();
        clusterSystem.setType("ClusterComputeResource");
        clusterSystem.setValue(clusterId);
        ComputeResourceSummary clusterSystemSummary = (ComputeResourceSummary) svc.getDynamicProperty(clusterSystem,
            "summary");
        int totalCpu = clusterSystemSummary.getTotalCpu();
        result.setTotalCpu(totalCpu);

        double usedMemory = 0;
        double totalMem = 0;
        int usedCpu = 0;
        List<ManagedObjectReference> hostArray = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterSystem,
            "host");
        for (ManagedObjectReference host : hostArray) {
            HostListSummary hostSystemInfo;
            try {
                HostHardwareInfo hardware = (HostHardwareInfo) svc.getDynamicProperty(host, "hardware");
                totalMem += hardware.getMemorySize();
                hostSystemInfo = (HostListSummary) svc.getDynamicProperty(host, "summary");
            } catch (Exception e) {
                logger.error("", e);
                continue;
            }
            HostListSummaryQuickStats quickStats = hostSystemInfo.getQuickStats();
            logger.info("quickStats: {}", quickStats);
            usedMemory = calUsedMemory(usedMemory, quickStats);
            usedCpu = calUsedCpu(usedCpu, quickStats);
        }
        totalMem /= 1024 * 1024;
        double freeMem = totalMem - usedMemory;
        result.setFreeMemory(freeMem);
        result.setTotalMemory(totalMem);
        result.setUsedCpu(usedCpu);

        List<ManagedObjectReference> datastoreList = (List<ManagedObjectReference>) svc.getDynamicProperty(
            clusterSystem, "datastore");
        double freedatastorespace = 0;
        double totaldatastorespace = 0;
        for (ManagedObjectReference datastoreRef : datastoreList) {
            DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(datastoreRef, "summary");
            freedatastorespace += datastoreSummary.getFreeSpace() / 1024 / 1024 / 1024;
            freedatastorespace = CommonUtil.roundToTheNearestTenth(freedatastorespace);
            totaldatastorespace += datastoreSummary.getCapacity() / 1024 / 1024 / 1024;
            totaldatastorespace = CommonUtil.roundToTheNearestTenth(totaldatastorespace);
        }
        result.setFreeDatastoreSpace(freedatastorespace);
        result.setTotalDatastoreSpace(totaldatastorespace);
        logger.info("getClusterResource end...");
        return result;
    }

    @Override
    public List<VMVo> getClusterVms(String vmwareId, String clusterId) throws CustomException {
        logger.info("getClusterVms begin...");
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceUtil svc;
        ecb.connect();
        svc = ecb.getServiceUtil();
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference clusterSystem = new ManagedObjectReference();
        clusterSystem.setType("ClusterComputeResource");
        clusterSystem.setValue(clusterId);

        List<ManagedObjectReference> hostArray = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterSystem,
            "host");
        List<VMVo> vms = new ArrayList<>();
        for (ManagedObjectReference host : hostArray) {
            // 查询虚拟机列表
            setClusterVms(clusterId, svc, vms, host);
        }
        logger.info("getClusterVms end...");
        return vms;
    }

    private void setClusterVms(String clusterId, ServiceUtil svc, List<VMVo> vmLsts, ManagedObjectReference host) {
        List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(host,
            "vm");
        for (ManagedObjectReference vm : vmLst) {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);

            // 为null或者 该虚拟机是模板类型 就跳过
            if (vmConfigInfo == null || (vmConfigInfo instanceof VirtualMachineConfigInfo
                && ((VirtualMachineConfigInfo) vmConfigInfo).isTemplate())) {
                continue;
            }

            VirtualMachineSummary vmSummary = (VirtualMachineSummary) svc.getDynamicProperty(vm, "summary");
            VirtualMachineRuntimeInfo getRunTime = vmSummary.getRuntime();
            VirtualMachineGuestSummary vmGuest = vmSummary.getGuest();
            VirtualMachineConfigInfo vmconfig = (VirtualMachineConfigInfo) svc.getDynamicProperty(vm, "config");
            VirtualHardware virtualHardware = vmconfig.getHardware();
            VMVo vmVo = new VMVo();
            vmVo.setModId(vm.getValue());
            vmVo.setClusterId(clusterId);
            vmVo.setHostId(host.getValue());
            vmVo.setVmId(vm.getValue());
            vmVo.setMemory(virtualHardware.getMemoryMB());
            vmVo.setVcpu(virtualHardware.getNumCPU());
            vmVo.setPowerStatus(getRunTime.getPowerState().toString());
            vmVo.setStatus("");
            vmVo.setIpAddess(vmGuest.getIpAddress());

            // novnc enabled
            try {
                getVncableAndosType(svc, vm, vmVo);
            } catch (ClassCastException e) {
                logger.warn("getVncableAndosType ClassCastException: ", e);
                continue;
            }
            // 查询网卡
            GuestInfo guestInfo = (GuestInfo) svc.getDynamicProperty(vm, "guest");
            List<GuestNicInfo> nets = guestInfo.getNet();
            List<Net> nics = new ArrayList<>();
            for (GuestNicInfo net : nets) {
                nics.add(new Net().setName(net.getNetwork()).setIp(net.getIpAddress()));
            }
            vmVo.setNets(nics);
            String vmName = (String) svc.getDynamicProperty(vm, "name");
            vmName = StringUtils.replace(vmName, "%2f", "/");
            vmName = StringUtils.replace(vmName, "%2F", "/");
            vmName = StringUtils.replace(vmName, "%25", "%");
            vmVo.setVmName(vmName);
            vmLsts.add(vmVo);
        }
    }

    private void getVncableAndosType(ServiceUtil svc, ManagedObjectReference vm, VMVo vmVo) {
        VirtualMachineConfigInfo configs = (VirtualMachineConfigInfo) svc.getDynamicProperty(vm, "config");
        for (OptionValue option : configs.getExtraConfig()) {
            if (Constants.VNC_ENABLED.equals(option.getKey())) {
                vmVo.setVncenabled(true);
                break;
            } else {
                vmVo.setVncenabled(false);
            }
        }
        // 设置系统类型
        String osFullName = configs.getGuestFullName();
        vmVo.setOsName(getOSNameByFullName(osFullName));
    }

    private String getOSNameByFullName(String fullSystemName) {
        String name = null;
        if (fullSystemName != null) {
            name = getNames(fullSystemName);
        }
        return name;
    }

    private String getNames(String fullSystemName) {
        String name;
        if (fullSystemName.toUpperCase().contains(Constants.OS_TYPE_WINDOWS)) {
            name = Constants.OS_TYPE_WINDOWS;
        } else if (fullSystemName.toUpperCase().contains(Constants.OS_TYPE_LINUX)) {
            name = Constants.OS_TYPE_LINUX;
        } else {
            name = Constants.OS_TYPE_OTHER;
        }
        return name;
    }

    private double calUsedMemory(double usedMemory, HostListSummaryQuickStats quickStats) {
        if (null != quickStats) {
            Integer memory = quickStats.getOverallMemoryUsage();
            if (null != memory) {
                usedMemory += memory;
            }
        }
        return usedMemory;
    }

    private int calUsedCpu(int usedCpu, HostListSummaryQuickStats quickStats) {
        if (null != quickStats) {
            Integer cpu = quickStats.getOverallCpuUsage();
            if (null != cpu) {
                usedCpu += cpu;

            }
        }
        return usedCpu;
    }

}
