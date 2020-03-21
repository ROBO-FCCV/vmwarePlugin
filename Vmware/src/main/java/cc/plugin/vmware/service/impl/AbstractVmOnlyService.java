/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import com.alibaba.fastjson.JSON;
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
import cc.plugin.vmware.model.vo.request.vm.VmConfigInfo;
import cc.plugin.vmware.model.vo.response.vm.TaskVmVo;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.StringRandom;

import com.vmware.vim25.AlreadyExistsFaultMsg;
import com.vmware.vim25.ClusterComputeResourceSummary;
import com.vmware.vim25.Description;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.OutOfBoundsFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualCdrom;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceBackingInfo;
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
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualPCNet32;
import com.vmware.vim25.VirtualVmxnet3;
import com.vmware.vim25.VmConfigFaultFaultMsg;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-27
 */
public abstract class AbstractVmOnlyService extends AbstractVmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmOnlyServiceImpl.class);

    private static final int SCSI_CONTROLLER_MAX_DEVICE = 16;

    private static final int SCSI_CONTROLLER_RESERVE_SLOT = 7;

    private static final long MAX_CAPACITY = 1024L;

    @Autowired
    private VncConfig vncConfig;

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    @Override
    public TaskVmVo createVmOnly(String vmwareId, VmConfigInfo vmConfigInfo) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference datacenterRef =
            getDatacenterRef(vmConfigInfo.getDatacenterName(), service, serviceContent);
        ManagedObjectReference hostSystem = getManagedObjectResuorce(vmConfigInfo.getHostName(), datacenterRef, svc);
        // 默认取最大的datastore
        ManagedObjectReference dataStoreRef = svc.getDecendentMoRef(null, "Datastore", vmConfigInfo.getDatastoreName());
        if (dataStoreRef == null) {
            throw new CustomException(ErrorCode.SYSTEM_ERROR_CODE, "datastore is not Exit");
        }
        ManagedObjectReference vmFolderRef =
            (ManagedObjectReference) svc.getObjectProperty(serviceContent, service, datacenterRef, "vmFolder");
        String vmName = StringUtils.replace(vmConfigInfo.getVmName(), "%", "%25");
        // 添加vmpath 必须
        VirtualMachineConfigSpec createSpec = new VirtualMachineConfigSpec();
        String dataStoreName = addVmPath(svc, dataStoreRef, vmName, createSpec);
        // 添加cdrom
        adCdRom(vmConfigInfo, dataStoreRef, createSpec);
        // 添加网卡
        addNics(vmConfigInfo, createSpec);
        addDisks(vmConfigInfo, dataStoreRef, vmName, createSpec, dataStoreName);
        // 设置cpu数 如果传入的cpu核数大于主机的核数 默认使用主机最大的核数 不然会导致创建的虚拟机无法启动
        boolean isClusterFlag = isClusterResource(vmConfigInfo.getHostName());
        Memory memory = vmConfigInfo.getMemory();
        setHardWareInfo(isClusterFlag, vmConfigInfo.getCpu(), memory.getQuantityMb(), hostSystem, createSpec, svc);
        // VNC配置参数
        int vncPort = addVncConfig(svc, hostSystem, createSpec, isClusterFlag);
        TaskVmVo taskVmVo = new TaskVmVo();
        ManagedObjectReference resourcePool = getResourcePool(vmConfigInfo.getHostName(),hostSystem,svc);
        if (resourcePool == null) {
            throw new ApplicationException("The resourcePool is not found");
        }
        try {
            manageCreateTask(service, isClusterFlag, vmConfigInfo.getOsType(), svc,
                vmFolderRef, hostSystem, resourcePool, createSpec, vncPort, taskVmVo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            VncPortTempStore.getInstance().removePortSet(vncPort);
            throw new ApplicationException("vmware create fail : " + e.getMessage(), e);
        }

        return taskVmVo;
    }

    private String addVmPath(ServiceUtil svc, ManagedObjectReference dataStoreRef, String vmName,
        VirtualMachineConfigSpec createSpec) {
        createSpec.setName(vmName);
        VirtualMachineFileInfo virtualMachineFileInfo = new VirtualMachineFileInfo();
        String dataStoreName = (String) svc.getDynamicProperty(dataStoreRef, "name");
        // 格式 "[dataStoreName] vmName/vmName.vmx"
        StringBuffer vmPathSb = new StringBuffer();
        vmPathSb.append("[")
            .append(dataStoreName)
            .append("] ")
            .append(vmName)
            .append(File.separator)
            .append(vmName)
            .append(".vmx");
        virtualMachineFileInfo.setVmPathName(vmPathSb.toString());
        createSpec.setFiles(virtualMachineFileInfo);
        return dataStoreName;
    }

    private int addVncConfig(ServiceUtil svc, ManagedObjectReference hostSystem, VirtualMachineConfigSpec createSpec,
        boolean isClusterFlag) {
        OptionValue optionVncEnabled = new OptionValue();
        optionVncEnabled.setKey(Constants.VNC_ENABLED);
        optionVncEnabled.setValue(Boolean.TRUE.toString());
        createSpec.getExtraConfig().add(optionVncEnabled);
        OptionValue optionVncPassword = new OptionValue();
        optionVncPassword.setKey(Constants.VNC_PWD);
        optionVncPassword.setValue(StringRandom.getStringRandom(Constants.VNC_PASSWORD_LENGTH_EIGHT));
        createSpec.getExtraConfig().add(optionVncPassword);
        OptionValue optionVncPort = new OptionValue();
        optionVncPort.setKey(Constants.VNC_PORT);
        int vncPort = getVNCPortByResource(isClusterFlag, svc, hostSystem);
        optionVncPort.setValue(vncPort);
        createSpec.getExtraConfig().add(optionVncPort);
        return vncPort;
    }

    private void addDisks(VmConfigInfo vmConfigInfo, ManagedObjectReference dataStoreRef, String vmName,
        VirtualMachineConfigSpec createSpec, String dataStoreName) {
        List<Integer> usedUnitNumbers = new ArrayList<>();
        List<Disk> disks = vmConfigInfo.getDisks();
        for (int i = 0; i < disks.size(); i++) {
            // 添加硬盘 存储空间
            VirtualDeviceConfigSpec virtualDeviceConfigSpecDisk = new VirtualDeviceConfigSpec();
            virtualDeviceConfigSpecDisk.setOperation(VirtualDeviceConfigSpecOperation.ADD);
            virtualDeviceConfigSpecDisk.setFileOperation(VirtualDeviceConfigSpecFileOperation.CREATE);
            VirtualDiskFlatVer2BackingInfo virtualDiskFlatVer2BackingInfo = new VirtualDiskFlatVer2BackingInfo();
            virtualDiskFlatVer2BackingInfo.setDiskMode(VirtualDiskMode.PERSISTENT.value());
            // 格式 "[dataStoreName] vmName/vmName.vmdk"
            StringBuffer fileNameSb = new StringBuffer();
            fileNameSb.append("[")
                .append(dataStoreName)
                .append("] ")
                .append(vmName)
                .append(File.separator)
                .append(vmName + i)
                .append(".vmdk");
            virtualDiskFlatVer2BackingInfo.setFileName(fileNameSb.toString());
            VirtualDisk virtualDisk = new VirtualDisk();
            Disk disk = disks.get(i);
            virtualDisk.setCapacityInKB(MAX_CAPACITY * MAX_CAPACITY * disk.getQuantityGb());
            virtualDisk.setKey(Constants.VIRTUAL_DISK_KEY + i);
            virtualDisk.setControllerKey(Constants.PARA_VIRTUAL_SCSI_CONTROLLER_KEY);
            virtualDisk.setUnitNumber(Constants.DEFAULT_VIRTUAL_DISK_UNIT_NUMBER + i); // 必须填 不填报错
            virtualDisk.setBacking(virtualDiskFlatVer2BackingInfo);
            virtualDeviceConfigSpecDisk.setDevice(virtualDisk);
            createSpec.getDeviceChange().add(virtualDeviceConfigSpecDisk);

            usedUnitNumbers.add(virtualDisk.getUnitNumber());
        }

        // 创建RDM磁盘的配置
        List<Integer> ctlKeys =
            constructControllerKey(Constants.PARA_VIRTUAL_SCSI_CONTROLLER_KEY, getUsedUnitNumbers(usedUnitNumbers));
        createVirtualDiskMapConfigSpec(ctlKeys, vmConfigInfo.getLunNames(), createSpec, dataStoreName);
    }

    private void addNics(VmConfigInfo vmConfigInfo, VirtualMachineConfigSpec createSpec) {
        List<VirtualDeviceConfigSpec> deviceChangelist = getNicList(vmConfigInfo.getNics(), vmConfigInfo.getOsType());
        createSpec.getDeviceChange().addAll(deviceChangelist);

        // 添加硬盘需要先添加控制器
        VirtualDeviceConfigSpec virtualDeviceConfigSpecController = new VirtualDeviceConfigSpec();
        virtualDeviceConfigSpecController.setDevice(getVirtualDevice());
        virtualDeviceConfigSpecController.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        createSpec.getDeviceChange().add(virtualDeviceConfigSpecController);
    }

    private void adCdRom(VmConfigInfo vmConfigInfo, ManagedObjectReference dataStoreRef,
        VirtualMachineConfigSpec createSpec) {
        VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
        VirtualCdrom virtualCdrom = new VirtualCdrom();
        virtualCdrom.setBacking(getBackingInfo(dataStoreRef, vmConfigInfo.getHostName()));
        VirtualDeviceConnectInfo virtualDeviceConnectInfo = new VirtualDeviceConnectInfo();
        virtualDeviceConnectInfo.setAllowGuestControl(true);
        virtualDeviceConnectInfo.setConnected(false);
        virtualDeviceConnectInfo.setStartConnected(true);
        virtualDeviceConnectInfo.setStatus(Constants.STATUS_OK);
        virtualCdrom.setConnectable(virtualDeviceConnectInfo);
        virtualCdrom.setControllerKey(Constants.VIRTUAL_IDE_CONTROLLER_KEY);
        virtualCdrom.setKey(Constants.VIRTUAL_CDROM_KEY);
        virtualDeviceConfigSpec.setDevice(virtualCdrom);
        virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        createSpec.getDeviceChange().add(virtualDeviceConfigSpec);
        createSpec.setGuestId(vmConfigInfo.getOsFullName());
    }

    private ManagedObjectReference getResourcePool(String hostName,ManagedObjectReference resourceSystem,ServiceUtil svc){
        ManagedObjectReference resourcePool;
        if(isClusterResource(hostName)){
            resourcePool = svc.getMoRefProp(resourceSystem, "resourcePool");
        }else{
            ManagedObjectReference parentResource = svc.getMoRefProp(resourceSystem, "parent");
            resourcePool = svc.getMoRefProp(parentResource, "resourcePool");
        }
        return resourcePool;
    }

    private boolean isClusterResource(String str) {
        return str.contains("domain");
    }

    private void setHardWareInfo(boolean isClusterResource, Cpu cpu, long ramSize, ManagedObjectReference resource,
        VirtualMachineConfigSpec createSpec, ServiceUtil svc) {
        if (isClusterResource) {
            ClusterComputeResourceSummary clusterComputeResourceSummary =
                (ClusterComputeResourceSummary) svc.getDynamicProperty(resource, "summary");
            buildclusterConfigObject(cpu, ramSize, createSpec, clusterComputeResourceSummary);
        } else {

            HostHardwareInfo hostHardwareInfo = (HostHardwareInfo) svc.getDynamicProperty(resource, "hardware");
            constructCreateSpec(cpu, createSpec, hostHardwareInfo);
            long ramB = ramSize * 1024;
            long memorySize = hostHardwareInfo == null ? 0 : hostHardwareInfo.getMemorySize();
            buildRamToCreateSpec(ramSize, createSpec, ramB, memorySize);
        }
    }

    /**
     * Buildcluster config object.
     *
     * @param cpu the cpu
     * @param ramSize the ram size
     * @param config the config
     * @param clusterComputeResourceSummary the cluster compute resource summary
     */
    public void buildclusterConfigObject(Cpu cpu, long ramSize, VirtualMachineConfigSpec config,
        ClusterComputeResourceSummary clusterComputeResourceSummary) {
        int cpuNum = cpu.getQuantity();
        if (clusterComputeResourceSummary != null && cpuNum > clusterComputeResourceSummary.getNumCpuCores()) {
            LOGGER.info("setting cpuNum bigger than host cpuNum: {}", clusterComputeResourceSummary.getNumCpuCores());
            cpuNum = clusterComputeResourceSummary.getNumCpuCores();
        }
        config.setNumCPUs(cpuNum);
        config.setNumCoresPerSocket(1);

        String cpuCores = cpu.getCpuCores();
        if (StringUtils.isNotEmpty(cpuCores)) {
            config.setNumCoresPerSocket(Integer.parseInt(cpuCores));
        }

        // 如果传入的内存大于主机的内存 默认使用主机的最大内存 不然会导致创建的虚拟机无法启动 RAMsize单位为MB
        config.setMemoryMB(ramSize);
    }

    private ManagedObjectReference getManagedObjectResuorce(String hostName, ManagedObjectReference datacenterRef,
        ServiceUtil svc) {
        ManagedObjectReference resource;
        if (isClusterResource(hostName)) {
            resource = new ManagedObjectReference();
            resource.setType("ClusterComputeResource");
            resource.setValue(hostName);
        } else {
            ManagedObjectReference hostSystem = svc.getDecendentMoRef(datacenterRef, "HostSystem", hostName);
            resource = hostSystem;
        }
        return resource;
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

    private void buildResultObject(VimPortType service, TaskInfo task, String osType, ServiceUtil svc, TaskVmVo taskVmVo)
        throws FileFaultFaultMsg, InsufficientResourcesFaultFaultMsg, InvalidStateFaultMsg, RuntimeFaultFaultMsg,
        TaskInProgressFaultMsg, VmConfigFaultFaultMsg {
        ManagedObjectReference vmInfo = (ManagedObjectReference) task.getResult();
        String vmId = vmInfo.getValue();
        ManagedObjectReference powerOnTask;
        // 获取当前vm的关联对象
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);

        // 开始启动电源任务
        powerOnTask = service.powerOnVMTask(vmRef, null);
        TaskInfo powerOnTaskinfo = (TaskInfo) svc.getDynamicProperty(powerOnTask, "info");
        if (powerOnTaskinfo != null) {
            String powerOnStatus = powerOnTaskinfo.getState().toString();
            taskVmVo.setTaskId(powerOnTask.getValue());
            taskVmVo.setStatus(powerOnStatus);
            taskVmVo.setOsType(osType);
        }
        taskVmVo.setTemplateId(vmRef.getValue());
        taskVmVo.setResourceId(vmRef.getValue());
    }

    private void manageCreateTask(VimPortType service, boolean isClusterResource, String osType, ServiceUtil svc,
        ManagedObjectReference vmFolderRef, ManagedObjectReference resourceSystem, ManagedObjectReference resourcePool,
        VirtualMachineConfigSpec createSpec, int vncPort, TaskVmVo taskVmVo) {
        ManagedObjectReference createTask;
        try {
            if (isClusterResource) {
                // 开始创建vm任务
                createTask = service.createVMTask(vmFolderRef, createSpec, resourcePool, null);
            } else {
                createTask = service.createVMTask(vmFolderRef, createSpec, resourcePool, resourceSystem);
            }
            TaskInfo task = (TaskInfo) svc.getDynamicProperty(createTask, "info");
            String createStatus = task == null ? "" : task.getState().toString();

            // 等待创建vm任务
            while (StringUtils.equalsIgnoreCase(TaskInfoState.RUNNING.value(), createStatus)
                || StringUtils.equalsIgnoreCase(TaskInfoState.QUEUED.value(), createStatus)) {
                task = (TaskInfo) svc.getDynamicProperty(createTask, "info");
                createStatus = task == null ? "" : task.getState().toString();
            }
            // 如果创建成功 则启动电源2
            if (StringUtils.equalsIgnoreCase(TaskInfoState.SUCCESS.value(), createStatus)) {
                buildResultObject(service, task, osType, svc, taskVmVo);
            } else {
                VncPortTempStore.getInstance().removePortSet(vncPort);
                LOGGER.error("create vm task failed task is:{}", JSON.toJSONString(task));
                throw new ApplicationException("vmware create error ! ");
            }
        } catch (AlreadyExistsFaultMsg | DuplicateNameFaultMsg | FileFaultFaultMsg | InsufficientResourcesFaultFaultMsg
            | InvalidDatastoreFaultMsg | InvalidNameFaultMsg | InvalidStateFaultMsg | OutOfBoundsFaultMsg
            | RuntimeFaultFaultMsg | VmConfigFaultFaultMsg | TaskInProgressFaultMsg e) {
            LOGGER.error(e.getMessage(), e);
            VncPortTempStore.getInstance().removePortSet(vncPort);
            throw new ApplicationException("vmware create fail : " + e.getMessage(), e);
        }
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

    private void constructCreateSpec(Cpu cpu, VirtualMachineConfigSpec createSpec,
        HostHardwareInfo hostHardwareInfo) {
        int cpuNum = cpu.getQuantity();
        if (hostHardwareInfo != null && cpuNum > hostHardwareInfo.getCpuInfo().getNumCpuCores()) {
            LOGGER.info("setting cpuNum bigger than host cpuNum: {}", hostHardwareInfo.getCpuInfo().getNumCpuCores());
            cpuNum = hostHardwareInfo.getCpuInfo().getNumCpuCores();
        }
        createSpec.setNumCPUs(cpuNum);
        createSpec.setNumCoresPerSocket(Constants.DEFAULT_NUM_CORES_PER_SOCKET);
        String cpuCores = cpu.getCpuCores();
        if (StringUtils.isNotEmpty(cpuCores)) {
            createSpec.setNumCoresPerSocket(Integer.parseInt(cpuCores));
        }
    }

    /**
     * 创建RDM配置
     *
     * @param ctlKeys 可用的控制器key,以及unit number
     * @param lunNames 挂载的lun
     * @param config 配置
     * @param dataStoreName the data store name
     */
    public void createVirtualDiskMapConfigSpec(List<Integer> ctlKeys, List<String> lunNames, VirtualMachineConfigSpec config, String dataStoreName) {
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
     * Gets controller key.
     *
     * @param index the index
     * @param ctlKeys the ctl keys
     * @return the controller key
     */
    public int getControllerKey(int index, List<Integer> ctlKeys) {
        if (index >= ctlKeys.size() / 2) {
            LOGGER.info("out of ctlKeys length.");
            return -1;
        }
        return ctlKeys.get(2 * index);
    }

    /**
     * Gets unit number.
     *
     * @param index the index
     * @param ctlKeys the ctl keys
     * @return the unit number
     */
    public int getUnitNumber(int index, List<Integer> ctlKeys) {
        if (index >= ctlKeys.size() / 2) {
            LOGGER.info("out of ctlKeys length.");
            return -1;
        }
        return ctlKeys.get((2 * index) + 1);
    }

    /**
     * Create virtual disk config spec virtual device config spec.
     *
     * @param controllerkey the controllerkey
     * @param unitNumber the unit number
     * @param deviceName the device name
     * @param dataStoreName the data store name
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

    private List<Integer> constructControllerKey(int controllerKey, List<Integer> usedUnitNumbers) {
        List<Integer> result = new ArrayList<>();
        for (int index = 0; index < SCSI_CONTROLLER_MAX_DEVICE; index++) {
            if (usedUnitNumbers.contains(index) || SCSI_CONTROLLER_RESERVE_SLOT == index) {
                continue;
            }

            result.add(controllerKey);
            result.add(index);
        }
        return result;
    }

    private int getVNCPortByResource(boolean isClusterResource, ServiceUtil svc, ManagedObjectReference resource) {
        int vncPort;
        if (isClusterResource) {
            vncPort = getClusterVncPort(svc, resource);
        } else {
            vncPort = getVncPort(svc, resource);
        }
        return vncPort;
    }

    private int getClusterVncPort(ServiceUtil svc, ManagedObjectReference clusterSystem) {

        List<ManagedObjectReference> hostSystemLst =
            (List<ManagedObjectReference>) svc.getDynamicProperty(clusterSystem, "host");
        int vncPort = 0;
        for (ManagedObjectReference hostSystem : hostSystemLst) {
            List<Integer> vncUsedPortGroup = new ArrayList<Integer>();
            List<ManagedObjectReference> vmList =
                (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem, "vm");
            if (vmList != null) {
                iteratorVmList(vmList, vncUsedPortGroup, svc);
            }
            Collections.sort(vncUsedPortGroup);

            // 默认 5901 到 6150 为vnc可用端口 详见yml文件
            int portMin = Integer.parseInt(vncConfig.getPortMin()) + 1;
            int portMax = Integer.parseInt(vncConfig.getPortMax());
            vncPort = getVncPortForCluster(vncPort, vncUsedPortGroup, portMin, portMax);
            // 并行创建虚拟机时 端口没有存到vmware时会重复 故使用临时存储存入端口 判断是否已使用
            VncPortTempStore.getInstance().putPortSet(vncPort);

        }
        return vncPort;
    }

    private int getVncPortForCluster(int vncPort, List<Integer> vncUsedPortGroup, int portMin, int portMax) {
        for (int in = portMin; in <= portMax; in++) {
            if (vncUsedPortGroup.indexOf(in) == -1) {
                if (!VncPortTempStore.getInstance().getPortSet().contains(in)) {
                    vncPort = in;
                    break;
                }
            }
        }
        return vncPort;
    }

    /**
     * 获取可用vnc端口
     *
     * @param svc the svc
     * @param hostSystem the host system
     * @return vnc port
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
        for (int in = portMin; in <= portMax; in++) {
            if (vncUsedPortGroup.indexOf(in) == -1) {
                if (!VncPortTempStore.getInstance().getPortSet().contains(in)) {
                    vncPort = in;
                    break;
                }
            }
        }
        // 并行创建虚拟机时 端口没有存到vmware时会重复 故使用临时存储存入端口 判断是否已使用
        VncPortTempStore.getInstance().putPortSet(vncPort);
        return vncPort;
    }

    /**
     * Iterator vm list.
     *
     * @param vmList the vm list
     * @param vncUsedPortGroup the vnc used port group
     * @param svc the svc
     */
    public void iteratorVmList(List<ManagedObjectReference> vmList, List<Integer> vncUsedPortGroup, ServiceUtil svc) {
        for (ManagedObjectReference vm : vmList) {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            VirtualMachineConfigInfo vconfig;
            if (vmConfigInfo == null) {
                continue;
            } else {
                vconfig = (VirtualMachineConfigInfo) vmConfigInfo;
            }
            for (OptionValue option : vconfig.getExtraConfig()) {
                if (Constants.VNC_PORT.equals(option.getKey())) {
                    vncUsedPortGroup.add(Integer.valueOf(option.getValue().toString()));
                }
            }
        }
    }

    /**
     * 创建VirtualDeviceConfigSpec有三种条件， 首先根据newwork类型判断分两种情况创建实体 其次newwork类型是dvs的根据系统类型创建实体
     *
     * @param networks network
     * @param osType 系统类型
     * @return VirtualDeviceSpec数据对象类型封装了单个虚拟设备的更改规范 。 必须完全指定要添加或修改的虚拟设备。
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

        LOGGER.debug(" network macAddress is {}", macAddress);
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
        PortGroupUrn portGroupUrn = newwork.getPortGroupUrn();
        VirtualDeviceConfigSpec virtualDeviceConfigSpec = new VirtualDeviceConfigSpec();
        virtualDeviceConfigSpec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        VirtualEthernetCard virtualEthernetCard;
        if ("WINDOWS".equalsIgnoreCase(osType)) {
            virtualEthernetCard = new VirtualE1000();
        } else {
            virtualEthernetCard = new VirtualPCNet32();
        }

        VirtualEthernetCardDistributedVirtualPortBackingInfo nicBacking =
            new VirtualEthernetCardDistributedVirtualPortBackingInfo();
        DistributedVirtualSwitchPortConnection portConn = new DistributedVirtualSwitchPortConnection();
        portConn.setPortgroupKey(portGroupUrn.getPortgroupKey());
        portConn.setSwitchUuid(newwork.getSwitchUuid());
        nicBacking.setPort(portConn);
        virtualEthernetCard.setBacking(nicBacking);

        LOGGER.debug(" network macAddress is {}", newwork.getMacAddress());
        if (newwork.getMacAddress() == null) {
            virtualEthernetCard.setAddressType("Generated");
        } else { // 手动设置macaddress
            virtualEthernetCard.setAddressType("Manual");
            virtualEthernetCard.setMacAddress(newwork.getMacAddress());
        }

        // 数据来源于网卡创建时sitePortId
        Description desc = new Description();
        desc.setLabel(portGroupUrn.getPortgroupKey());
        desc.setSummary(portGroupUrn.getPortgroupKey());
        virtualEthernetCard.setDeviceInfo(desc);
        virtualDeviceConfigSpec.setDevice(virtualEthernetCard);
        return virtualDeviceConfigSpec;
    }

    /**
     * Gets backing info.
     *
     * @param dataStoreRef the data store ref
     * @param hostName the host name
     * @return the backing info
     */
    protected abstract VirtualDeviceBackingInfo getBackingInfo(ManagedObjectReference dataStoreRef, String hostName);

    /**
     * Gets virtual device.
     *
     * @return the virtual device
     */
    protected abstract VirtualDevice getVirtualDevice();

    /**
     * Gets used unit numbers.
     *
     * @param usedUnitNumbers the used unit numbers
     * @return the used unit numbers
     */
    protected abstract List<Integer> getUsedUnitNumbers(List<Integer> usedUnitNumbers);

}
