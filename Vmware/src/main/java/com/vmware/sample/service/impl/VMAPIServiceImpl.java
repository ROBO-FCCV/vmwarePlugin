/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.vm.CdromInfo;
import com.vmware.sample.model.vm.DiskToInfo;
import com.vmware.sample.model.vm.NetworkBasic;
import com.vmware.sample.model.vm.NetworkInfo;
import com.vmware.sample.model.vm.SnapShotInfo;
import com.vmware.sample.model.vm.VirtualMachineBasic;
import com.vmware.sample.model.vm.VirtualMachineInfo;
import com.vmware.sample.model.vm.VmConfigurationInfo;
import com.vmware.sample.model.vm.VmTemplateInfo;
import com.vmware.sample.model.vm.VmVNCInfo;
import com.vmware.sample.model.vm.VmVNCStatusInfo;
import com.vmware.sample.service.VMService;

import com.vmware.vcenter.VM;
import com.vmware.vcenter.VMTypes;
import com.vmware.vcenter.vm.GuestOS;
import com.vmware.vcenter.vm.Power;
import com.vmware.vcenter.vm.hardware.CdromTypes;
import com.vmware.vcenter.vm.hardware.CpuTypes;
import com.vmware.vcenter.vm.hardware.DiskTypes;
import com.vmware.vcenter.vm.hardware.EthernetTypes;
import com.vmware.vcenter.vm.hardware.MemoryTypes;
import com.vmware.vim25.GuestOsDescriptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Virtual machine API Service
 *
 * @since 2020-09-14
 */
@Slf4j
@Service("vm-api-service")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VMAPIServiceImpl implements VMService {
    private final VmwareAPIClient vmwareAPIClient;

    @Override
    public List<VirtualMachineInfo> getVms(String vmwareId) {
        VM stub = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        VMTypes.FilterSpec.Builder bolder = new VMTypes.FilterSpec.Builder();
        List<VMTypes.Summary> vmSummaryList = stub.list(bolder.build());
        return getVirtualMachineInfos(vmSummaryList);
    }

    private List<VirtualMachineInfo> getVirtualMachineInfos(List<VMTypes.Summary> vmSummaryList) {
        List<VirtualMachineInfo> vmInfoList = new ArrayList<>();
        for (VMTypes.Summary vmSummary : vmSummaryList) {
            VirtualMachineInfo vm = new VirtualMachineInfo();
            vm.setVmName(vmSummary.getName());
            vm.setModId(vmSummary.getVm());
            vm.setVmId(vmSummary.getVm());
            vm.setPowerStatus(vmSummary.getPowerState().toString());
            vm.setCpuCount(vmSummary.getCpuCount());
            vm.setMemorySize(DataSize.ofMegabytes(vmSummary.getMemorySizeMiB()).toBytes());
            vmInfoList.add(vm);
        }
        return vmInfoList;
    }

    @Override
    public List<VirtualMachineInfo> getVmsByHost(String vmwareId, String hostId) {
        VM stub = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        VMTypes.FilterSpec.Builder bolder = new VMTypes.FilterSpec.Builder();
        List<VMTypes.Summary> vmSummaryList = stub.list(bolder.build());
        bolder.setHosts(Collections.singleton(hostId));
        return getVirtualMachineInfos(vmSummaryList);
    }

    @Override
    public List<VirtualMachineBasic> queryVmsByHost(String vmwareId, String hostId) {
        VM vmStub = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        VMTypes.FilterSpec.Builder bolder = new VMTypes.FilterSpec.Builder();
        bolder.setHosts(Collections.singleton(hostId));
        List<VMTypes.Summary> vmSummaryList = vmStub.list(bolder.build());
        List<VirtualMachineBasic> vmBasicList = new ArrayList<>();
        for (VMTypes.Summary vmSummary : vmSummaryList) {
            VirtualMachineBasic vm = new VirtualMachineBasic();
            vm.setVmName(vmSummary.getName());
            vm.setModId(vmSummary.getVm());
            vm.setVmId(vmSummary.getVm());
            vm.setPowerStatus(vmSummary.getPowerState().toString());
            vm.setCpuCount(vmSummary.getCpuCount());
            vm.setMemorySize(DataSize.ofMegabytes(vmSummary.getMemorySizeMiB()).toBytes());
            vmBasicList.add(vm);
        }
        return vmBasicList;
    }

    @Override
    public String powerStopByVmId(String vmwareId, String vmId) {
        Power power = vmwareAPIClient.getStubConfiguration(vmwareId, Power.class);
        power.stop(vmId);
        return RestCodeEnum.SUCCESS.getMsg();
    }

    @Override
    public String powerStartByVmId(String vmwareId, String vmId) {
        Power power = vmwareAPIClient.getStubConfiguration(vmwareId, Power.class);
        power.start(vmId);
        return RestCodeEnum.SUCCESS.getMsg();
    }

    @Override
    public String powerResetByVmId(String vmwareId, String vmId) {
        Power power = vmwareAPIClient.getStubConfiguration(vmwareId, Power.class);
        power.reset(vmId);
        return RestCodeEnum.SUCCESS.getMsg();
    }

    @Override
    public String deleteVmByVmId(String vmwareId, String vmId) {
        VM vm = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        vm.delete(vmId);
        return RestCodeEnum.SUCCESS.getMsg();
    }

    @Override
    public String createVmSnapshot(String vmwareI, SnapShotInfo snapshot) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public List<GuestOsDescriptor> getGuestSystems(String vmwareId, String clusterId, String hostId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String mountVmwareTools(String vmwareId, String vmId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String markVmTemplate(String vmwareId, String vmId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String getVmwareToolsStatus(String vmwareId, String vmId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public Map<String, List<VirtualMachineInfo>> getVmsByHosts(String vmwareId, List<String> hostIds) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public VmVNCInfo getVmVNCbyVmId(String vmwareId, String vmId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public VirtualMachineInfo getVmByVmId(String vmwareId, String vmId) {
        VM stub = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        VMTypes.Info info = stub.get(vmId);
        VirtualMachineInfo virtualMachineInfo = new VirtualMachineInfo();
        virtualMachineInfo.setVmName(info.getName());
        virtualMachineInfo.setPowerStatus(info.getPowerState().name());
        virtualMachineInfo.setOsFullName(info.getGuestOS().name());
        virtualMachineInfo.setCpuCount(info.getCpu().getCount());
        virtualMachineInfo.setNumCoresPerSocket(info.getCpu().getCoresPerSocket());
        virtualMachineInfo.setMemorySize(DataSize.ofMegabytes(info.getMemory().getSizeMiB()).toBytes());
        Map<String, DiskTypes.Info> diskMap = info.getDisks();
        Map<String, EthernetTypes.Info> nicMap = info.getNics();
        setDiskAndNetworks(diskMap, nicMap, virtualMachineInfo);
        return virtualMachineInfo;
    }

    private void setDiskAndNetworks(Map<String, DiskTypes.Info> diskMap, Map<String, EthernetTypes.Info> nicMap,
        VirtualMachineInfo virtualMachineInfo) {
        List<DiskToInfo> diskList = new ArrayList<>();
        List<NetworkBasic> networkBasics = new ArrayList<>();
        for (String key : diskMap.keySet()) {
            DiskToInfo disk = new DiskToInfo();
            disk.setDiskName(diskMap.get(key).getLabel());
            disk.setDiskSize(diskMap.get(key).getCapacity());
            diskList.add(disk);
        }
        for (String key : nicMap.keySet()) {
            NetworkBasic networkBasic = new NetworkBasic();
            networkBasic.setName(nicMap.get(key).getLabel());
            networkBasic.setType(nicMap.get(key).getType().name());
            networkBasic.setConnected("CONNECTED".equals(nicMap.get(key).getState().name()));
            networkBasics.add(networkBasic);
        }
        virtualMachineInfo.setDisks(diskList);
        virtualMachineInfo.setNetworks(networkBasics);
    }

    @Override
    public VmVNCStatusInfo getVmVNCStatus(String vmwareId, String vmId) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }

    @Override
    public String createVmByConfig(String vmwareId, VmConfigurationInfo vmConfigInfo) {
        VMTypes.CreateSpec createSpec = new VMTypes.CreateSpec();
        GuestOS guestOS = GuestOS.valueOf(vmConfigInfo.getOsVersion());
        createSpec.setGuestOS(guestOS);
        createSpec.setName(vmConfigInfo.getVmName());
        VMTypes.PlacementSpec placementSpec = new VMTypes.PlacementSpec();
        placementSpec.setHost(vmConfigInfo.getHostId());
        placementSpec.setCluster(vmConfigInfo.getClusterId());
        placementSpec.setDatastore(vmConfigInfo.getDatastoreId());
        placementSpec.setFolder(vmConfigInfo.getVmFolder());
        createSpec.setPlacement(placementSpec);
        CpuTypes.UpdateSpec updateSpec = new CpuTypes.UpdateSpec();
        updateSpec.setCount(vmConfigInfo.getCpuInfo().getCount().longValue());
        updateSpec.setCoresPerSocket(vmConfigInfo.getCpuInfo().getCoreSockets().longValue());
        createSpec.setCpu(updateSpec);
        MemoryTypes.UpdateSpec memorySpec = new MemoryTypes.UpdateSpec();
        memorySpec.setSizeMiB(vmConfigInfo.getMemorySize());
        createSpec.setMemory(memorySpec);
        createSpec.setNics(getNetwork(vmConfigInfo));
        createSpec.setDisks(getDisks(vmConfigInfo));
        createSpec.setCdroms(getCdrom(vmConfigInfo));
        VM vm = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        vm.create(createSpec);
        return Strings.EMPTY;
    }

    @Override
    public String getVmIdByVmName(String vmwareId, String vmName) {
        VM stub = vmwareAPIClient.getStubConfiguration(vmwareId, VM.class);
        VMTypes.FilterSpec vmFilterSpec = new VMTypes.FilterSpec.Builder().setNames(Collections.singleton(vmName))
            .build();
        List<VMTypes.Summary> vmList = stub.list(vmFilterSpec);
        return vmList.get(0).getVm();
    }

    private List<DiskTypes.CreateSpec> getDisks(VmConfigurationInfo vmConfigInfo) {
        List<DiskTypes.CreateSpec> disks = new ArrayList<>();
        for (int i = 0; i < vmConfigInfo.getDisks().size(); i++) {
            DiskTypes.CreateSpec dataDiskCreateSpec = new DiskTypes.CreateSpec();
            DiskTypes.VmdkCreateSpec vmdkCreateSpec = new DiskTypes.VmdkCreateSpec();
            vmdkCreateSpec.setCapacity(vmConfigInfo.getDisks().get(i).getMemory());
            dataDiskCreateSpec.setNewVmdk(vmdkCreateSpec);
            disks.add(dataDiskCreateSpec);
        }
        return disks;
    }

    private List<EthernetTypes.CreateSpec> getNetwork(VmConfigurationInfo vmConfigInfo) {
        List<EthernetTypes.CreateSpec> createSpecList = new ArrayList<>();
        for (NetworkInfo networkInfo : vmConfigInfo.getNetworks()) {
            EthernetTypes.CreateSpec ethernetTypes = new EthernetTypes.CreateSpec();
            ethernetTypes.setType(EthernetTypes.EmulationType.VMXNET3);
            if (networkInfo.getMacAddress() == null) {
                ethernetTypes.setMacType(EthernetTypes.MacAddressType.GENERATED);
            } else {
                ethernetTypes.setMacType(EthernetTypes.MacAddressType.MANUAL);
                ethernetTypes.setMacAddress(networkInfo.getMacAddress());
            }
            EthernetTypes.BackingSpec backingSpec = new EthernetTypes.BackingSpec();
            backingSpec.setType(EthernetTypes.BackingType.STANDARD_PORTGROUP);
            backingSpec.setNetwork(networkInfo.getName());
            ethernetTypes.setBacking(backingSpec);
            createSpecList.add(ethernetTypes);
        }
        return createSpecList;
    }

    private List<CdromTypes.CreateSpec> getCdrom(VmConfigurationInfo vmConfigInfo) {
        CdromTypes.CreateSpec cdromSpec = new CdromTypes.CreateSpec();
        List<CdromInfo> cdromInfoList = vmConfigInfo.getCdrom();
        for (CdromInfo cdromInfo : cdromInfoList) {
            CdromTypes.BackingSpec backingSpec = new CdromTypes.BackingSpec();
            backingSpec.setType(CdromTypes.BackingType.valueOf(cdromInfo.getDeviceType()));
            if ("ISO_FILE".equals(cdromInfo.getDeviceType())) {
                backingSpec.setIsoFile(cdromInfo.getIsoFile());
            } else if ("HOST_DEVICE".equals(cdromInfo.getDeviceType())) {
                backingSpec.setHostDevice(cdromInfo.getHostDevice());
            }
            backingSpec.setDeviceAccessType(CdromTypes.DeviceAccessType.valueOf(cdromInfo.getAccessType()));
            cdromSpec.setBacking(backingSpec);
        }
        return Collections.singletonList(cdromSpec);
    }

    @Override
    public String cloneVmByTemplate(String vmwareId, VmTemplateInfo vmTemplateInfo) {
        throw new PluginException(RestCodeEnum.API_NONE_IMPLEMENT);
    }
}
