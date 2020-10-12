/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.Constants;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.to.ClusterAndHostTo;
import cc.plugin.vmware.model.to.ClusterTo;
import cc.plugin.vmware.model.to.HostTo;
import cc.plugin.vmware.model.vo.response.VcenterEnvironment;
import cc.plugin.vmware.model.vo.response.cluster.ClusterVO;
import cc.plugin.vmware.model.vo.response.datacenter.DataCenter;
import cc.plugin.vmware.model.vo.response.datastore.Datastore;
import cc.plugin.vmware.model.vo.response.datastore.IsoEnty;
import cc.plugin.vmware.model.vo.response.host.Host;
import cc.plugin.vmware.model.vo.response.vm.Net;
import cc.plugin.vmware.model.vo.response.vm.VMVo;
import cc.plugin.vmware.service.DataCenterService;
import cc.plugin.vmware.util.CommonUtil;

import com.vmware.vim25.ComputeResourceSummary;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.HostListSummaryQuickStats;
import com.vmware.vim25.HostRuntimeInfo;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineGuestSummary;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
@Service
public class DataCenterServiceImpl implements DataCenterService {
    private static final Logger logger = LoggerFactory.getLogger(DataCenterServiceImpl.class);
    private ExtendedAppUtil extendedAppUtil;

    public DataCenterServiceImpl(ExtendedAppUtil extendedAppUtil) {
        this.extendedAppUtil = extendedAppUtil;
    }

    @Override
    public VcenterEnvironment getVcenterBasicInfo(String vmwareId) throws CustomException {
        VcenterEnvironment result = new VcenterEnvironment();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceUtil svc;
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        if (svc == null || svc.connection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference rootFolder = content.getRootFolder();
        List<ManagedObjectReference> computeResourceList = svc.getDecendentMoRefs(rootFolder, "ComputeResource");
        double totalMem = 0;
        double freeMem;
        int totalCpu = 0;
        int usedCpu = 0;
        double usedMemory = 0;
        for (ManagedObjectReference comRes : computeResourceList) {
            ComputeResourceSummary resourceSummary = (ComputeResourceSummary) svc.getDynamicProperty(comRes, "summary");
            totalCpu += resourceSummary.getTotalCpu();
            List<ManagedObjectReference> hostArray = (List<ManagedObjectReference>) svc.getDynamicProperty(comRes,
                "host");
            for (ManagedObjectReference host : hostArray) {
                HostHardwareInfo hardware;
                HostListSummary hostSystemInfo;
                try {
                    hardware = (HostHardwareInfo) svc.getDynamicProperty(host, "hardware");
                    hostSystemInfo = (HostListSummary) svc.getDynamicProperty(host, "summary");
                } catch (Exception ex) {
                    logger.error("", ex);
                    continue;
                }
                HostListSummaryQuickStats quickStats = hostSystemInfo.getQuickStats();
                if (quickStats.getOverallMemoryUsage() != null) {
                    usedMemory += quickStats.getOverallMemoryUsage();
                    totalMem += hardware.getMemorySize();
                }
                if (quickStats.getOverallCpuUsage() != null) {
                    usedCpu += quickStats.getOverallCpuUsage();
                }
            }

        }
        totalMem = totalMem / 1024 / 1024;
        freeMem = totalMem - usedMemory;

        result.setTotalMemMb(totalMem);
        result.setFreeMemMb(freeMem);
        result.setUsedMemory(usedMemory);
        result.setTotalCpu(totalCpu);
        result.setUsedCpu(usedCpu);
        List<ManagedObjectReference> datastoreList = svc.getDecendentMoRefs(rootFolder, "Datastore");
        double freedatastorespace = 0;
        double totaldatastorespace = 0;
        for (ManagedObjectReference datastoreRef : datastoreList) {
            DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(datastoreRef, "summary");
            freedatastorespace += datastoreSummary.getFreeSpace() / 1024 / 1024 / 1024;
            freedatastorespace = CommonUtil.roundToTheNearestTenth(freedatastorespace);
            totaldatastorespace += datastoreSummary.getCapacity() / 1024 / 1024 / 1024;

            totaldatastorespace = CommonUtil.roundToTheNearestTenth(totaldatastorespace);
        }
        result.setFreedatastorespace(freedatastorespace);
        result.setTotaldatastorespace(totaldatastorespace);
        return result;
    }

    @Override
    public List<DataCenter> getClustersAndHosts(String vmwareId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        if (svc == null || svc.connection == null || content == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference rootFolder = content.getRootFolder();
        List<ManagedObjectReference> datacenterList = svc.getDecendentMoRefs(rootFolder, "Datacenter");
        List<DataCenter> dataCenters = new ArrayList<>();

        // 获取数据中心
        for (ManagedObjectReference obj : datacenterList) {
            DataCenter dc = new DataCenter();
            String datacenterName = (String) svc.getDynamicProperty(obj, "name");
            logger.info("Map.. {}", datacenterName);
            dc.setDataCenterName(datacenterName);
            ManagedObjectReference hostRef = (ManagedObjectReference) svc.getDynamicProperty(obj, "hostFolder");
            // 获取cluster下面主机信息
            List<ClusterVO> clusterVOLst = clusterHostsHandle(svc, hostRef);
            dc.setClusterList(clusterVOLst);
            // 设置独立主机
            List<Host> hosts = hostHandle(svc, hostRef);
            dc.setExsiHostList(hosts);
            dataCenters.add(dc);
        }
        return dataCenters;
    }

    @Override
    public List<ClusterAndHostTo> getHosts(String vmwareId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        if (svc == null || svc.connection == null || content == null) {
            logger.error(String.format(Locale.ENGLISH, "The connection of Vmware %s is empty", vmwareId));
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference rootFolder = content.getRootFolder();
        List<ManagedObjectReference> datacenterList = svc.getDecendentMoRefs(rootFolder, "Datacenter");
        List<ClusterAndHostTo> clusterAndHostTos = new ArrayList<>();
        // 获取数据中心
        for (ManagedObjectReference obj : datacenterList) {
            ClusterAndHostTo clusterAndHostTo = new ClusterAndHostTo();
            String datacenterName = (String) svc.getDynamicProperty(obj, "name");
            logger.info("Map.. {}", datacenterName);
            clusterAndHostTo.setDatacenterName(datacenterName);
            ManagedObjectReference hostRef = (ManagedObjectReference) svc.getDynamicProperty(obj, "hostFolder");
            // 获取cluster下面主机信息
            List<ClusterTo> clusterTos = queryClusters(svc, hostRef);
            clusterAndHostTo.setClusters(clusterTos);
            // 获取独立主机
            List<HostTo> hostTos = queryStandaloneHosts(svc, hostRef);
            clusterAndHostTo.setHosts(hostTos);
            clusterAndHostTos.add(clusterAndHostTo);
        }
        return clusterAndHostTos;
    }

    private List<HostTo> queryStandaloneHosts(ServiceUtil svc, ManagedObjectReference hostRef) {
        List<HostTo> hostTos = new ArrayList<>();
        List<ManagedObjectReference> computeResources = svc.getDecendentMoRefs(hostRef, "ComputeResource");
        if (CollectionUtils.isNotEmpty(computeResources)) {
            for (ManagedObjectReference computeResource : computeResources) {
                logger.info("Host.getType() : {}", computeResource.getType());
                if ("ComputeResource".equals(computeResource.getType())) { // 独立主机
                    HostTo hostTo = queryHost(svc, computeResource);
                    hostTos.add(hostTo);
                } else { // 其他主机不处理
                    logger.info("Other hosts do not need to process.");
                }
            }
        }
        return hostTos;
    }

    private List<Host> hostHandle(ServiceUtil svc, ManagedObjectReference hostRef) {
        List<ManagedObjectReference> list = svc.getDecendentMoRefs(hostRef, "ComputeResource");
        List<Host> hostLst = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (ManagedObjectReference host : list) {
                logger.info("Host.getType() : {}", host.getType());
                if ("ComputeResource".equals(host.getType())) {
                    Host host1 = hostsHandle(svc, host);
                    hostLst.add(host1);
                } else {
                    logger.info("Other hosts do not need to process.");
                }
            }
        }
        return hostLst;
    }

    private Host hostsHandle(ServiceUtil svc, ManagedObjectReference host) {
        Host hostRes = new Host();
        List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(host, "host");
        String hostName = (String) svc.getDynamicProperty(host, "name");
        for (ManagedObjectReference htTmp : hostlist) {
            HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(htTmp, "summary");
            List<Datastore> datastoreLst = new ArrayList<>();
            setClusterHostDatastore(svc, datastoreLst, htTmp, hostRes);
            HostRuntimeInfo hostRuntimeInfo = hostSystemInfo.getRuntime();
            hostRes.setEsxStatus(hostRuntimeInfo.getConnectionState().toString());
            hostRes.setName(hostName);
            hostRes.setIp(hostName);
            hostRes.setMoId(htTmp.getValue());
            List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(htTmp, "vm");
            List<VMVo> vmLsts = new ArrayList<>();
            setHostsVms(svc, htTmp, vmLst, vmLsts);
            hostRes.setVmList(vmLsts);
        }
        return hostRes;
    }

    private void setHostsVms(ServiceUtil svc, ManagedObjectReference htTmp, List<ManagedObjectReference> vmLst,
        List<VMVo> vmLsts) {
        for (ManagedObjectReference vm : vmLst) {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            VirtualMachineConfigInfo config;
            if (vmConfigInfo == null) {
                continue;
            } else {
                try {
                    config = (VirtualMachineConfigInfo) vmConfigInfo;
                } catch (ClassCastException e) {
                    logger.warn("GetVmConfigInfo ClassCastException vm", e);
                    continue;
                }
            }
            // 过滤掉模板
            if (config.isTemplate()) {
                continue;
            }
            VirtualMachineSummary vmSummary = (VirtualMachineSummary) svc.getDynamicProperty(vm, "summary");
            VirtualMachineRuntimeInfo getRunTime = vmSummary.getRuntime();
            VirtualMachineGuestSummary vmGuest = vmSummary.getGuest();
            VirtualMachineConfigInfo vmconfig = (VirtualMachineConfigInfo) svc.getDynamicProperty(vm, "config");
            VirtualHardware virtualHardware = vmconfig.getHardware();
            VMVo vmVo = new VMVo();
            vmVo.setModId(vm.getValue());
            vmVo.setHostId(htTmp.getValue());
            vmVo.setClusterId("");
            vmVo.setStatus("");
            vmVo.setVmId(vm.getValue());
            String vmName = (String) svc.getDynamicProperty(vm, "name");
            vmVo.setVmName(vmName);
            vmVo.setMemory(virtualHardware.getMemoryMB() / 1024);
            vmVo.setVcpu(virtualHardware.getNumCPU());
            vmVo.setPowerStatus(getRunTime.getPowerState().toString());
            vmVo.setIpAddess(vmGuest.getIpAddress());
            vmVo.setOsName(getOSNameByFullName(config.getGuestFullName()));
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
            vmLsts.add(vmVo);
        }
    }

    private HostTo queryHost(ServiceUtil svc, ManagedObjectReference host) {
        HostTo hostTo = new HostTo();
        String hostName = (String) svc.getDynamicProperty(host, "name");
        List<ManagedObjectReference> hostList = (List<ManagedObjectReference>) svc.getDynamicProperty(host, "host");
        if (CollectionUtils.isNotEmpty(hostList)) {
            ManagedObjectReference managedObjectReference = hostList.get(0);
            HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(managedObjectReference,
                "summary");
            hostTo.setStatus(hostSystemInfo.getRuntime().getConnectionState().toString());
            hostTo.setMoId(managedObjectReference.getValue());
        }
        hostTo.setName(hostName);
        hostTo.setIpAddress(hostName);
        return hostTo;
    }

    private void setClusterHostDatastore(ServiceUtil svc, List<Datastore> htTmpDatastoreLst,
        ManagedObjectReference htTmp, Host ht) {
        List<ManagedObjectReference> hostdatastoreLst = (List<ManagedObjectReference>) svc.getDynamicProperty(htTmp,
            "datastore");
        for (ManagedObjectReference hostdatastore : hostdatastoreLst) {
            Datastore datastore = new Datastore();
            String datastoreName = (String) svc.getDynamicProperty(hostdatastore, "name");
            String modId = hostdatastore.getValue();
            datastore.setModId(modId);
            datastore.setName(datastoreName);
            DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(hostdatastore, "summary");
            long capacitySize = datastoreSummary.getCapacity() / 1024 / 1024 / 1024;
            long freeSize = datastoreSummary.getFreeSpace() / 1024 / 1024 / 1024;
            datastore.setCapacityGB(capacitySize);
            datastore.setFreeSizeGB(freeSize);
            datastore.setUsedSizeGB(capacitySize - freeSize);
            htTmpDatastoreLst.add(datastore);
        }
        ht.setDatastoreLst(htTmpDatastoreLst);
    }

    private List<ClusterVO> clusterHostsHandle(ServiceUtil svc, ManagedObjectReference hostRef) {
        List<ClusterVO> clusterVOLst = new ArrayList<>();
        List<ManagedObjectReference> clusterLst = svc.getDecendentMoRefs(hostRef, "ClusterComputeResource");
        if (clusterLst != null) {
            for (ManagedObjectReference clusterVo : clusterLst) {
                ClusterVO clusterVO = new ClusterVO();
                List<Datastore> datastoreLst = new ArrayList<>();
                List<ManagedObjectReference> clusterdatastoreLst
                    = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterVo, "datastore");
                for (ManagedObjectReference clusterdatastore : clusterdatastoreLst) {
                    Datastore datastore = new Datastore();

                    String datastoreName = (String) svc.getDynamicProperty(clusterdatastore, "name");
                    String modId = clusterdatastore.getValue();
                    datastore.setModId(modId);
                    DatastoreSummary datastoreSummary = (DatastoreSummary) svc.getDynamicProperty(clusterdatastore,
                        "summary");
                    long capacitySize = datastoreSummary.getCapacity() / 1024 / 1024 / 1024;
                    long freeSize = datastoreSummary.getFreeSpace() / 1024 / 1024 / 1024;
                    datastore.setCapacityGB(capacitySize);
                    datastore.setFreeSizeGB(freeSize);
                    datastore.setUsedSizeGB(capacitySize - freeSize);
                    datastore.setName(datastoreName);
                    datastoreLst.add(datastore);
                }
                clusterVO.setDatastoreLst(datastoreLst);
                List<ManagedObjectReference> hostList = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterVo,
                    "host");
                List<Host> hostlists = new ArrayList<>();

                for (ManagedObjectReference htTmp : hostList) {
                    String hostName = (String) svc.getDynamicProperty(htTmp, "name");
                    HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(htTmp, "summary");
                    Host ht = new Host();
                    List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(htTmp,
                        "vm");
                    List<VMVo> vmLsts = new ArrayList<>();
                    setVmList(svc, clusterVo, htTmp, vmLst, vmLsts);
                    ht.setVmList(vmLsts);
                    HostRuntimeInfo hostRuntimeInfo = hostSystemInfo.getRuntime();
                    ht.setEsxStatus(hostRuntimeInfo.getConnectionState().toString());
                    ht.setName(hostName);
                    ht.setIp(hostName);
                    ht.setMoId(htTmp.getValue());
                    List<Datastore> htTmpDatastoreLst = new ArrayList<>();
                    setClusterHostDatastore(svc, htTmpDatastoreLst, htTmp, ht);
                    hostlists.add(ht);
                }
                clusterVO.setMoId(clusterVo.getValue());
                String clusterName = (String) svc.getDynamicProperty(clusterVo, "name");
                clusterVO.setName(clusterName);
                clusterVO.setHostList(hostlists);
                clusterVOLst.add(clusterVO);
            }
        }
        return clusterVOLst;
    }

    private void setVmList(ServiceUtil svc, ManagedObjectReference clusterVo, ManagedObjectReference htTmp,
        List<ManagedObjectReference> vmLst, List<VMVo> vmLsts) {
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
            vmVo.setClusterId(clusterVo.getValue());
            vmVo.setHostId(htTmp.getValue());
            vmVo.setVmId(vm.getValue());
            vmVo.setMemory(virtualHardware.getMemoryMB() / 1024);
            vmVo.setVcpu(virtualHardware.getNumCPU());
            vmVo.setPowerStatus(getRunTime.getPowerState().toString());
            vmVo.setStatus("");
            vmVo.setIpAddess(vmGuest.getIpAddress());

            // novnc enabled
            try {
                getVncableAndosType(svc, vm, vmVo);
            } catch (ClassCastException e) {
                logger.warn("GetVncableAndosType ClassCastException: ", e);
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
            vmVo.setVmName(vmName);
            vmLsts.add(vmVo);
        }
    }

    private List<ClusterTo> queryClusters(ServiceUtil svc, ManagedObjectReference hostRef) {
        List<ClusterTo> clusterTos = new ArrayList<>();
        List<ManagedObjectReference> clusterLst = svc.getDecendentMoRefs(hostRef, "ClusterComputeResource");
        if (clusterLst != null) {
            for (ManagedObjectReference clusterVo : clusterLst) {
                ClusterTo clusterTo = new ClusterTo();
                List<ManagedObjectReference> hostList = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterVo,
                    "host");
                List<HostTo> hosts = new ArrayList<>();
                for (ManagedObjectReference htTmp : hostList) {
                    String hostName = (String) svc.getDynamicProperty(htTmp, "name");
                    HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(htTmp, "summary");
                    HostTo ht = new HostTo();
                    HostRuntimeInfo hostRuntimeInfo = hostSystemInfo.getRuntime();
                    ht.setStatus(hostRuntimeInfo.getConnectionState().toString())
                        .setName(hostName)
                        .setIpAddress(hostName)
                        .setMoId(htTmp.getValue());
                    hosts.add(ht);
                }
                clusterTo.setMoId(clusterVo.getValue());
                String clusterName = (String) svc.getDynamicProperty(clusterVo, "name");
                clusterTo.setName(clusterName);
                clusterTo.setHosts(hosts);
                clusterTos.add(clusterTo);
            }
        }
        return clusterTos;
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

    @Override
    @SuppressWarnings("unchecked")
    public List<DataCenter> getDataCenterBasicInfo(String vmwareId, boolean isActiveHost)
        throws CustomException, FileFaultFaultMsg, InvalidDatastoreFaultMsg, RuntimeFaultFaultMsg {
        logger.info("GetIsoInfo begin...");
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ManagedObjectReference rootFolder;
        List<DataCenter> dataCenters = new ArrayList<>();
        logger.info("map..");
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            logger.error(String.format(Locale.ENGLISH, "The connection of Vmware %s is empty", vmwareId));
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        VimPortType service = serviceConnection.getVimPort();
        if (svc == null || svc.connection == null || content == null) {
            logger.error("The connection of Vmware {} is empty", vmwareId);
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        rootFolder = content.getRootFolder();
        List<ManagedObjectReference> datacenterList = svc.getDecendentMoRefs(rootFolder, "Datacenter");
        ManagedObjectReference hostRef;
        ManagedObjectReference[] hostList;
        // 获取数据中心
        for (ManagedObjectReference obj : datacenterList) {
            DataCenter dc = new DataCenter();
            String datacenterName = (String) svc.getDynamicProperty(obj, "name");
            logger.info("Map..{}", datacenterName);
            dc.setDataCenterName(datacenterName);
            hostRef = (ManagedObjectReference) svc.getDynamicProperty(obj, "hostFolder");
            List<ManagedObjectReference> list = (List<ManagedObjectReference>) svc.getDynamicProperty(hostRef,
                "childEntity");
            hostList = getHostList(list);
            List<Host> exsiHostList = new ArrayList<>();
            // 获取主机列表
            iteratorHost(hostList, svc, exsiHostList, isActiveHost);
            dc.setExsiHostList(exsiHostList);
            dataCenters.add(dc);
        }
        return dataCenters;
    }

    private ManagedObjectReference[] getHostList(List<ManagedObjectReference> list) {
        ManagedObjectReference[] hostList;
        if (list != null) {
            hostList = list.toArray(new ManagedObjectReference[] {});
        } else {
            hostList = new ArrayList<>().toArray(new ManagedObjectReference[] {});
        }
        return hostList;
    }

    private void iteratorHost(ManagedObjectReference[] hostList, ServiceUtil svc, List<Host> exsiHostList,
        Boolean isActiveHost) {
        for (ManagedObjectReference host : hostList) {
            getClusterComputerR(svc, exsiHostList, host, isActiveHost);
        }
    }

    private void getClusterComputerR(ServiceUtil svc, List<Host> exsiHostList, ManagedObjectReference host,
        Boolean isActiveHost) {
        if ("ClusterComputeResource".equals(host.getType())) {
            List<ManagedObjectReference> hostTempList = (List<ManagedObjectReference>) svc.getDynamicProperty(host,
                "host");
            if (hostTempList == null) {
                return;
            }

            getClusterComputeResource(svc, host, exsiHostList, hostTempList, isActiveHost);

        } else if (host.getType().equals("Folder")) {
            List<ManagedObjectReference> hostTempList = (List<ManagedObjectReference>) svc.getDynamicProperty(host,
                "childEntity");
            if (hostTempList == null) {
                return;
            }
            getClusterComputeResource(svc, host, exsiHostList, hostTempList, isActiveHost);
        } else {
            List<ManagedObjectReference> hostTempList = new ArrayList<>();
            getClusterComputeResource(svc, host, exsiHostList, hostTempList, isActiveHost);
        }
    }

    private void getClusterComputeResource(ServiceUtil svc, ManagedObjectReference host, List<Host> exsiHostList,
        List<ManagedObjectReference> hostTempList, Boolean isActiveHost) {
        Host ht = new Host();
        if ("ClusterComputeResource".equals(host.getType())) {
            setDataStor(svc, host, exsiHostList, hostTempList, isActiveHost);
        } else if (host.getType().equals("Folder")) {
            ManagedObjectReference[] datastoreList = setFolderDataStore(svc, exsiHostList, hostTempList, isActiveHost);
            logger.info("DatastoreList....{}", datastoreList);
        } else {
            setNomalDataStore(svc, host, exsiHostList, ht, isActiveHost);
        }
    }

    private void setNomalDataStore(ServiceUtil svc, ManagedObjectReference host, List<Host> exsiHostList, Host ht,
        Boolean isActiveHost) {
        ManagedObjectReference[] datastoreList = null;
        String hostName = (String) svc.getDynamicProperty(host, "name");
        List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(host, "host");
        List<Host> hostlists = new ArrayList<>();
        for (ManagedObjectReference htTmp : hostlist) {
            HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(htTmp, "summary");

            HostRuntimeInfo hostRuntimeInfo = hostSystemInfo.getRuntime();
            // 如果是批量部署则过滤链接不上的
            if (filterActiveHost(isActiveHost, hostRuntimeInfo)) {
                continue;
            }
            ht.setEsxStatus(hostRuntimeInfo.getConnectionState().toString());
            ht.setName(hostName);
            ht.setIp(hostName);
            ht.setMoId(htTmp.getValue());
            hostlists.add(ht);
        }

        List<ManagedObjectReference> talist = (List<ManagedObjectReference>) svc.getDynamicProperty(host, "datastore");
        if (talist != null) {
            datastoreList = talist.toArray(new ManagedObjectReference[] {});
        }
        hostSetDataStor(datastoreList, hostlists, exsiHostList);
    }

    private void setDataStor(ServiceUtil svc, ManagedObjectReference host, List<Host> exsiHostList,
        List<ManagedObjectReference> hostTempList, Boolean isActiveHost) {
        ManagedObjectReference[] datastoreList;
        List<ManagedObjectReference> managedlist = (List<ManagedObjectReference>) svc.getDynamicProperty(host,
            "datastore");
        datastoreList = managedlist.toArray(new ManagedObjectReference[] {});
        List<Host> hostlists = new ArrayList<Host>();

        setHostList(svc, host, hostTempList, isActiveHost, hostlists);
        hostSetDataStor(datastoreList, hostlists, exsiHostList);
    }

    private void setHostList(ServiceUtil svc, ManagedObjectReference host, List<ManagedObjectReference> hostTempList,
        Boolean isActiveHost, List<Host> hostlists) {
        Host ht;
        for (ManagedObjectReference tmpHost : hostTempList) {
            ht = new Host();
            String hostName = (String) svc.getDynamicProperty(tmpHost, "name");
            logger.info("HostName..{}", hostName);
            List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(host, "host");
            getHostList(svc, isActiveHost, hostlists, ht, tmpHost, hostName, hostlist);
        }
    }

    private void getHostList(ServiceUtil svc, Boolean isActiveHost, List<Host> hostlists, Host ht,
        ManagedObjectReference tmpHost, String hostName, List<ManagedObjectReference> hostlist) {
        for (ManagedObjectReference htTmp : hostlist) {
            if (htTmp.getValue().equals(tmpHost.getValue())) {
                HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(htTmp, "summary");
                HostRuntimeInfo hostRuntimeInfo = hostSystemInfo.getRuntime();
                // 如果是批量部署则过滤链接不上的
                if (filterActiveHost(isActiveHost, hostRuntimeInfo)) {
                    continue;
                }
                ht.setEsxStatus(hostRuntimeInfo.getConnectionState().toString());
                ht.setName(hostName);
                ht.setIp(hostName);
                ht.setMoId(htTmp.getValue());
                hostlists.add(ht);
            }
        }
    }

    private void hostSetDataStor(ManagedObjectReference[] datastoreList, List<Host> htLst, List<Host> exsiHostList) {
        for (Host ht : htLst) {
            if (datastoreList != null) {
                List<IsoEnty> dataStoreList = new ArrayList<>();
                ht.setDataStoreList(dataStoreList);
            }
            exsiHostList.add(ht);
        }
    }

    private ManagedObjectReference[] setFolderDataStore(ServiceUtil svc, List<Host> exsiHostList,
        List<ManagedObjectReference> hostTempList, Boolean isActiveHost) {
        Host ht;
        ManagedObjectReference[] datastoreList = null;
        List<Host> hostLst = new ArrayList<Host>();
        for (ManagedObjectReference tmpHost : hostTempList) {
            String hostName = (String) svc.getDynamicProperty(tmpHost, "name");
            logger.info("HostName..{}", hostName);
            List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(tmpHost,
                "host");

            for (ManagedObjectReference htTmp : hostlist) {
                ht = new Host();
                HostListSummary hostSystemInfo = (HostListSummary) svc.getDynamicProperty(htTmp, "summary");
                HostRuntimeInfo hostRuntimeInfo = hostSystemInfo.getRuntime();
                // 如果是批量部署则过滤链接不上的
                if (filterActiveHost(isActiveHost, hostRuntimeInfo)) {
                    continue;
                }
                ht.setEsxStatus(hostRuntimeInfo.getConnectionState().toString());
                ht.setName(hostName);
                ht.setIp(hostName);
                ht.setMoId(htTmp.getValue());
                hostLst.add(ht);
            }
            List<ManagedObjectReference> datalist = (List<ManagedObjectReference>) svc.getDynamicProperty(tmpHost,
                "datastore");
            if (datalist != null) {
                datastoreList = datalist.toArray(new ManagedObjectReference[] {});
            }
        }
        hostSetDataStor(datastoreList, hostLst, exsiHostList);
        return datastoreList;
    }

    private boolean filterActiveHost(Boolean isActiveHost, HostRuntimeInfo hostRuntimeInfo) {
        logger.info("FilterActiveHost begin...");
        if (isActiveHost) {
            if (!"CONNECTED".equalsIgnoreCase(hostRuntimeInfo.getConnectionState().toString())) {
                logger.info("This host is abnormal...");
                return true;
            }
        }
        return false;
    }
}
