/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.Constants;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.constant.VncConfig;
import cc.plugin.vmware.constant.VncPortTempStore;
import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.vm.Cpu;
import cc.plugin.vmware.model.vo.request.vm.Disk;
import cc.plugin.vmware.model.vo.request.vm.Memory;
import cc.plugin.vmware.model.vo.request.vm.Network;
import cc.plugin.vmware.model.vo.request.vm.PortGroupUrn;
import cc.plugin.vmware.model.vo.request.vm.VmConfigTemplate;
import cc.plugin.vmware.model.vo.response.vm.TaskVmVo;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.StringRandom;

import com.vmware.connection.helpers.builders.ObjectSpecBuilder;
import com.vmware.connection.helpers.builders.PropertyFilterSpecBuilder;
import com.vmware.connection.helpers.builders.PropertySpecBuilder;
import com.vmware.vim25.ArrayOfVirtualDevice;
import com.vmware.vim25.ClusterComputeResourceSummary;
import com.vmware.vim25.CustomizationAdapterMapping;
import com.vmware.vim25.CustomizationFaultFaultMsg;
import com.vmware.vim25.CustomizationFixedIp;
import com.vmware.vim25.CustomizationFixedName;
import com.vmware.vim25.CustomizationGlobalIPSettings;
import com.vmware.vim25.CustomizationGuiUnattended;
import com.vmware.vim25.CustomizationIPSettings;
import com.vmware.vim25.CustomizationIdentification;
import com.vmware.vim25.CustomizationLicenseDataMode;
import com.vmware.vim25.CustomizationLicenseFilePrintData;
import com.vmware.vim25.CustomizationLinuxPrep;
import com.vmware.vim25.CustomizationPassword;
import com.vmware.vim25.CustomizationSpec;
import com.vmware.vim25.CustomizationSysprep;
import com.vmware.vim25.CustomizationUserData;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.Description;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.MigrationFaultFaultMsg;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.ToolsConfigInfo;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDeviceConnectInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskCompatibilityMode;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualDiskMode;
import com.vmware.vim25.VirtualDiskRawDiskMappingVer1BackingInfo;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualPCNet32;
import com.vmware.vim25.VirtualSCSIController;
import com.vmware.vim25.VirtualVmxnet3;
import com.vmware.vim25.VmConfigFaultFaultMsg;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The type Vm by template.
 *
 * @since 2019 -09-19
 */
@Service
public class VmByTemplateImpl extends AbstractVmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmByTemplateImpl.class);

    private static final int SCSI_CONTROLLER_MAX_DEVICE = 16;

    private static final int SCSI_CONTROLLER_RESERVE_SLOT = 7;

    @Autowired
    private VncConfig vncConfig;

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    @Override
    public TaskVmVo createVmByTemplate(String vmwareId, VmConfigTemplate vmConfigTemplate) throws CustomException {
        LOGGER.info("###create vm by template, params={}",vmConfigTemplate);
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        ServiceUtil svc = ecb.getServiceUtil();
        Object obj = new Object();
        locked(obj);
        TaskVmVo taskVmVo = new TaskVmVo();

        vmConfigTemplate.setVmName(StringUtils.replace(vmConfigTemplate.getVmName(), "%", "%25"));
        ManagedObjectReference datacenterRef = getDatacenterRef(vmConfigTemplate.getDatacenterName(), service,
            serviceContent);

        ManagedObjectReference vmFolderRef = (ManagedObjectReference) svc.getObjectProperty(serviceContent, service,
            datacenterRef, "vmFolder");
        ManagedObjectReference vmRef = svc.getDecendentMoRef(null, "VirtualMachine",
            vmConfigTemplate.getTemplateName());
        String hostName = vmConfigTemplate.getHostName();
        if (hostName != null) {
            if (hostName.contains("domain")) {
                return clusterHanlde(vmConfigTemplate, svc, datacenterRef, vmFolderRef, vmRef, service, vmwareId);
            } else {
                ManagedObjectReference hostSystem = svc.getDecendentMoRef(datacenterRef, "HostSystem", hostName);
                ManagedObjectReference computeResource = svc.getMoRefProp(hostSystem, "parent");
                ManagedObjectReference resourcePool = svc.getMoRefProp(computeResource, "resourcePool");

                ManagedObjectReference dataStoreRef = new ManagedObjectReference();
                dataStoreRef = setDataStoreRef(vmConfigTemplate.getDatastoreId(), svc, hostSystem, dataStoreRef);
                // 异常判断
                errorInfo(dataStoreRef, vmFolderRef, vmRef, hostSystem, resourcePool, datacenterRef);

                VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
                VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();
                relocSpec.setDatastore(dataStoreRef);
                relocSpec.setPool(resourcePool);
                relocSpec.setHost(hostSystem);
                cloneSpec.setLocation(relocSpec);
                cloneSpec.setPowerOn(Boolean.parseBoolean(vmConfigTemplate.getAutoBoot()));
                cloneSpec.setTemplate(false);
                VirtualMachineConfigSpec config = new VirtualMachineConfigSpec();
                config.setName(vmConfigTemplate.getVmName());
                config.setAnnotation("virtual machine");
                // VNC配置参数
                OptionValue optionVncEnabled = new OptionValue();
                optionVncEnabled.setKey(Constants.VNC_ENABLED);
                optionVncEnabled.setValue(Boolean.TRUE.toString());
                config.getExtraConfig().add(optionVncEnabled);
                OptionValue optionVncPassword = new OptionValue();
                optionVncPassword.setKey(Constants.VNC_PWD);
                optionVncPassword.setValue(StringRandom.getStringRandom(Constants.VNC_PASSWORD_LENGTH_EIGHT));
                config.getExtraConfig().add(optionVncPassword);
                OptionValue optionVncPort = new OptionValue();
                optionVncPort.setKey(Constants.VNC_PORT);
                int vncPort = getVncPort(svc, hostSystem);
                optionVncPort.setValue(vncPort);
                config.getExtraConfig().add(optionVncPort);
                // 获取操作系统类型
                String osType = getOsType(svc, vmRef);
                LOGGER.info("osType... {}", osType);
                // update network----
                List<VirtualDeviceConfigSpec> deviceChangelist = getNicList(vmConfigTemplate.getNics(), osType);
                config.getDeviceChange().addAll(deviceChangelist);

                String dataStoreName = (String) svc.getDynamicProperty(dataStoreRef, "name");
                int controllerKey = Constants.PARA_VIRTUAL_SCSI_CONTROLLER_KEY;
                List<Disk> disks = vmConfigTemplate.getDisks();
                if (disks != null) {
                    setDisks(disks, dataStoreName, vmConfigTemplate.getVmName(), controllerKey, config);
                }

                // 创建RDM磁盘的配置
                List<Integer> ctlKeys = getControllerKey(serviceContent, service, vmRef);
                createVirtualDiskMapConfigSpec(ctlKeys, vmConfigTemplate.getLunNames(), config, dataStoreName);
                clone(vmConfigTemplate, svc, hostSystem, cloneSpec, config, osType);
                setTaskVmVo(vmConfigTemplate, service, svc, taskVmVo, vmFolderRef, vmRef,
                    hostSystem, cloneSpec, vncPort, osType);
            }
        }
        return taskVmVo;
    }

    private void setTaskVmVo(VmConfigTemplate vmConfigTemplate, VimPortType service, ServiceUtil svc, TaskVmVo taskVmVo,
        ManagedObjectReference vmFolderRef, ManagedObjectReference vmRef, ManagedObjectReference hostSystem,
        VirtualMachineCloneSpec cloneSpec, int vncPort, String osType) {
        try {
            ManagedObjectReference cloneTask = service.cloneVMTask(vmRef, vmFolderRef,
                vmConfigTemplate.getVmName(), cloneSpec);

            taskVmVo.setTaskUri(cloneTask.getValue());
            TaskInfo task = (TaskInfo) svc.getDynamicProperty(cloneTask, "info");
            if (task != null) {
                taskVmVo.setStatus(task.getState().toString());
            }
            taskVmVo.setOsType(osType);
            taskVmVo.setTemplateId(vmRef.getValue());
            long timeOut = 1000 * 1000; // 10秒超时
            long sleepTimeCount = 0; // 起始计时
            waitGetResourceId(sleepTimeCount, timeOut, taskVmVo, hostSystem, vmConfigTemplate.getVmName(), svc);
        } catch (CustomizationFaultFaultMsg | FileFaultFaultMsg | InsufficientResourcesFaultFaultMsg | InvalidDatastoreFaultMsg | InvalidStateFaultMsg | MigrationFaultFaultMsg | RuntimeFaultFaultMsg | TaskInProgressFaultMsg | VmConfigFaultFaultMsg e) {
            LOGGER.error(e.getMessage(), e);
            VncPortTempStore.getInstance().removePortSet(vncPort);
            throw new ApplicationException("vmware clone fail : " + e.getMessage(), e);
        }
    }

    private List<Integer> getControllerKey(ServiceContent content, VimPortType service, ManagedObjectReference vmMor) {
        List<Integer> retVal = new ArrayList<Integer>();

        if (null == vmMor) {
            return new ArrayList<>();
        }

        List<VirtualDevice> listvd = getVirtualDevices(content, service, vmMor);

        Map<Integer, VirtualDevice> deviceMap = new HashMap<Integer, VirtualDevice>();
        for (VirtualDevice virtualDevice : listvd) {
            deviceMap.put(virtualDevice.getKey(), virtualDevice);
        }
        boolean found = false;
        found = setVdKey(retVal, listvd, deviceMap, found);

        if (!found) {
            throw new RuntimeException("The SCSI controller on the vm has maxed out its "
                + "capacity. Please add an additional SCSI controller");
        }
        return retVal;
    }

    private boolean setVdKey(List<Integer> retVal, List<VirtualDevice> listvd, Map<Integer, VirtualDevice> deviceMap,
        boolean found) {
        for (VirtualDevice virtualDevice : listvd) {
            if (virtualDevice instanceof VirtualSCSIController) {
                found = setFound(retVal, deviceMap, found, (VirtualSCSIController) virtualDevice);
            }
        }
        return found;
    }

    private boolean setFound(List<Integer> retVal, Map<Integer, VirtualDevice> deviceMap, boolean found,
        VirtualSCSIController virtualDevice) {
        VirtualSCSIController vscsic = virtualDevice;
        int[] slots = new int[SCSI_CONTROLLER_MAX_DEVICE];
        slots[SCSI_CONTROLLER_RESERVE_SLOT] = 1;
        List<Integer> devicelist = vscsic.getDevice();
        for (Integer deviceKey : devicelist) {
            if (deviceMap.containsKey(deviceKey)) {
                if (deviceMap.get(deviceKey) != null && deviceMap.get(deviceKey).getUnitNumber() != null) {
                    slots[deviceMap.get(deviceKey).getUnitNumber()] = 1;
                }
            }
        }
        found = setFoundFlag(retVal, found, vscsic, slots);
        return found;
    }

    /**
     * Create virtual disk map config spec.
     *
     * @param ctlKeys the ctl keys
     * @param lunNames the lun names
     * @param config the config
     */
    public void createVirtualDiskMapConfigSpec(List<Integer> ctlKeys, List<String> lunNames,
        VirtualMachineConfigSpec config, String dataStoreName) {
        if ("\"\"".equals(lunNames) || ctlKeys == null || ctlKeys.size() <= 0) {
            LOGGER.info("can not create RDM Disk for no lun or no controllerkey.");
            return;
        }
        int index = 0;
        for (String lunName : lunNames) {
            int crlk = getControllerKey(index, ctlKeys);
            int untNb = getUnitNumber(index, ctlKeys);
            index++;

            VirtualDeviceConfigSpec cfg = createVirtualDiskConfigSpec(crlk, untNb, lunName, dataStoreName);
            config.getDeviceChange().add(cfg);
        }
    }

    /**
     * Create virtual disk config spec virtual device config spec.
     *
     * @param controllerkey the controllerkey
     * @param unitNumber the unit number
     * @param deviceName the device name
     * @return the virtual device config spec
     */
    VirtualDeviceConfigSpec createVirtualDiskConfigSpec(int controllerkey, int unitNumber, String deviceName, String dataStoreName) {
        VirtualDeviceConnectInfo vdci = new VirtualDeviceConnectInfo();
        vdci.setStartConnected(true);
        vdci.setConnected(true);
        vdci.setAllowGuestControl(false);

        VirtualDisk newvirtualdisk = new VirtualDisk();
        newvirtualdisk.setControllerKey(controllerkey);
        newvirtualdisk.setUnitNumber(unitNumber);

        // rdm类型磁盘容量就是自身大小,不支持设置,这里仅设置任意值
        newvirtualdisk.setCapacityInKB(1);
        newvirtualdisk.setKey(0);
        newvirtualdisk.setConnectable(vdci);

        VirtualDiskRawDiskMappingVer1BackingInfo rdmorrdmpbackinginfo = new VirtualDiskRawDiskMappingVer1BackingInfo();

        rdmorrdmpbackinginfo.setCompatibilityMode(VirtualDiskCompatibilityMode.VIRTUAL_MODE.value());
        rdmorrdmpbackinginfo.setDeviceName(deviceName);
        rdmorrdmpbackinginfo.setDiskMode("persistent");
        // RDM会存在与VMFS上的一个卷，这里需要设置DataStore
        rdmorrdmpbackinginfo.setFileName("[" + dataStoreName + "]");
        newvirtualdisk.setBacking(rdmorrdmpbackinginfo);

        VirtualDeviceConfigSpec virtualdiskconfigspec = new VirtualDeviceConfigSpec();
        virtualdiskconfigspec.setFileOperation(VirtualDeviceConfigSpecFileOperation.CREATE);
        virtualdiskconfigspec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        virtualdiskconfigspec.setDevice(newvirtualdisk);
        return virtualdiskconfigspec;
    }

    /**
     * Gets vnc port.
     *
     * @param svc the svc
     * @param hostSystem the host system
     * @return the vnc port
     */
    public int getVncPort(ServiceUtil svc, ManagedObjectReference hostSystem) {
        List<Integer> vncUsedPortGroup = new ArrayList<Integer>();
        List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem, "vm");
        if (vmList != null) {
            iteratorVmList(vmList, vncUsedPortGroup, svc);
        }
        Collections.sort(vncUsedPortGroup);
        int vncPort = 0;
        // 默认 5901 到 6150 为vnc可用端口 详见yml文件
        int portMin = Integer.parseInt(vncConfig.getPortMin()) + 1;
        int portMax = Integer.parseInt(vncConfig.getPortMax());
        for (int num = portMin; num <= portMax; num++) {
            if (vncUsedPortGroup.indexOf(num) == -1) {
                if (!VncPortTempStore.getInstance().getPortSet().contains(num)) {
                    vncPort = num;
                    break;
                }
            }
        }
        // 并行创建虚拟机时 端口没有存到vmware时会重复 故使用临时存储存入端口 判断是否已使用
        VncPortTempStore.getInstance().putPortSet(vncPort);
        return vncPort;
    }

    private void iteratorVmList(List<ManagedObjectReference> vmList, List<Integer> vncUsedPortGroup, ServiceUtil svc) {
        for (ManagedObjectReference vm : vmList) {
            iterateExtraConfig(vncUsedPortGroup, svc, vm);
        }
    }

    private void iterateExtraConfig(List<Integer> vncUsedPortGroup, ServiceUtil svc, ManagedObjectReference vm) {
        try {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            VirtualMachineConfigInfo vconfig;
            if (vmConfigInfo == null) {
                return;
            } else {
                vconfig = (VirtualMachineConfigInfo) vmConfigInfo;
            }
            for (OptionValue option : vconfig.getExtraConfig()) {
                if (Constants.VNC_PORT.equals(option.getKey())) {
                    vncUsedPortGroup.add(Integer.valueOf(option.getValue().toString()));
                }
            }
        } catch (Exception e) {
            LOGGER.info("vm is creating message... {}", e.getMessage(), e);
        }
    }

    /**
     * Gets nic list.
     *
     * @param networks the networks
     * @param osType the os type
     * @return the nic list
     */
    public List<VirtualDeviceConfigSpec> getNicList(List<Network> networks, String osType) {
        List<VirtualDeviceConfigSpec> deviceChanges = new ArrayList<VirtualDeviceConfigSpec>();

        for (Network network : networks) {
            if ("dvs".equals(network.getType())) {
                deviceChanges.add(setBasicInfo(osType, network));
            } else {
                deviceChanges.add(addNicSpec(network.getPortGroupUrn().getPortgroupId(), network.getMacAddress()));
            }
        }
        return deviceChanges;
    }

    private VirtualDeviceConfigSpec addNicSpec(String portGroup, String macAddress) {
        VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
        virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        VirtualEthernetCard virtualEthernetCard = new VirtualVmxnet3();
        VirtualEthernetCardNetworkBackingInfo nicBackingInfo = new VirtualEthernetCardNetworkBackingInfo();
        nicBackingInfo.setDeviceName(portGroup);
        if (macAddress == null) {
            virtualEthernetCard.setAddressType("Generated");
        } else { // 手动设置macaddress
            virtualEthernetCard.setAddressType("Manual");
            virtualEthernetCard.setMacAddress(macAddress);
        }
        virtualEthernetCard.setBacking(nicBackingInfo);
        virtualEthernetCard.setKey(4);
        virtualDeviceConfigSpec.setDevice(virtualEthernetCard);
        return virtualDeviceConfigSpec;
    }

    private VirtualDeviceConfigSpec setBasicInfo(String osType, Network newwork) {
        PortGroupUrn portGroupurn = newwork.getPortGroupUrn();
        VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
        virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        VirtualEthernetCard virtualEthernetCard;
        if ("WINDOWS".equalsIgnoreCase(osType)) {
            virtualEthernetCard = new VirtualE1000();
        } else {
            virtualEthernetCard = new VirtualPCNet32();
        }

        VirtualEthernetCardDistributedVirtualPortBackingInfo nicBacking
            = new VirtualEthernetCardDistributedVirtualPortBackingInfo();
        DistributedVirtualSwitchPortConnection portConn = new DistributedVirtualSwitchPortConnection();
        portConn.setPortgroupKey(portGroupurn.getPortgroupKey());
        portConn.setSwitchUuid(newwork.getSwitchUuid());
        nicBacking.setPort(portConn);
        virtualEthernetCard.setBacking(nicBacking);
        if (newwork.getMacAddress() == null) {
            virtualEthernetCard.setAddressType("Generated");
        } else { // 手动设置macaddress
            virtualEthernetCard.setAddressType("Manual");
            virtualEthernetCard.setMacAddress(newwork.getMacAddress());
        }

        // 数据来源于网卡创建时sitePortId
        Description desc = new Description();
        desc.setLabel(portGroupurn.getPortgroupKey());
        desc.setSummary(portGroupurn.getPortgroupKey());
        virtualEthernetCard.setDeviceInfo(desc);
        virtualDeviceConfigSpec.setDevice(virtualEthernetCard);
        return virtualDeviceConfigSpec;
    }

    private void locked(Object obj) {
        synchronized (obj) {
            try {
                obj.wait(3000);
            } catch (InterruptedException e) {
                LOGGER.error("cloneVM sleep fail", e);
            }
        }
    }

    private void waitGetResourceId(long sleepTimeCount, long timeOut, TaskVmVo taskVmVo,
        ManagedObjectReference hostSystem, String vmName, ServiceUtil svc) {
        ManagedObjectReference newVmRef = null;
        // 虚拟机对象出现会延迟几秒 所以循环等待
        while (newVmRef == null && sleepTimeCount <= timeOut) {
            waitOneSecond();
            String newVmName = StringUtils.replace(vmName, "/", "%2f");
            newVmRef = svc.getDecendentMoRef(hostSystem, "VirtualMachine", newVmName);
            sleepTimeCount += 1000;
        }

        newVmRefHandle(taskVmVo, newVmRef);
    }

    private void clone(VmConfigTemplate vmConfigTemplate, ServiceUtil svc, ManagedObjectReference hostSystem,
        VirtualMachineCloneSpec cloneSpec, VirtualMachineConfigSpec config, String osType) {
        // 如果传入的cpu核数大于主机的核数 默认使用主机最大的核数 不然会导致创建的虚拟机无法启动
        HostHardwareInfo hostHardwareInfo = (HostHardwareInfo) svc.getDynamicProperty(hostSystem, "hardware");
        Memory memory = vmConfigTemplate.getMemory();
        buildConfigObject(vmConfigTemplate.getCpu(), memory.getQuantityMb(), config, hostHardwareInfo);
        // tools
        ToolsConfigInfo info = new ToolsConfigInfo();
        info.setAfterPowerOn(true);
        info.setAfterResume(true);
        info.setBeforeGuestStandby(false);
        info.setBeforeGuestReboot(true);
        info.setBeforeGuestShutdown(true);
        config.setTools(info);
        cloneSpec.setConfig(config);

        // 添加
        CustomizationSpec customization = new CustomizationSpec();
        ArrayList<CustomizationAdapterMapping> array = new ArrayList<CustomizationAdapterMapping>();
        buildCustomization(vmConfigTemplate.getNics(), customization, array);
        CustomizationPassword customizationPassword = new CustomizationPassword();
        customizationPassword.setPlainText(true);
        customizationPassword.setValue(vmConfigTemplate.getOspassword());
        CustomizationGuiUnattended guiUnattended = new CustomizationGuiUnattended();
        guiUnattended.setPassword(customizationPassword);
        guiUnattended.setAutoLogon(false);
        guiUnattended.setTimeZone(201);
        guiUnattended.setAutoLogonCount(0);

        // 系统主机名称有特殊会报错 故去掉特殊字符
        String osHostName = CommonUtil.filterSymbol(vmConfigTemplate.getVmName());

        CustomizationFixedName fixedName = new CustomizationFixedName();
        // add by zhl 删除无用的ifelse分支 20170706
        fixedName.setName(osHostName);

        CustomizationUserData userdata = new CustomizationUserData();
        userdata.setComputerName(fixedName);
        userdata.setFullName(osHostName);
        userdata.setOrgName("LocalDomain");
        userdata.setProductId("");
        CustomizationPassword domainCustomizationPassword = new CustomizationPassword();
        domainCustomizationPassword.setPlainText(true);
        domainCustomizationPassword.setValue(vmConfigTemplate.getOspassword());
        CustomizationIdentification identification = new CustomizationIdentification();
        identification.setJoinWorkgroup("WORKGROUP");
        identification.setDomainAdminPassword(domainCustomizationPassword);
        buildCustomizationObject(osType, customization, guiUnattended, fixedName, userdata, identification);
        CustomizationGlobalIPSettings globalIPSettings = new CustomizationGlobalIPSettings();
        List<String> dnsSuffixLists = new ArrayList<>();
        List<String> dnsServerLists = new ArrayList<>();
        List<String> allDns = new ArrayList<>();
        buildAllDns(vmConfigTemplate.getNics(), allDns);
        iteratorAllDnsObject(dnsSuffixLists, dnsServerLists, allDns);
        globalIPSettings.getDnsSuffixList().addAll(dnsSuffixLists);
        globalIPSettings.getDnsServerList().addAll(dnsServerLists);
        customization.setGlobalIPSettings(globalIPSettings);
        cloneSpec.setCustomization(customization);
        LOGGER.info("Launching clone task to create a clone");
    }

    private void buildConfigObject(Cpu cpu, long ramSize, VirtualMachineConfigSpec config,
        HostHardwareInfo hostHardwareInfo) {
        int cpuNum = cpu.getQuantity();
        if (hostHardwareInfo != null && cpuNum > hostHardwareInfo.getCpuInfo().getNumCpuCores()) {
            LOGGER.info("setting cpuNum bigger than host cpuNum: {}", hostHardwareInfo.getCpuInfo().getNumCpuCores());
            cpuNum = hostHardwareInfo.getCpuInfo().getNumCpuCores();
        }
        config.setNumCPUs(cpuNum);
        config.setNumCoresPerSocket(1);

        String cpuCores = cpu.getCpuCores();
        if (StringUtils.isNotEmpty(cpuCores)) {
            config.setNumCoresPerSocket(Integer.parseInt(cpuCores));
        }

        // 如果传入的内存大于主机的内存 默认使用主机的最大内存 不然会导致创建的虚拟机无法启动 RAMsize单位为MB
        long ramB = ramSize * 1024;
        long memorySize = hostHardwareInfo == null ? 0 : hostHardwareInfo.getMemorySize();
        buildRamToCreateSpec(ramSize, config, ramB, memorySize);
    }

    /**
     * Build ram to create spec.
     *
     * @param ramSize the ram size
     * @param createSpec the create spec
     * @param ramB the ram b
     * @param memorySize the memory size
     */
    public void buildRamToCreateSpec(long ramSize, VirtualMachineConfigSpec createSpec, long ramB, long memorySize) {
        if (ramB > (memorySize / 1024)) {
            LOGGER.info("setting memorySize bigger than host memorySize: {}", memorySize);
            long hostMb = memorySize / (1024 * 1024);
            // 内存必须是4MB的倍数才能启动虚拟机
            ramSize = hostMb - hostMb % 4;
        }
        createSpec.setMemoryMB(ramSize);
    }

    private int getControllerKey(int index, List<Integer> ctlKeys) {
        if (index >= ctlKeys.size() / 2) {
            LOGGER.info("out of ctlKeys length.");
            return -1;
        }
        return ctlKeys.get(2 * index);
    }

    private int getUnitNumber(int index, List<Integer> ctlKeys) {
        if (index >= ctlKeys.size() / 2) {
            LOGGER.info("out of ctlKeys length.");
            return -1;
        }
        return ctlKeys.get((2 * index) + 1);
    }

    private boolean setFoundFlag(List<Integer> retVal, boolean found, VirtualSCSIController vscsic, int[] slots) {
        for (int num = 0; num < slots.length; num++) {
            if (slots[num] != 1) {
                retVal.add(vscsic.getKey());
                retVal.add(num);
                found = true;
            }
        }
        return found;
    }

    /**
     * Gets virtual devices.
     *
     * @param content the content
     * @param service the service
     * @param vmMor the vm mor
     * @return the virtual devices
     */
    public List<VirtualDevice> getVirtualDevices(ServiceContent content, VimPortType service,
        ManagedObjectReference vmMor) {
        List<VirtualDevice> listvd = new ArrayList<>();
        try {
            Map<String, Object> entityMap = entityProps(content, service, vmMor,
                new String[] {"config.hardware.device"});
            if (entityMap != null && entityMap.containsKey("config.hardware.device")) {
                if (entityMap.get("config.hardware.device") != null) {
                    listvd = ((ArrayOfVirtualDevice) entityMap.get("config.hardware.device")).getVirtualDevice();
                }
            }
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
            LOGGER.error(
                "get entityProps fail, param is config.hardware.device, vmMor type is {}, vmMor value is {}",
                vmMor.getType(), vmMor.getValue());
            throw new ApplicationException("vmWare createSnapshot fail :{}", e.getMessage());
        }
        return listvd;
    }

    /**
     * Entity props map.
     *
     * @param content the content
     * @param service the service
     * @param entityMor the entity mor
     * @param props the props
     * @return the map
     * @throws InvalidPropertyFaultMsg the invalid property fault msg
     * @throws RuntimeFaultFaultMsg the runtime fault fault msg
     */
    public Map<String, Object> entityProps(ServiceContent content, VimPortType service,
        ManagedObjectReference entityMor, String[] props) throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        final HashMap<String, Object> result = new HashMap<>();

        // Create PropertyFilterSpec using the PropertySpec and ObjectPec
        PropertyFilterSpec[] propertyFilterSpecs = {
            new PropertyFilterSpecBuilder().propSet(
                // Create Property Spec
                new PropertySpecBuilder().all(Boolean.FALSE).type(entityMor.getType()).pathSet(props)).objectSet(
                // Now create Object Spec
                new ObjectSpecBuilder().obj(entityMor))
        };

        List<ObjectContent> objectContents = service.retrievePropertiesEx(content.getPropertyCollector(),
            Arrays.asList(propertyFilterSpecs), new RetrieveOptions()).getObjects();

        if (objectContents != null) {
            for (ObjectContent objectContent : objectContents) {
                List<DynamicProperty> dps = objectContent.getPropSet();
                for (DynamicProperty dp : dps) {
                    result.put(dp.getName(), dp.getVal());
                }
            }
        }
        return result;
    }

    private void setDisks(List<Disk> disks, String dataStoreName, String vmName, int controllerKey,
        VirtualMachineConfigSpec config) {
        for (int i = 0; i < disks.size(); i++) {
            // 添加硬盘 存储空间
            VirtualDeviceConfigSpec virtualDeviceConfigSpecDisk = new VirtualDeviceConfigSpec();
            virtualDeviceConfigSpecDisk.setOperation(VirtualDeviceConfigSpecOperation.EDIT);
            VirtualDiskFlatVer2BackingInfo virtualDiskFlatVer2BackingInfo = new VirtualDiskFlatVer2BackingInfo();
            virtualDiskFlatVer2BackingInfo.setDiskMode(VirtualDiskMode.PERSISTENT.value());
            // 格式 "[dataStoreName] vmName/vmName.vmdk"
            StringBuffer fileNameSb = new StringBuffer();
            if (i == 0) {
                fileNameSb.append("[")
                    .append(dataStoreName)
                    .append("] ")
                    .append(vmName)
                    .append(File.separator)
                    .append(vmName)
                    .append(".vmdk");
            } else {
                fileNameSb.append("[")
                    .append(dataStoreName)
                    .append("] ")
                    .append(vmName)
                    .append(File.separator)
                    .append(vmName + "_" + i)
                    .append(".vmdk");
            }
            virtualDiskFlatVer2BackingInfo.setFileName(fileNameSb.toString());
            VirtualDisk virtualDisk = new VirtualDisk();
            int capacityGB = disks.get(i).getQuantityGb();
            virtualDisk.setCapacityInKB(1024L * 1024L * capacityGB);
            virtualDisk.setKey(Constants.VIRTUAL_DISK_KEY + i);
            virtualDisk.setControllerKey(controllerKey);
            virtualDisk.setUnitNumber(Constants.DEFAULT_VIRTUAL_DISK_UNIT_NUMBER + i); // 必须填 不填报错
            virtualDisk.setBacking(virtualDiskFlatVer2BackingInfo);
            virtualDeviceConfigSpecDisk.setDevice(virtualDisk);
            config.getDeviceChange().add(virtualDeviceConfigSpecDisk);
        }
    }

    private void errorInfo(ManagedObjectReference dataStoreRef, ManagedObjectReference vmFolderRef,
        ManagedObjectReference vmRef, ManagedObjectReference hostSystem, ManagedObjectReference resourcePool,
        ManagedObjectReference datacenterRef) {

        creamVmErrorInfo(dataStoreRef, vmFolderRef, hostSystem, resourcePool, datacenterRef);
        if (vmRef == null) {
            throw new ApplicationException("The virtual template is not found");
        }
    }

    private void creamVmErrorInfo(ManagedObjectReference dataStoreRef, ManagedObjectReference vmFolderRef,
        ManagedObjectReference hostSystem, ManagedObjectReference resourcePool, ManagedObjectReference datacenterRef) {

        dealDatacenRefNotValid(datacenterRef);
        if (vmFolderRef == null) {
            throw new ApplicationException("The vmFolder is not found");
        }

        if (hostSystem == null) {
            throw new ApplicationException("The hostSystem is not found");
        }

        if (resourcePool == null) {
            throw new ApplicationException("The resourcePool is not found");
        }

        // 默认取最大的datastore
        if (dataStoreRef == null) {
            throw new ApplicationException("The dataStoreRef is not found");
        }

    }

    private TaskVmVo clusterHanlde(VmConfigTemplate vmConfigTemplate, ServiceUtil svc,
        ManagedObjectReference datacenterRef, ManagedObjectReference vmFolderRef, ManagedObjectReference vmRef,
        VimPortType service, String vmwareId) {
        ManagedObjectReference clusterSystem = new ManagedObjectReference();
        clusterSystem.setType("ClusterComputeResource");
        clusterSystem.setValue(vmConfigTemplate.getHostName());

        ManagedObjectReference resourcePool = svc.getMoRefProp(clusterSystem, "resourcePool");

        // 默认取最大的datastore
        ManagedObjectReference dataStoreRef = new ManagedObjectReference();
        dataStoreRef = setDataStoreRef(vmConfigTemplate.getDatastoreId(), svc, clusterSystem, dataStoreRef);

        // 异常判断
        errorClusterInfo(dataStoreRef, vmFolderRef, vmRef, resourcePool, datacenterRef);

        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();
        relocSpec.setDatastore(dataStoreRef);
        relocSpec.setPool(resourcePool);
        cloneSpec.setLocation(relocSpec);
        cloneSpec.setPowerOn(Boolean.parseBoolean(vmConfigTemplate.getAutoBoot()));
        cloneSpec.setTemplate(false);
        VirtualMachineConfigSpec config = new VirtualMachineConfigSpec();
        config.setName(vmConfigTemplate.getVmName());
        config.setAnnotation("virtual machine");
        // VNC配置参数
        OptionValue optionVncEnabled = new OptionValue();
        optionVncEnabled.setKey(Constants.VNC_ENABLED);
        optionVncEnabled.setValue(Boolean.TRUE.toString());
        config.getExtraConfig().add(optionVncEnabled);
        OptionValue optionVncPassword = new OptionValue();
        optionVncPassword.setKey(Constants.VNC_PWD);
        optionVncPassword.setValue(StringRandom.getStringRandom(Constants.VNC_PASSWORD_LENGTH_EIGHT));
        config.getExtraConfig().add(optionVncPassword);
        OptionValue optionVncPort = new OptionValue();
        optionVncPort.setKey(Constants.VNC_PORT);
        int vncPort = getClusterVncPort(svc, clusterSystem);
        optionVncPort.setValue(vncPort);
        config.getExtraConfig().add(optionVncPort);
        // 获取操作系统类型
        String osType = getOsType(svc, vmRef);
        LOGGER.info("osType...{}", osType);
        // update network----
        List<VirtualDeviceConfigSpec> deviceChangelist = getNicList(vmConfigTemplate.getNics(), osType);
        config.getDeviceChange().addAll(deviceChangelist);

        // 如果传入的cpu核数大于主机的核数 默认使用主机最大的核数 不然会导致创建的虚拟机无法启动
        ClusterComputeResourceSummary clusterComputeResourceSummary
            = (ClusterComputeResourceSummary) svc.getDynamicProperty(clusterSystem, "summary");

        Memory memory = vmConfigTemplate.getMemory();
        buildclusterConfigObject(vmConfigTemplate.getCpu().getQuantity(), memory.getQuantityMb(), config,
            clusterComputeResourceSummary);
        // tools
        ToolsConfigInfo info = new ToolsConfigInfo();
        info.setAfterPowerOn(true);
        info.setAfterResume(true);
        info.setBeforeGuestStandby(false);
        info.setBeforeGuestReboot(true);
        info.setBeforeGuestShutdown(true);
        config.setTools(info);
        cloneSpec.setConfig(config);

        return cluster(svc, datacenterRef, vmFolderRef, vmRef, vmConfigTemplate.getVmName(), vmConfigTemplate.getNics(),
            vmConfigTemplate.getOspassword(), service, vmwareId, vmConfigTemplate.getCloneVmOnly(), clusterSystem,
            cloneSpec, vncPort, osType);
    }

    /**
     * Buildcluster config object.
     *
     * @param cupNum the cup num
     * @param ramSize the ram size
     * @param config the config
     * @param clusterComputeResourceSummary the cluster compute resource summary
     */
    public void buildclusterConfigObject(int cupNum, long ramSize, VirtualMachineConfigSpec config,
        ClusterComputeResourceSummary clusterComputeResourceSummary) {
        if (clusterComputeResourceSummary != null && cupNum > clusterComputeResourceSummary.getNumCpuCores()) {
            LOGGER.info("setting cpuNum bigger than host cpuNum: {}", clusterComputeResourceSummary.getNumCpuCores());
            cupNum = clusterComputeResourceSummary.getNumCpuCores();
        }
        config.setNumCPUs(cupNum);
        config.setNumCoresPerSocket(1);

        // 如果传入的内存大于主机的内存 默认使用主机的最大内存 不然会导致创建的虚拟机无法启动 RAMsize单位为MB
        config.setMemoryMB(ramSize);
    }

    private void errorClusterInfo(ManagedObjectReference dataStoreRef, ManagedObjectReference vmFolderRef,
        ManagedObjectReference vmRef, ManagedObjectReference resourcePool, ManagedObjectReference datacenterRef) {

        creamVmClusterErrorInfo(dataStoreRef, vmFolderRef, resourcePool, datacenterRef);
        if (vmRef == null) {
            throw new ApplicationException("The virtual template is not found");
        }
    }

    private void creamVmClusterErrorInfo(ManagedObjectReference dataStoreRef, ManagedObjectReference vmFolderRef,
        ManagedObjectReference resourcePool, ManagedObjectReference datacenterRef) {

        dealDatacenRefNotValid(datacenterRef);
        if (vmFolderRef == null) {
            throw new ApplicationException("The vmFolder is not found");
        }

        if (resourcePool == null) {
            throw new ApplicationException("The resourcePool is not found");
        }

        // 默认取最大的datastore
        if (dataStoreRef == null) {
            throw new ApplicationException("The dataStoreRef is not found");
        }

    }

    private void dealDatacenRefNotValid(ManagedObjectReference datacenterRef) {
        if (datacenterRef == null) {
            throw new ApplicationException("The specified datacenter is not found");
        }
    }

    private void buildCustomization(List<Network> ports, CustomizationSpec customization,
        ArrayList<CustomizationAdapterMapping> array) {
        for (Network port : ports) {
            array.add(addAdapter(port.getIpAddress(), port.getNetmask(), port.getGateway()));
        }

        if (array.size() > 0) {
            customization.getNicSettingMap().addAll(array);
        }
    }

    private CustomizationAdapterMapping addAdapter(String ipAddress, String netmask, String gateway) {
        CustomizationAdapterMapping map3 = new CustomizationAdapterMapping();
        CustomizationIPSettings adapter3 = new CustomizationIPSettings();
        CustomizationFixedIp ip3 = new CustomizationFixedIp();
        ip3.setIpAddress(ipAddress);
        adapter3.setIp(ip3);
        adapter3.setDnsDomain("localdomain");
        adapter3.setSubnetMask(netmask);
        adapter3.getGateway().add(gateway);
        map3.setAdapter(adapter3);
        return map3;
    }

    private TaskVmVo cluster(ServiceUtil svc, ManagedObjectReference datacenterRef, ManagedObjectReference vmFolderRef,
        ManagedObjectReference vmRef, String vmName, List<Network> ports, String password, VimPortType service,
        String vmwareId, String cloneVMOnly, ManagedObjectReference clusterSystem, VirtualMachineCloneSpec cloneSpec,
        int vncPort, String osType) {
        // 添加
        CustomizationSpec customization = new CustomizationSpec();
        ArrayList<CustomizationAdapterMapping> array = new ArrayList<CustomizationAdapterMapping>();
        buildCustomization(ports, customization, array);
        CustomizationPassword customizationPassword = new CustomizationPassword();
        customizationPassword.setPlainText(true);
        customizationPassword.setValue(password);
        CustomizationGuiUnattended guiUnattended = new CustomizationGuiUnattended();
        guiUnattended.setPassword(customizationPassword);
        guiUnattended.setAutoLogon(false);
        guiUnattended.setTimeZone(201);
        guiUnattended.setAutoLogonCount(0);

        // 系统主机名称有特殊会报错 故去掉特殊字符
        String osHostName = CommonUtil.filterSymbol(vmName);

        CustomizationFixedName fixedName = new CustomizationFixedName();
        // add by zhl 删除无用的ifelse分支 20170706
        fixedName.setName(osHostName);

        CustomizationUserData userdata = new CustomizationUserData();
        userdata.setComputerName(fixedName);
        userdata.setFullName(osHostName);
        userdata.setOrgName("LocalDomain");
        userdata.setProductId("");
        CustomizationPassword domainCustomizationPassword = new CustomizationPassword();
        domainCustomizationPassword.setPlainText(true);
        domainCustomizationPassword.setValue(password);
        CustomizationIdentification identification = new CustomizationIdentification();
        identification.setJoinWorkgroup("WORKGROUP");
        identification.setDomainAdminPassword(domainCustomizationPassword);
        buildCustomizationObject(osType, customization, guiUnattended, fixedName, userdata, identification);
        CustomizationGlobalIPSettings globalIpSettings = new CustomizationGlobalIPSettings();
        List<String> dnsSuffixLists = new ArrayList<>();
        List<String> dnsServerLists = new ArrayList<>();
        List<String> allDns = new ArrayList<>();
        buildAllDns(ports, allDns);
        iteratorAllDnsObject(dnsSuffixLists, dnsServerLists, allDns);
        globalIpSettings.getDnsSuffixList().addAll(dnsSuffixLists);
        globalIpSettings.getDnsServerList().addAll(dnsServerLists);
        customization.setGlobalIPSettings(globalIpSettings);
        cloneSpec.setCustomization(customization);
        LOGGER.info("Launching clone task to create a clone");
        TaskVmVo taskVmVo = new TaskVmVo();
        try {

            ManagedObjectReference cloneTask = service.cloneVMTask(vmRef, vmFolderRef, vmName, cloneSpec);
            taskVmVo.setTaskUri(cloneTask.getValue());
            TaskInfo task = (TaskInfo) svc.getDynamicProperty(cloneTask, "info");
            if (task != null) {
                taskVmVo.setStatus(task.getState().toString());
            }
            taskVmVo.setTemplateId(vmRef.getValue());
            taskVmVo.setOsType(osType);
            waitGetClusterResourceId(taskVmVo, vmName, clusterSystem, svc);
            LOGGER.info("cluster clone task success vmname is {}",vmName);
            return taskVmVo;
        } catch (CustomizationFaultFaultMsg | FileFaultFaultMsg | InsufficientResourcesFaultFaultMsg | InvalidDatastoreFaultMsg | InvalidStateFaultMsg | MigrationFaultFaultMsg | RuntimeFaultFaultMsg | TaskInProgressFaultMsg | VmConfigFaultFaultMsg e) {
            LOGGER.error("cluster clone task failed" +e.getMessage()+ "{}", e);
            VncPortTempStore.getInstance().removePortSet(vncPort);
            throw new ApplicationException("vmware clone fail : " + e.getMessage(), e);
        }
    }

    private void waitGetClusterResourceId(TaskVmVo taskVmVo, String vmName, ManagedObjectReference clusterSystem,
        ServiceUtil svc) {
        long timeOut = 10 * 1000; // 10秒超时
        long sleepTimeCount = 0; // 起始计时
        ManagedObjectReference newVmRef = null;
        // 虚拟机对象出现会延迟几秒 所以循环等待
        List<ManagedObjectReference> hostSystems = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterSystem,
            "host");
        for (ManagedObjectReference hostSystem : hostSystems) {
            sleepTimeCount = 0;
            while (newVmRef == null && sleepTimeCount <= timeOut) {
                waitOneSecond();
                newVmRef = svc.getDecendentMoRef(hostSystem, "VirtualMachine", StringUtils.replace(vmName, "/", "%2f"));
                sleepTimeCount += 1000;
            }
        }

        newVmRefHandle(taskVmVo, newVmRef);
    }

    private void newVmRefHandle(TaskVmVo taskVmVo, ManagedObjectReference newVmRef) {
        if (newVmRef == null) {
            throw new ApplicationException("The virtual machine is not found");
        } else {
            taskVmVo.setResourceId(newVmRef.getValue());
        }
    }

    private void waitOneSecond() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new ApplicationException("vmware InterruptedException fail ", e);
        }
    }

    private void iteratorAllDnsObject(List<String> dnsSuffixLists, List<String> dnsServerLists, List<String> allDns) {
        for (String allDn : allDns) {
            dnsServerLists.add(allDn);
            dnsSuffixLists.add("localdomain");
        }
    }

    private void buildAllDns(List<Network> ports, List<String> allDns) {
        for (Network port : ports) {
            if (port.getDns() == null) {
                continue;
            }
            for (String s : port.getDns()) {
                if (!allDns.contains(s)) {
                    allDns.add(s);
                }
            }
        }
    }

    private void buildCustomizationObject(String osType, CustomizationSpec customization,
        CustomizationGuiUnattended guiUnattended, CustomizationFixedName fixedName, CustomizationUserData userdata,
        CustomizationIdentification identification) {
        if ("WINDOWS".equalsIgnoreCase(osType)) {
            LOGGER.info("windows enter...");
            CustomizationSysprep identitySettings = new CustomizationSysprep();
            identitySettings.setGuiUnattended(guiUnattended);
            identitySettings.setUserData(userdata);
            identitySettings.setIdentification(identification);
            CustomizationLicenseFilePrintData licenseFilePrintData = new CustomizationLicenseFilePrintData();
            licenseFilePrintData.setAutoUsers(5);
            licenseFilePrintData.setAutoMode(CustomizationLicenseDataMode.PER_SERVER);
            identitySettings.setLicenseFilePrintData(licenseFilePrintData);
            customization.setIdentity(identitySettings);
        } else {
            CustomizationLinuxPrep identitySettings = new CustomizationLinuxPrep();
            identitySettings.setHostName(fixedName);
            identitySettings.setDomain("localdomain");
            customization.setIdentity(identitySettings);
        }
    }

    private String getOsType(ServiceUtil svc, ManagedObjectReference vmRef) {
        String osType = null;
        VirtualMachineConfigInfo templateConfig = (VirtualMachineConfigInfo) svc.getDynamicProperty(vmRef, "config");
        String fullSystemName = null;
        if (templateConfig != null) {
            fullSystemName = templateConfig.getGuestFullName();
        }
        LOGGER.info("fullSystemName... {}", fullSystemName);
        if (fullSystemName != null) {
            osType = getNames(fullSystemName);
        }
        return osType;
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

    private int getClusterVncPort(ServiceUtil svc, ManagedObjectReference clusterSystem) {
        List<ManagedObjectReference> hostSystemLst = (List<ManagedObjectReference>) svc.getDynamicProperty(
            clusterSystem, "host");
        int vncPort = 0;
        for (ManagedObjectReference hostSystem : hostSystemLst) {
            List<Integer> vncUsedPortGroup = new ArrayList<Integer>();
            vncPort = getVncPort(svc, hostSystem);
        }
        return vncPort;
    }

    private ManagedObjectReference setDataStoreRef(String datastoreId, ServiceUtil svc,
        ManagedObjectReference hostSystem, ManagedObjectReference dataStoreRef) {
        if (StringUtils.isNoneEmpty(datastoreId)) {
            dataStoreRef.setType("Datastore");
            dataStoreRef.setValue(datastoreId);
        } else {
            List<ManagedObjectReference> malist = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
                "datastore");
            if (malist != null && malist.size() > 0) {
                ManagedObjectReference[] dataStoreArr = malist.toArray(new ManagedObjectReference[0]);
                dataStoreRef = getMaxSizeDataStore(dataStoreArr, svc);
            }
        }
        return dataStoreRef;
    }

    /**
     * Gets max size data store.
     *
     * @param dataStoreArrs the data store arrs
     * @param svc the svc
     * @return the max size data store
     */
    public ManagedObjectReference getMaxSizeDataStore(ManagedObjectReference[] dataStoreArrs, ServiceUtil svc) {
        ManagedObjectReference dataStoreRef = null;
        if (dataStoreArrs != null && dataStoreArrs.length > 0) {
            for (int i = 0; i < dataStoreArrs.length; i++) {
                ManagedObjectReference currentDs = dataStoreArrs[i];
                if (i == 0) {
                    dataStoreRef = currentDs;
                    continue;
                }
                DatastoreSummary currentDsInfo = (DatastoreSummary) svc.getDynamicProperty(currentDs, "summary");
                DatastoreSummary lastDsInfo = (DatastoreSummary) svc.getDynamicProperty(dataStoreRef, "summary");
                if (currentDsInfo.getFreeSpace() > lastDsInfo.getFreeSpace()) {
                    dataStoreRef = currentDs;
                }
            }
        }
        return dataStoreRef;
    }

    private ManagedObjectReference getDatacenterRef(String datacenterName, VimPortType service,
        ServiceContent serviceContent) {
        ManagedObjectReference datacenterRef;
        try {
            datacenterRef = service.findByInventoryPath(serviceContent.getSearchIndex(), datacenterName);
        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
            throw new ApplicationException("vmware findByInventoryPath fail ", runtimeFaultFaultMsg);
        }
        if (datacenterRef == null) {
            throw new ApplicationException("The specified datacenter is not found");
        }
        return datacenterRef;
    }
}

