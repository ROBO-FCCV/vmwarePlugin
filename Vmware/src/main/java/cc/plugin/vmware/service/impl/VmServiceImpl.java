/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.Constants;
import cc.plugin.vmware.constant.DesConfig;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.host.HostRequest;
import cc.plugin.vmware.model.vo.request.vm.RenameVmRequest;
import cc.plugin.vmware.model.vo.request.vm.SnapshotRequest;
import cc.plugin.vmware.model.vo.response.vm.Disk;
import cc.plugin.vmware.model.vo.response.vm.LinuxSystemType;
import cc.plugin.vmware.model.vo.response.vm.Net;
import cc.plugin.vmware.model.vo.response.vm.Network;
import cc.plugin.vmware.model.vo.response.vm.OtherSystemType;
import cc.plugin.vmware.model.vo.response.vm.VMVo;
import cc.plugin.vmware.model.vo.response.vm.VcenterType;
import cc.plugin.vmware.model.vo.response.vm.VmByHostIpRes;
import cc.plugin.vmware.model.vo.response.vm.VmInfo;
import cc.plugin.vmware.model.vo.response.vm.VmOverStatus;
import cc.plugin.vmware.model.vo.response.vm.VmResponseVo;
import cc.plugin.vmware.model.vo.response.vm.VmStatus;
import cc.plugin.vmware.model.vo.response.vm.VmStatusVo;
import cc.plugin.vmware.model.vo.response.vm.VncVo;
import cc.plugin.vmware.model.vo.response.vm.WindowsSystemType;
import cc.plugin.vmware.service.VmService;
import cc.plugin.vmware.util.Cipher;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.Des;
import cc.plugin.vmware.util.Version;

import com.alibaba.fastjson.JSONObject;
import com.vmware.vim25.AboutInfo;
import com.vmware.vim25.Description;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.GuestNicInfo;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.SnapshotFaultFaultMsg;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimFaultFaultMsg;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConnectInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualE1000E;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineGuestSummary;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.VirtualMachineTicket;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.VirtualVmxnet3;
import com.vmware.vim25.VmConfigFaultFaultMsg;
import com.vmware.vim25.VmToolsUpgradeFaultFaultMsg;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.soap.SOAPFaultException;

/**
 * 功能描述
 *
 * @since 2019 -09-27
 */
@Service
public class VmServiceImpl implements VmService {
    private static final Logger logger = LoggerFactory.getLogger(VmServiceImpl.class);
    /**
     * 使用HTML console SDK的VMware分水岭版本
     */
    private static final String HTML_CONSOLE_VERSION = "7.0";

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    /**
     * The Cipher.
     */
    @Autowired
    Cipher cipher;

    @Autowired
    private DesConfig desConfig;

    @Override
    public VmStatus powerOn(String vmwareId, String vmName)
        throws CustomException, RuntimeFaultFaultMsg, InsufficientResourcesFaultFaultMsg, FileFaultFaultMsg,
        VmConfigFaultFaultMsg, TaskInProgressFaultMsg, InvalidStateFaultMsg {
        VmStatus vmStatus = new VmStatus();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ManagedObjectReference powerOnTask;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = setvmRef(vmName, svc);
        if (vmRef == null) {
            throw new ApplicationException("The virtual" + vmName + " is not found");
        }
        if (service == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        powerOnTask = service.powerOnVMTask(vmRef, null);
        TaskInfo powerOnTaskinfo = (TaskInfo) svc.getDynamicProperty(powerOnTask, "info");
        String powerOnStatus = null == powerOnTaskinfo ? "" : powerOnTaskinfo.getState().toString();
        vmStatus.setTaskId(powerOnTask.getValue());
        vmStatus.setStatus(powerOnStatus);
        return vmStatus;
    }

    @Override
    public VmStatus powerOff(String vmwareId, String vmName)
        throws CustomException, TaskInProgressFaultMsg, InvalidStateFaultMsg, RuntimeFaultFaultMsg {
        VmStatus vmStatus = new VmStatus();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ManagedObjectReference powerOffTask;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = setvmRef(vmName, svc);
        if (vmRef == null) {
            throw new ApplicationException("The virtual" + vmName + " is not found");
        }

        if (null == service) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        powerOffTask = service.powerOffVMTask(vmRef);
        TaskInfo powerOnTaskinfo = (TaskInfo) svc.getDynamicProperty(powerOffTask, "info");
        String powerOffStatus = null == powerOnTaskinfo ? "" : powerOnTaskinfo.getState().toString();
        vmStatus.setTaskId(powerOffTask.getValue());
        vmStatus.setStatus(powerOffStatus);
        return vmStatus;
    }

    @Override
    public String pownOnVm(String vmwareId, String vmId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        return setvmwarePowerOn(service, vmRef);
    }

    private String setvmwarePowerOn(VimPortType service, ManagedObjectReference vmRef) throws ApplicationException {
        ManagedObjectReference powerOnTask;
        if (service != null) {
            try {
                powerOnTask = service.powerOnVMTask(vmRef, null);

                return JSONObject.toJSONString(powerOnTask);
            } catch (FileFaultFaultMsg | InsufficientResourcesFaultFaultMsg | VmConfigFaultFaultMsg | InvalidStateFaultMsg | RuntimeFaultFaultMsg | TaskInProgressFaultMsg ex) {
                logger.info("setvmwarePowerOn exception:{}", ex);
                throw new ApplicationException(ErrorCode.SYSTEM_ERROR_CODE, ex.getMessage());
            }
        }
        return " ";
    }

    @Override
    public String pownOffVm(String vmwareId, String vmId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new ApplicationException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        return setPowerOff(service, vmRef);
    }

    private String setPowerOff(VimPortType service, ManagedObjectReference vmRef) throws ApplicationException {
        ManagedObjectReference powerOnTask;
        if (service != null) {
            try {
                powerOnTask = service.powerOffVMTask(vmRef);

                return JSONObject.toJSONString(powerOnTask);
            } catch (InvalidStateFaultMsg | RuntimeFaultFaultMsg | TaskInProgressFaultMsg exc) {
                logger.info("setvmwarePowerOn exception:{}", exc);
                throw new ApplicationException(exc.getMessage(), exc);
            }
        }
        return " ";
    }

    @Override
    public VmByHostIpRes queryVmListByHost(String vmwareId, HostRequest hostRequest) throws CustomException {
        VmByHostIpRes vmByHostIpRes = new VmByHostIpRes();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();

        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceUtil svc = ecb.getServiceUtil();
        List<VmResponseVo> vmResponseVos = new ArrayList<>();
        for (Object ip : hostRequest.getHostNames()) {
            String hostName = (String) ip;
            ManagedObjectReference hostSystem = svc.getDecendentMoRef(null, "HostSystem", hostName);
            if (hostSystem == null) {
                logger.error("getVmByHost get host: {} not found!!", hostName);
                continue;
            }

            List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
                "vm");
            if (vmList == null) {
                return vmByHostIpRes;
            }
            setVmResponseList(svc, vmResponseVos, vmList);
            vmByHostIpRes.setVms(vmResponseVos);
        }
        return vmByHostIpRes;
    }

    private void setVmResponseList(ServiceUtil svc, List<VmResponseVo> vmResponseVos,
        List<ManagedObjectReference> vmList) {
        for (ManagedObjectReference vmMof : vmList) {
            VmResponseVo vmResponseVo = new VmResponseVo();
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vmMof);
            VirtualMachineConfigInfo config;
            if (vmConfigInfo == null) {
                continue;
            } else {
                config = (VirtualMachineConfigInfo) vmConfigInfo;
            }
            // 过滤掉模板
            if (config.isTemplate()) {
                continue;
            }
            String vmName = (String) svc.getDynamicProperty(vmMof, "name");
            vmResponseVo.setVmName(StringUtils.replace(vmName, "%25", "%"));
            String osFullName = config.getGuestFullName();
            vmResponseVo.setOsName(getOSNameByFullName(osFullName));
            String uuid = config.getInstanceUuid();
            vmResponseVo.setVmId(vmMof.getValue());
            vmResponseVo.setUuid(uuid);
            vmResponseVos.add(vmResponseVo);
        }
    }

    private String getOSNameByFullName(String fullSystemName) {
        String name = null;
        if (fullSystemName != null) {
            name = getNames(fullSystemName);
        }
        return name;
    }

    @Override
    public String deleteVm(String vmwareId, String vmId, String vmName)
        throws CustomException, InvalidStateFaultMsg, InterruptedException, TaskInProgressFaultMsg,
        RuntimeFaultFaultMsg, VimFaultFaultMsg {
        String message = "";
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        ManagedObjectReference rootFolder = serviceContent.getRootFolder();
        ServiceUtil svc = ecb.getServiceUtil();
        VimPortType service = serviceConnection.getVimPort();
        List<ManagedObjectReference> vmList = svc.getDecendentMoRefs(rootFolder, "VirtualMachine");
        if (isExistVm(vmId, vmList, vmName)) {
            message = setResult(service, vmId, vmName, svc);
        } else {
            message = "The virtual machine has been deleted";
        }
        return message;
    }

    private String setResult(VimPortType service, String vmId, String vmName, ServiceUtil svc)
        throws RuntimeFaultFaultMsg, VimFaultFaultMsg, InvalidStateFaultMsg, TaskInProgressFaultMsg,
        InterruptedException {
        ManagedObjectReference vmReference = new ManagedObjectReference();
        ManagedObjectReference taskReference = null;

        if (vmName != null) {
            if (vmName.contains("ROBOTempTransferMachine_")) {
                vmReference = svc.getDecendentMoRef(null, "VirtualMachine", vmName);
            } else {
                vmReference.setType("VirtualMachine");
                vmReference.setValue(vmId);
            }
        } else {
            vmReference.setType("VirtualMachine");
            vmReference.setValue(vmId);
        }
        // 获取虚拟机状态
        VirtualMachineRuntimeInfo runvmri = (VirtualMachineRuntimeInfo) svc.getDynamicProperty(vmReference, "runtime");
        if (runvmri != null) {
            String powerState = runvmri.getPowerState().value(); // poweredOn
            if ("poweredOff".equals(powerState)) {
                taskReference = service.destroyTask(vmReference);
            } else {
                ManagedObjectReference powerOffTask = service.powerOffVMTask(vmReference);
                TaskInfo powerOffTaskinfo = (TaskInfo) svc.getDynamicProperty(powerOffTask, "info");
                String powerOffStatus = powerOffTaskinfo.getState().toString();
                TimeUnit.SECONDS.sleep(1);
                if (powerOffStatus.equals("SUCCESS")) {
                    taskReference = service.destroyTask(vmReference);
                } else {
                    TimeUnit.SECONDS.sleep(3);
                    taskReference = service.destroyTask(vmReference);
                }
            }
        }
        return JSONObject.toJSONString(taskReference);
    }

    /**
     * 判断虚拟机是否在虚拟化平台存在
     */
    private boolean isExistVm(String vmId, List<ManagedObjectReference> vmList, String vmName) {
        logger.info("isExistVm enter...");
        boolean flag = false;
        if (vmName != null) {
            return true;
        }
        if (null != vmList && !vmList.isEmpty()) {
            for (ManagedObjectReference managerObj : vmList) {
                if (managerObj.getValue().equals(vmId)) {
                    flag = true;
                    break;
                }
            }
        }
        logger.info("isExistVm leave...flag: {}", flag);
        return flag;
    }

    @Override
    public String resetVm(String vmwareId, String vmId)
        throws CustomException, TaskInProgressFaultMsg, InvalidStateFaultMsg, RuntimeFaultFaultMsg {
        String message = "";
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        ManagedObjectReference rootFolder = serviceContent.getRootFolder();
        ServiceUtil svc = ecb.getServiceUtil();
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vm = new ManagedObjectReference();
        vm.setType("VirtualMachine");
        vm.setValue(vmId);
        ManagedObjectReference task = service.resetVMTask(vm);

        message = svc.waitForTask(task);
        return message;
    }

    @Override
    public String reNameVm(String vmwareId, RenameVmRequest renameVmRequest)
        throws CustomException, RuntimeFaultFaultMsg {
        String message = "";
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceUtil svc = ecb.getServiceUtil();
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = null;
        vmRef = svc.getDecendentMoRef(null, "VirtualMachine", renameVmRequest.getVmName());
        if (vmRef == null) {
            message = String.format(Locale.ENGLISH, "VirtualMachine %s not find", renameVmRequest.getVmName());
        }
        setreNameVm(service, vmRef, renameVmRequest.getNewName(), svc);
        return message;
    }

    private String setreNameVm(VimPortType service, ManagedObjectReference vmRef, String vmNewName, ServiceUtil svc)
        throws RuntimeFaultFaultMsg {
        ManagedObjectReference reNameTask;
        if (service != null) {
            try {
                reNameTask = service.renameTask(vmRef, vmNewName);

                return JSONObject.toJSONString(reNameTask);
            } catch (DuplicateNameFaultMsg e) {
                logger.info("rename error...", e);
                throw new ApplicationException(ErrorCode.SYSTEM_ERROR_CODE, e.getMessage());
            } catch (InvalidNameFaultMsg e) {
                logger.info("rename error... ", e);
                throw new ApplicationException(ErrorCode.SYSTEM_ERROR_CODE, e.getMessage());
            }
        }
        return "";
    }

    @Override
    public String getVmIdByName(String vmwareId, String vmName) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference vmRef = svc.getDecendentMoRef(null, "VirtualMachine", vmName);
        if (vmRef != null) {
            if (null != vmRef.getValue()) {
                return vmRef.getValue();
            }
        }
        return "";
    }

    @Override
    public VmStatusVo getVmStatusByVmId(String vmwareId, String vmId) throws CustomException {
        VmStatusVo vmStatusVo = new VmStatusVo();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }

        GuestInfo guestinfo = (GuestInfo) svc.getDynamicProperty(vmRef, "guest");
        logger.info("vmId...end");
        VirtualMachineSummary vmSummary = (VirtualMachineSummary) svc.getDynamicProperty(vmRef, "summary");
        VirtualMachineRuntimeInfo runtimeInfo = vmSummary.getRuntime();
        String powerStatus = runtimeInfo.getPowerState().value();
        vmStatusVo.setStatus(StringUtils.equalsIgnoreCase(powerStatus, "poweredOn") ? "running" : "stopped");
        if (guestinfo != null) {
            vmStatusVo.setIp(guestinfo.getIpAddress());
        }

        VirtualMachineConfigInfo config = (VirtualMachineConfigInfo) svc.getDynamicProperty(vmRef, "config");
        String vmName = (String) svc.getDynamicProperty(vmRef, "name");
        vmStatusVo.setVmName(vmName);
        String port = null;
        String pwd = null;
        if (config != null) {
            for (OptionValue optionValue : config.getExtraConfig()) {
                port = getPort(port, optionValue);
                pwd = getPwd(pwd, optionValue);
            }
        }
        vmStatusVo.setVncPort(port);
        vmStatusVo.setVncHelso(pwd);
        String encodePassword = new Des().strEnc(pwd, desConfig.getKey1(), desConfig.getKey2(), desConfig.getKey3());

        vmStatusVo.setVncEncHelso(encodePassword);
        String osType = getOsType(svc, vmRef);
        vmStatusVo.setOsType(osType);
        List<ManagedObjectReference> managedList = (List<ManagedObjectReference>) svc.getDynamicProperty(vmRef,
            "network");
        malistNotEmptycheck(vmStatusVo, svc, managedList, vmRef, vmId);
        return vmStatusVo;
    }

    private void malistNotEmptycheck(VmStatusVo vmStatusVo, ServiceUtil svc, List<ManagedObjectReference> managedList,
        ManagedObjectReference vmRef, String vmId) {
        if (managedList != null && managedList.size() > 0) {
            ManagedObjectReference network = managedList.get(0);
            List<ManagedObjectReference> hostList = (List<ManagedObjectReference>) svc.getDynamicProperty(network,
                "host");

            setFlag(vmStatusVo, svc, vmId, hostList);
        }
    }

    private void setFlag(VmStatusVo vmStatusVo, ServiceUtil svc, String vmId, List<ManagedObjectReference> hostList) {
        if (hostList != null && hostList.size() > 0) {
            for (ManagedObjectReference hostSystem : hostList) {
                List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
                    "vm");
                boolean flag = false;
                flag = getFlag(vmStatusVo, svc, vmId, hostSystem, vmList, flag);
                if (flag) {
                    break;
                }
            }
        }
    }

    private boolean getFlag(VmStatusVo vmStatusVo, ServiceUtil svc, String vmId, ManagedObjectReference hostSystem,
        List<ManagedObjectReference> vmList, boolean flag) {
        for (ManagedObjectReference virtualMachine : vmList) {
            String vmwareId = virtualMachine.getValue();
            if (vmwareId.equals(vmId)) {
                String hostName = (String) svc.getDynamicProperty(hostSystem, "name");
                vmStatusVo.setVncHost(hostName);
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 获取操作系统类型 <功能详细描述>
     * <p>
     *
     * @see [类、类#方法、类#成员]
     */
    private String getOsType(ServiceUtil svc, ManagedObjectReference vmRef) {
        String osType = null;
        VirtualMachineConfigInfo templateConfig = (VirtualMachineConfigInfo) svc.getDynamicProperty(vmRef, "config");
        String fullSystemName = null;
        if (templateConfig != null) {
            fullSystemName = templateConfig.getGuestFullName();
        }
        logger.info("fullSystemName... {}", fullSystemName);
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

    private String getPwd(String pwd, OptionValue optionValue) {
        if (Constants.VNC_PWD.equals(optionValue.getKey())) {
            pwd = (String) optionValue.getValue();
        }
        return pwd;
    }

    private Boolean getVncEnabled(boolean vncEnabled, OptionValue optionValue) {
        if (Constants.VNC_ENABLED.equals(optionValue.getKey())) {
            vncEnabled = Boolean.parseBoolean(String.valueOf(optionValue.getValue()));
        }
        return vncEnabled;
    }

    private String getPort(String port, OptionValue optionValue) {
        if (Constants.VNC_PORT.equals(optionValue.getKey())) {
            port = (String) optionValue.getValue();
        }
        return port;
    }

    @Override
    public VmInfo queryVmDetailByVmId(String vmwareId, String vmId) throws CustomException {
        VmInfo vmVo = new VmInfo();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }

        VirtualMachineConfigInfo vmconfig = (VirtualMachineConfigInfo) svc.getDynamicProperty(vmRef, "config");
        VirtualHardware virtualHardware = vmconfig.getHardware();
        int memoryMB = virtualHardware.getMemoryMB();
        vmVo.setMemoryMB(memoryMB);
        vmVo.setVmId(vmId);
        vmVo.setMoId(vmId);
        vmVo.setOsName(getOSNameByFullName(vmconfig.getGuestFullName()));
        String vmName = (String) svc.getDynamicProperty(vmRef, "name");
        vmVo.setVmName(StringUtils.replace(vmName, "%25", "%"));
        int numsocket = virtualHardware.getNumCoresPerSocket();
        vmVo.setNumSocket(numsocket);
        int numCpu = virtualHardware.getNumCPU();
        vmVo.setNumCpu(numCpu);
        List<VirtualDevice> virtualDeviceLst = virtualHardware.getDevice();
        List<Disk> disks = new ArrayList<Disk>();
        List<Network> netWorkLst = new ArrayList<Network>();
        for (VirtualDevice vd : virtualDeviceLst) {
            if (vd instanceof VirtualDisk) {
                Disk disk = new Disk();
                Description description = vd.getDeviceInfo();
                String diskName = description.getLabel();
                disk.setDiskName(diskName);
                int disksize = (int) (((VirtualDisk) vd).getCapacityInKB() / 1024 / 1024);
                disk.setDiskSize(disksize);
                setDiskMap(vd, disk);
                disks.add(disk);
            }
            if (vd instanceof VirtualVmxnet3 || vd instanceof VirtualE1000 || vd instanceof VirtualE1000E) {
                Network network = new Network();
                Description description = vd.getDeviceInfo();
                String networkName = description.getSummary();
                network.setNetworkName(networkName);
                setNetWorkMap(vd, network);

                VirtualDeviceConnectInfo status = vd.getConnectable();
                Boolean isconnect = status.isConnected();
                network.setStatus(isconnect);
                netWorkLst.add(network);
            }
        }
        getVM(vmVo, vmId, vmRef, svc, serviceConnection.getServiceContent(), disks, netWorkLst);
        return vmVo;
    }

    private void getVM(VmInfo vmVo, String vmId, ManagedObjectReference vmRef, ServiceUtil svc, ServiceContent content,
        List<Disk> diskLst, List<Network> netWorks) {
        vmVo.setDisks(diskLst);

        GuestInfo guestInfo = (GuestInfo) svc.getDynamicProperty(vmRef, "guest");
        // 查询网卡
        List<GuestNicInfo> nets = guestInfo.getNet();
        List<Net> nics = new ArrayList<>();
        for (GuestNicInfo net : nets) {
            nics.add(new Net().setName(net.getNetwork()).setIp(net.getIpAddress()));
        }
        vmVo.setNets(nics);
        dealIpAddressNotNull(vmVo, guestInfo);
        vmVo.setNetworks(netWorks);

        VirtualMachineRuntimeInfo virtualMachineRuntimeInfo = (VirtualMachineRuntimeInfo) svc.getDynamicProperty(vmRef,
            "runtime");

        vmVo.setVmStatus(virtualMachineRuntimeInfo.getPowerState().value());
        vmVo.setHostMoId("");
        vmVo.setHostName("");
        ManagedObjectReference rootFolder = content.getRootFolder();

        List<ManagedObjectReference> hostSystemResourceList = svc.getDecendentMoRefs(rootFolder, "HostSystem");
        String dependentHostMoId = null;
        for (ManagedObjectReference hostSystem : hostSystemResourceList) {
            List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostSystem,
                "vm");
            boolean isBelonghostSystem = false;
            isBelonghostSystem = checkBelongHost(vmId, vmList, isBelonghostSystem);
            if (isBelonghostSystem) {
                dependentHostMoId = hostSystem.getValue();
                vmVo.setHostMoId(dependentHostMoId);
                String dependentHostName = (String) svc.getDynamicProperty(hostSystem, "name");
                vmVo.setHostName(dependentHostName);
                break;
            }
        }

        List<ManagedObjectReference> clusterLst = svc.getDecendentMoRefs(rootFolder, "ClusterComputeResource");
        for (ManagedObjectReference clusterVo : clusterLst) {
            List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterVo,
                "host");
            boolean isBelongCluster = false;
            isBelongCluster = checkisBelongCluster(dependentHostMoId, hostlist, isBelongCluster);
            if (isBelongCluster) {
                vmVo.setClusterMoId(clusterVo.getValue());
                String dependentClusterName = (String) svc.getDynamicProperty(clusterVo, "name");
                vmVo.setClusterName(dependentClusterName);
            } else {
                vmVo.setClusterMoId("");
                vmVo.setClusterName("");
            }
        }
    }

    private boolean checkBelongHost(String vmId, List<ManagedObjectReference> vmList, boolean isBelonghostSystem) {
        isBelonghostSystem = checkisBelongCluster(vmId, vmList, isBelonghostSystem);
        return isBelonghostSystem;
    }

    private boolean checkisBelongCluster(String dependentHostMoId, List<ManagedObjectReference> hostlist,
        boolean isBelongCluster) {
        for (ManagedObjectReference clusterHost : hostlist) {
            if (clusterHost.getValue().equalsIgnoreCase(dependentHostMoId)) {
                isBelongCluster = true;
                break;
            }

        }
        return isBelongCluster;
    }

    private void dealIpAddressNotNull(VmInfo vmVo, GuestInfo guestInfo) {
        if (!StringUtils.isEmpty(guestInfo.getIpAddress())) {
            vmVo.setIp(guestInfo.getIpAddress());
        }
    }

    private void setDiskMap(VirtualDevice vd, Disk disk) {
        if (vd.getBacking() instanceof VirtualDiskFlatVer2BackingInfo) {
            VirtualDiskFlatVer2BackingInfo back = (VirtualDiskFlatVer2BackingInfo) vd.getBacking();
            disk.setThinProvisioned(back.isThinProvisioned());
        } else {
            disk.setThinProvisioned(false);
        }
    }

    private void setNetWorkMap(VirtualDevice vd, Network network) {
        if (vd instanceof VirtualVmxnet3) {
            network.setType("VirtualVmxnet3");
        } else if (vd instanceof VirtualE1000) {
            network.setType("VirtualE1000");
        } else if (vd instanceof VirtualE1000E) {
            network.setType("VirtualE1000E");
        }
    }

    @Override
    public VncVo queryVncInfo(String vmwareId, String vmId) throws CustomException {
        VncVo vncVo = new VncVo();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        VirtualMachineConfigInfo config = (VirtualMachineConfigInfo) svc.getDynamicProperty(vmRef, "config");
        if (config == null) {
            return null;
        }
        String pwd = null;
        String port = null;
        boolean vncEnabled = false;
        for (OptionValue optionValue : config.getExtraConfig()) {
            port = getPort(port, optionValue);
            pwd = getPwd(pwd, optionValue);
            vncEnabled = getVncEnabled(vncEnabled, optionValue);
        }
        List<ManagedObjectReference> managedList = (List<ManagedObjectReference>) svc.getDynamicProperty(vmRef,
            "network");

        if (managedList != null && managedList.size() > 0) {
            ManagedObjectReference network = managedList.get(0);
            List<ManagedObjectReference> hostList = (List<ManagedObjectReference>) svc.getDynamicProperty(network,
                "host");
            vncVo.setVncHost(checkHostList(svc, vmId, hostList));
        }
        String vmName = (String) svc.getDynamicProperty(vmRef, "name");
        vncVo.setVmName(vmName);
        vncVo.setVersion(serviceConnection.getServiceContent().getAbout().getVersion());
        vncVo.setVncHelso(pwd);
        vncVo.setVncPort(port);
        vncVo.setVncEnabled(vncEnabled);
        return getHtmlConsoleTicket(vncVo, serviceConnection, vmRef);
    }

    private VncVo getHtmlConsoleTicket(VncVo vncVo, ServiceConnection serviceConnection, ManagedObjectReference vmRef)
        throws CustomException {
        try {
            Version version = new Version(vncVo.getVersion());
            Version version7 = new Version(HTML_CONSOLE_VERSION);
            if (version.compareTo(version7) >= 0) {
                VirtualMachineTicket virtualMachineTicket = serviceConnection.getVimPort()
                    .acquireTicket(vmRef, "webmks");
                vncVo.setTicket(virtualMachineTicket.getTicket());
                vncVo.setVncPort(String.valueOf(virtualMachineTicket.getPort()));
                vncVo.setVncHost(virtualMachineTicket.getHost());
                vncVo.setVncEnabled(false);
            }
        } catch (RuntimeFaultFaultMsg | InvalidStateFaultMsg invalidStateFaultMsg) {
            throw new CustomException(ErrorCode.FAILED_CODE, ErrorCode.VMWARE_INFO_ILLEGAL_MSG);
        }
        return vncVo;
    }

    private String checkHostList(ServiceUtil svc, String vmId, List<ManagedObjectReference> hostList) {
        if (hostList != null && hostList.size() > 0) {
            for (ManagedObjectReference hostSystem : hostList) {
                String hostName = dealNotEmpty(svc, vmId, hostSystem);
                if (!hostName.isEmpty()) {
                    return hostName;
                }
            }
        }
        return "";
    }

    private String dealNotEmpty(ServiceUtil svc, String vmId, ManagedObjectReference hostRef) {
        List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(hostRef, "vm");
        for (ManagedObjectReference virtualMachine : vmList) {
            String vmwareId = virtualMachine.getValue();
            if (vmwareId.equals(vmId)) {
                return (String) svc.getDynamicProperty(hostRef, "name");
            }
        }
        return "";
    }

    @Override
    public String getVmwareToolsStatus(String vmwareId, String vmId) throws CustomException {
        String result = "";
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        ServiceUtil svc = ecb.getServiceUtil();
        GuestInfo guestinfo = (GuestInfo) svc.getDynamicProperty(vmRef, "guest");
        VirtualMachineToolsStatus toolsIsInstall = guestinfo.getToolsStatus();
        result = toolsIsInstall.toString();
        return result;
    }

    @Override
    public void markAsTemplate(String vmwareId, String vmId)
        throws CustomException, FileFaultFaultMsg, RuntimeFaultFaultMsg, InvalidStateFaultMsg, VmConfigFaultFaultMsg {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        service.markAsTemplate(vmRef);
    }

    @Override
    public void mountVMToolsInstaller(String vmwareId, String vmId)
        throws CustomException, VmToolsUpgradeFaultFaultMsg, RuntimeFaultFaultMsg, InvalidStateFaultMsg,
        VmConfigFaultFaultMsg {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_MSG, "vm connection failed");
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(vmId);
        service.mountToolsInstaller(vmRef);
        logger.info("mountVMToolsInstaller success.");
    }

    @Override
    public List<VmOverStatus> getAllOverallStatus(String vmwareId) throws CustomException {
        List<VmOverStatus> vmOverStatuss = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_MSG, "vm connection failed");
        }
        VimPortType vimPort = serviceConnection.getVimPort();
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        ManagedObjectReference rootFolder = serviceConnection.getRootFolder();
        Map<String, ManagedEntityStatus> vmStatusMap = getAllVmStatus(rootFolder, vimPort, serviceContent);
        vmStatusMap.forEach((k, v) -> {
            VmOverStatus vmOverStatus = new VmOverStatus();
            vmOverStatus.setVmId(k);
            vmOverStatus.setOverallStatus(v.value());
            vmOverStatuss.add(vmOverStatus);
        });
        return vmOverStatuss;
    }

    private static TraversalSpec getVmTraversalSpec() {

        TraversalSpec vAppToVApp = new TraversalSpec();
        vAppToVApp.setName("vAppToVApp");
        vAppToVApp.setType("VirtualApp");
        vAppToVApp.setPath("resourcePool");

        TraversalSpec vAppToVm = new TraversalSpec();
        vAppToVm.setName("vAppToVM");
        vAppToVm.setType("VirtualApp");
        vAppToVm.setPath("vm");

        SelectionSpec vAppRecursion = new SelectionSpec();
        vAppRecursion.setName("vAppToVApp");
        SelectionSpec vmInVApp = new SelectionSpec();
        vmInVApp.setName("vAppToVM");
        List<SelectionSpec> vAppToVms = new ArrayList<>();
        vAppToVms.add(vAppRecursion);
        vAppToVms.add(vmInVApp);
        vAppToVApp.getSelectSet().addAll(vAppToVms);

        SelectionSpec selectionSpec = new SelectionSpec();
        selectionSpec.setName("VisitFolders");

        TraversalSpec dataCenterToVMFolder = new TraversalSpec();
        dataCenterToVMFolder.setName("DataCenterToVMFolder");
        dataCenterToVMFolder.setType("Datacenter");
        dataCenterToVMFolder.setPath("vmFolder");
        dataCenterToVMFolder.setSkip(false);
        dataCenterToVMFolder.getSelectSet().add(selectionSpec);

        TraversalSpec traversalSpec = new TraversalSpec();
        traversalSpec.setName("VisitFolders");
        traversalSpec.setType("Folder");
        traversalSpec.setPath("childEntity");
        traversalSpec.setSkip(false);
        List<SelectionSpec> selectionSpecs = new ArrayList<SelectionSpec>();
        selectionSpecs.add(selectionSpec);
        selectionSpecs.add(dataCenterToVMFolder);
        selectionSpecs.add(vAppToVm);
        selectionSpecs.add(vAppToVApp);
        traversalSpec.getSelectSet().addAll(selectionSpecs);
        return traversalSpec;
    }

    /**
     * Gets all vm status.
     *
     * @param rootFolder the root folder
     * @param vimPort the vim port
     * @param serviceContent the service content
     * @return the all vm status
     * @throws ApplicationException the application exception
     */
    public Map<String, ManagedEntityStatus> getAllVmStatus(ManagedObjectReference rootFolder, VimPortType vimPort,
        ServiceContent serviceContent) throws ApplicationException {
        Map<String, ManagedEntityStatus> retVal = new HashMap<>();
        try {
            TraversalSpec tSpec = getVmTraversalSpec();
            PropertySpec propertySpec = new PropertySpec();
            propertySpec.setAll(Boolean.FALSE);
            propertySpec.getPathSet().add("overallStatus");
            propertySpec.setType("VirtualMachine");

            ObjectSpec objectSpec = new ObjectSpec();
            objectSpec.setObj(rootFolder);
            objectSpec.setSkip(Boolean.TRUE);
            objectSpec.getSelectSet().add(tSpec);

            PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
            propertyFilterSpec.getPropSet().add(propertySpec);
            propertyFilterSpec.getObjectSet().add(objectSpec);

            List<PropertyFilterSpec> listpfs = new ArrayList<>(1);
            listpfs.add(propertyFilterSpec);
            List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs, vimPort, serviceContent);

            if (listobjcont != null) {
                for (ObjectContent oc : listobjcont) {
                    ManagedObjectReference mr = oc.getObj();
                    List<DynamicProperty> dps = oc.getPropSet();
                    // vm id 和 overallstatus
                    retVal.put(mr.getValue(), (ManagedEntityStatus) dps.get(0).getVal());
                }
            }
        } catch (SOAPFaultException sfe) {
            logger.error(sfe.toString(), sfe);
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR_CODE, sfe.getMessage());
        }
        return retVal;
    }

    private List<ObjectContent> retrievePropertiesAllObjects(List<PropertyFilterSpec> listpfs, VimPortType vimPort,
        ServiceContent serviceContent) throws ApplicationException {

        RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

        ManagedObjectReference propCollectorRef = serviceContent.getPropertyCollector();

        List<ObjectContent> objectContents = new ArrayList<>();
        try {
            RetrieveResult retrieveResult = vimPort.retrievePropertiesEx(propCollectorRef, listpfs,
                propObjectRetrieveOpts);
            if (retrieveResult != null && retrieveResult.getObjects() != null && !retrieveResult.getObjects()
                .isEmpty()) {
                objectContents.addAll(retrieveResult.getObjects());
            }
            String token = null;
            if (retrieveResult != null && retrieveResult.getToken() != null) {
                token = retrieveResult.getToken();
            }
            while (token != null && !token.isEmpty()) {
                retrieveResult = vimPort.continueRetrievePropertiesEx(propCollectorRef, token);
                token = getObjectContentList(objectContents, retrieveResult);
            }
        } catch (SOAPFaultException | InvalidPropertyFaultMsg | RuntimeFaultFaultMsg exc) {
            logger.error(exc.getMessage(), exc);
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR_CODE, exc.getMessage());
        }
        return objectContents;
    }

    private String getObjectContentList(List<ObjectContent> listobjcontent, RetrieveResult rslts) {
        String token;
        token = null;
        if (rslts != null) {
            token = rslts.getToken();
            if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
                listobjcontent.addAll(rslts.getObjects());
            }
        }
        return token;
    }

    @Override
    public List<VcenterType> getSystemType(String vmwareId) throws CustomException {
        List<VcenterType> vcenterTypes = null;
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent serviceContent = serviceConnection.getServiceContent();
        AboutInfo aboutInfo = serviceContent.getAbout();

        if (null != aboutInfo) {
            vcenterTypes = getSystemOsMap(aboutInfo.getApiVersion());
        }
        return vcenterTypes;
    }

    @Override
    public String createSnapshot(String vmwareId, SnapshotRequest snapshotRequest)
        throws InterruptedException, SnapshotFaultFaultMsg, InvalidNameFaultMsg, VmConfigFaultFaultMsg,
        FileFaultFaultMsg, RuntimeFaultFaultMsg, TaskInProgressFaultMsg, InvalidStateFaultMsg, CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_MSG, "vm connection failed");
        }
        ServiceUtil svc = ecb.getServiceUtil();
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType("VirtualMachine");
        vmRef.setValue(snapshotRequest.getVmId());
        ManagedObjectReference taskMor = service.createSnapshotTask(vmRef, snapshotRequest.getName(),
            snapshotRequest.getDescription(), true, true);
        TaskInfo task;
        // c 最多尝试30s
        for (int i = 3; i < 30; i++) {
            TimeUnit.SECONDS.sleep(i);
            task = (TaskInfo) svc.getDynamicProperty(taskMor, "info");
            if (task.getState() == TaskInfoState.SUCCESS) {
                return snapshotRequest.getName();
            }
            logger.info("creating snapshot...,the numbers of  retry is:{},cost :{} seconds", i - 3, i);
        }
        logger.error("create snapshot task fail,operation overTime");
        throw new ApplicationException("create snapshot task fail,operation overTime");
    }

    @Override
    public List<VMVo> getHostVms(String vmwareId, String hostId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        ManagedObjectReference hostMO = new ManagedObjectReference();
        hostMO.setType("HostSystem");
        hostMO.setValue(hostId);
        List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(hostMO, "vm");
        List<VMVo> vmVos = new ArrayList<>();
        setHostVms(svc, vmLst, vmVos);
        return vmVos;
    }

    private void setHostVms(ServiceUtil svc, List<ManagedObjectReference> vmLst, List<VMVo> vmVos) {
        for (ManagedObjectReference vm : vmLst) {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            VirtualMachineConfigInfo config;
            if (vmConfigInfo == null) {
                continue;
            } else {
                try {
                    config = (VirtualMachineConfigInfo) vmConfigInfo;
                } catch (ClassCastException e) {
                    logger.warn("getVmConfigInfo ClassCastException vm", e);
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
            vmVo.setVmId(vm.getValue());
            String vmName = (String) svc.getDynamicProperty(vm, "name");
            vmVo.setVmName(replaceSpecialCharacterOfVmName(vmName));
            vmVo.setMemory(virtualHardware.getMemoryMB());
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

            setVirtualDeviceDisks(virtualHardware, vmVo);
            vmVos.add(vmVo);
        }
    }

    @Override
    public List<VMVo> getVms(String vmwareId) throws CustomException {
        List<VMVo> result = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ManagedObjectReference rootFolder;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {
            logger.error(String.format(Locale.ENGLISH, "The connection of Vmware %s is empty", vmwareId));
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        if (svc == null || svc.connection == null || content == null) {
            logger.error(String.format(Locale.ENGLISH, "The connection of Vmware %s is empty", vmwareId));
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        rootFolder = content.getRootFolder();
        List<ManagedObjectReference> datacenterList = svc.getDecendentMoRefs(rootFolder, "Datacenter");
        ManagedObjectReference hostRef;
        // 获取数据中心
        for (ManagedObjectReference obj : datacenterList) {
            String datacenterName = (String) svc.getDynamicProperty(obj, "name");
            logger.info("map..{}", datacenterName);
            hostRef = (ManagedObjectReference) svc.getDynamicProperty(obj, "hostFolder");
            List<ManagedObjectReference> clusterLst = svc.getDecendentMoRefs(hostRef, "ClusterComputeResource");
            // 获取cluster下面主机信息
            clusterHostsHandle(svc, clusterLst, result);
            // 设置主机
            hostHandle(svc, hostRef, result);
        }
        return result;
    }

    @Override
    public String powerOnVmTask(String vmwareId, String vmId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            logger.error("ServiceConnection is null.");
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(Constant.VIRTUAL_MACHINE);
        vmRef.setValue(vmId);
        ManagedObjectReference powerOnVMTask;
        if (service == null) {
            logger.error("Service is null.");
            throw new CustomException(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG);
        }
        try {
            powerOnVMTask = service.powerOnVMTask(vmRef, null);
        } catch (FileFaultFaultMsg | InsufficientResourcesFaultFaultMsg | VmConfigFaultFaultMsg | InvalidStateFaultMsg | RuntimeFaultFaultMsg | TaskInProgressFaultMsg ex) {
            logger.error("PowerOn exception", ex);
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR_CODE, ex.getMessage());
        }
        if (powerOnVMTask == null) {
            throw new CustomException(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG);
        }
        return powerOnVMTask.getValue();
    }

    @Override
    public String powerOffVmTask(String vmwareId, String vmId) throws CustomException {
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceConnection serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            logger.error("serviceConnection is null.");
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        VimPortType service = serviceConnection.getVimPort();
        ManagedObjectReference vmRef = new ManagedObjectReference();
        vmRef.setType(Constant.VIRTUAL_MACHINE);
        vmRef.setValue(vmId);
        ManagedObjectReference powerOffVMTask;
        if (service == null) {
            logger.error("Service is null.");
            throw new CustomException(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG);
        }
        try {
            powerOffVMTask = service.powerOffVMTask(vmRef);
        } catch (InvalidStateFaultMsg | RuntimeFaultFaultMsg | TaskInProgressFaultMsg exc) {
            logger.error("PowerOff exception", exc);
            throw new ApplicationException(exc.getMessage(), exc);
        }
        if (powerOffVMTask == null) {
            throw new CustomException(ErrorCode.SYSTEM_ERROR_CODE, ErrorCode.SYSTEM_ERROR_MSG);
        }
        return powerOffVMTask.getValue();
    }

    private void clusterHostsHandle(ServiceUtil svc, List<ManagedObjectReference> clusterLst, List<VMVo> result) {
        if (clusterLst != null) {
            for (ManagedObjectReference clusterVo : clusterLst) {
                String clusterName = (String) svc.getDynamicProperty(clusterVo, "name");
                List<ManagedObjectReference> hostList = (List<ManagedObjectReference>) svc.getDynamicProperty(clusterVo,
                    "host");
                for (ManagedObjectReference htTmp : hostList) {
                    String hostName = (String) svc.getDynamicProperty(htTmp, "name");
                    List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(htTmp,
                        "vm");
                    List<VMVo> vmLsts = new ArrayList<>();
                    setClusterVms(svc, clusterVo, clusterName, htTmp, hostName, vmLst, vmLsts);
                    result.addAll(vmLsts);
                }
            }
        }
    }

    private void setClusterVms(ServiceUtil svc, ManagedObjectReference clusterVo, String clusterName,
        ManagedObjectReference htTmp, String hostName, List<ManagedObjectReference> vmLst, List<VMVo> vmLsts) {
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
            vmVo.setClusterName(clusterName);
            vmVo.setHostName(hostName);
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
            setVirtualDeviceDisks(virtualHardware, vmVo);
            String vmName = (String) svc.getDynamicProperty(vm, "name");
            vmVo.setVmName(replaceSpecialCharacterOfVmName(vmName));
            vmLsts.add(vmVo);
        }
    }

    private String replaceSpecialCharacterOfVmName(String vmName) {
        vmName = StringUtils.replace(vmName, "%2f", "/");
        vmName = StringUtils.replace(vmName, "%2F", "/");
        vmName = StringUtils.replace(vmName, "%25", "%");
        return vmName;
    }

    private void hostHandle(ServiceUtil svc, ManagedObjectReference hostRef, List<VMVo> result) {
        List<ManagedObjectReference> list = (List<ManagedObjectReference>) svc.getDynamicProperty(hostRef,
            "childEntity");
        for (ManagedObjectReference host : list) {
            logger.info("host.getType() : {}", host.getType());
            if ("ClusterComputeResource".equals(host.getType())) {
                continue;
            }
            // 文件夹
            else if (host.getType().equals("Folder")) {
                folderHandle(svc, host, result);
            }
            // 直接主机
            else {
                hostsHandle(svc, host, result);
            }

        }
    }

    private void folderHandle(ServiceUtil svc, ManagedObjectReference host, List<VMVo> result) {
        List<ManagedObjectReference> hostTempList = (List<ManagedObjectReference>) svc.getDynamicProperty(host,
            "childEntity");
        for (ManagedObjectReference tmpHost : hostTempList) {
            String hostName = (String) svc.getDynamicProperty(tmpHost, "name");
            List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(tmpHost,
                "host");
            for (ManagedObjectReference htTmp : hostlist) {
                List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(htTmp, "vm");
                List<VMVo> vmLsts = new ArrayList<VMVo>();
                setFolderVms(svc, hostName, htTmp, vmLst, vmLsts);
                result.addAll(vmLsts);
            }
        }
    }

    private void setFolderVms(ServiceUtil svc, String hostName, ManagedObjectReference htTmp,
        List<ManagedObjectReference> vmLst, List<VMVo> vmLsts) {
        for (ManagedObjectReference vm : vmLst) {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            VirtualMachineConfigInfo config;
            if (vmConfigInfo == null) {
                continue;
            } else {
                config = (VirtualMachineConfigInfo) vmConfigInfo;
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
            vmVo.setHostName(hostName);
            vmVo.setHostId(htTmp.getValue());
            vmVo.setModId(vm.getValue());
            vmVo.setVmId(vm.getValue());
            String vmName = (String) svc.getDynamicProperty(vm, "name");
            vmVo.setVmName(replaceSpecialCharacterOfVmName(vmName));
            vmVo.setStatus("");
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
            setVirtualDeviceDisks(virtualHardware, vmVo);
            vmLsts.add(vmVo);
        }
    }

    private void hostsHandle(ServiceUtil svc, ManagedObjectReference host, List<VMVo> result) {
        List<ManagedObjectReference> hostlist = (List<ManagedObjectReference>) svc.getDynamicProperty(host, "host");
        for (ManagedObjectReference htTmp : hostlist) {
            String hostName = (String) svc.getDynamicProperty(htTmp, "name");
            List<ManagedObjectReference> vmLst = (List<ManagedObjectReference>) svc.getDynamicProperty(htTmp, "vm");
            List<VMVo> vmLsts = new ArrayList<>();
            setHostVms(svc, htTmp, hostName, vmLst, vmLsts);
            result.addAll(vmLsts);
        }
    }

    private void setHostVms(ServiceUtil svc, ManagedObjectReference htTmp, String hostName,
        List<ManagedObjectReference> vmLst, List<VMVo> vmLsts) {
        for (ManagedObjectReference vm : vmLst) {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            VirtualMachineConfigInfo config;
            if (vmConfigInfo == null) {
                continue;
            } else {
                try {
                    config = (VirtualMachineConfigInfo) vmConfigInfo;
                } catch (ClassCastException e) {
                    logger.warn("getVmConfigInfo ClassCastException vm", e);
                    continue;
                }
            }
            // 过滤掉模板
            if (config.isTemplate()) {
                continue;
            }
            VMVo vmVo = setVmBasicInfo(svc, htTmp, hostName, vm, config);
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

    private VMVo setVmBasicInfo(ServiceUtil svc, ManagedObjectReference htTmp, String hostName,
        ManagedObjectReference vm, VirtualMachineConfigInfo config) {
        VirtualMachineSummary vmSummary = (VirtualMachineSummary) svc.getDynamicProperty(vm, "summary");
        VirtualMachineRuntimeInfo getRunTime = vmSummary.getRuntime();
        VirtualMachineGuestSummary vmGuest = vmSummary.getGuest();
        VirtualMachineConfigInfo vmconfig = (VirtualMachineConfigInfo) svc.getDynamicProperty(vm, "config");
        VirtualHardware virtualHardware = vmconfig.getHardware();
        VMVo vmVo = new VMVo();
        vmVo.setHostName(hostName);
        vmVo.setModId(vm.getValue());
        vmVo.setHostId(htTmp.getValue());
        vmVo.setClusterId("");
        vmVo.setStatus("");
        vmVo.setVmId(vm.getValue());
        String vmName = (String) svc.getDynamicProperty(vm, "name");
        vmVo.setVmName(replaceSpecialCharacterOfVmName(vmName));
        vmVo.setMemory(virtualHardware.getMemoryMB() / 1024);
        vmVo.setVcpu(virtualHardware.getNumCPU());
        vmVo.setPowerStatus(getRunTime.getPowerState().toString());
        vmVo.setIpAddess(vmGuest.getIpAddress());
        vmVo.setOsName(getOSNameByFullName(config.getGuestFullName()));
        setVirtualDeviceDisks(virtualHardware, vmVo);
        return vmVo;
    }

    private void setVirtualDeviceDisks(VirtualHardware virtualHardware, VMVo vmVo) {
        List<VirtualDevice> virtualDeviceLst = virtualHardware.getDevice();
        List<Disk> disks = new ArrayList<>();
        for (VirtualDevice vd : virtualDeviceLst) {
            if (vd instanceof VirtualDisk) {
                Disk disk = new Disk();
                Description description = vd.getDeviceInfo();
                String diskName = description.getLabel();
                disk.setDiskName(diskName);
                int disksize = (int) (((VirtualDisk) vd).getCapacityInKB() / 1024 / 1024);
                disk.setDiskSize(disksize);
                setDiskMap(vd, disk);
                disks.add(disk);
            }
        }
        vmVo.setDisks(disks);
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
        vmVo.setOsFullName(osFullName);
    }

    private List<VcenterType> getSystemOsMap(String version) {
        List<VcenterType> vcenterTypes = new ArrayList<>();
        VcenterType vcenterwindowsType = new VcenterType();
        WindowsSystemType windowsType = new WindowsSystemType();
        setWinSystemOs(version, windowsType);
        setWindowsSystemTypes(vcenterTypes, vcenterwindowsType, windowsType);

        VcenterType vcenterLinuxType = new VcenterType();
        LinuxSystemType linuxSystemType = new LinuxSystemType();
        setLinuxSystemOs(version, linuxSystemType);
        setLinuxSystemTypes(vcenterTypes, vcenterLinuxType, linuxSystemType);
        // Other类型虚拟机类型
        VcenterType vcenterOtherType = new VcenterType();
        OtherSystemType otherSystemType = new OtherSystemType();
        setOtherSystemOs(version, otherSystemType);
        setOtherSystemTypes(vcenterTypes, vcenterOtherType, otherSystemType);
        return vcenterTypes;
    }

    private void setOtherSystemTypes(List<VcenterType> vcenterTypes, VcenterType vcenterOtherType,
        OtherSystemType otherSystemType) {
        otherSystemType.setDarwin14_64Guest("Apple Mac OS X 10.10 (64 bit)");
        otherSystemType.setDarwin13_64Guest("Apple Mac OS X 10.9 (64 bit)");
        otherSystemType.setDarwin12_64Guest("Apple Mac OS X 10.8 (64 bit)");
        otherSystemType.setDarwin11_64Guest("Apple Mac OS X 10.7 (64 bit)");
        otherSystemType.setDarwin11Guest("Apple Mac OS X 10.7 (32 bit)");
        otherSystemType.setDarwin10_64Guest("Apple Mac OS X 10.6 (64 bit)");
        otherSystemType.setDarwin10Guest("Apple Mac OS X 10.6 (32 bit)");
        otherSystemType.setFreebsd64Guest("FreeBSD (64 bit)");
        otherSystemType.setFreebsdGuest("FreeBSD (32 bit)");
        otherSystemType.setOs2Guest("IBM OS/2");

        otherSystemType.setNetware6Guest("Novell NetWare 6.x");
        otherSystemType.setNetware5Guest("Novell NetWare 5.1");
        otherSystemType.setSolaris11_64Guest("Oracle Solaris 11 (64 bit)");
        otherSystemType.setSolaris10_64Guest("Oracle Solaris 10 (64 bit)");
        otherSystemType.setSolaris10Guest("Oracle Solaris 10 (32 bit)");
        otherSystemType.setSolaris9Guest("Sun Microsystems Solaris 9");
        otherSystemType.setSolaris8Guest("Sun Microsystems Solaris 8");
        otherSystemType.setOpenServer6Guest("SCO OpenServer 6");
        otherSystemType.setOpenServer5Guest("SCO OpenServer 5");
        otherSystemType.setUnixWare7Guest("SCO UnixWare 7");
        otherSystemType.seteComStation2Guest("Serenity Systems eComStation 2");

        otherSystemType.seteComStationGuest("Serenity Systems eComStation 1");
        otherSystemType.setOtherGuest64("other (64 bit)");
        otherSystemType.setOtherGuest("other (32 bit)");
        otherSystemType.setVmkernel65Guest("VMware ESXi 6.5");
        otherSystemType.setVmkernel6Guest("VMware ESXi 6.x");
        otherSystemType.setVmkernel5Guest("VMware ESXi 5.x");
        otherSystemType.setVmkernelGuest("VMware ESXi 4.x");
        vcenterOtherType.setType("Other");
        vcenterOtherType.setSystemType(otherSystemType);
        vcenterTypes.add(vcenterOtherType);
    }

    private void setLinuxSystemTypes(List<VcenterType> vcenterTypes, VcenterType vcenterLinuxType,
        LinuxSystemType linuxSystemType) {
        linuxSystemType.setRhel7_64Guest("Red Hat Enterprise Linux 7 (64 bit)");
        linuxSystemType.setRhel6_64Guest("Red Hat Enterprise Linux 6 (64 bit)");
        linuxSystemType.setRhel6Guest("Red Hat Enterprise Linux 6 (32 bit)");
        linuxSystemType.setRhel5_64Guest("Red Hat Enterprise Linux 5 (64 bit)");
        linuxSystemType.setRhel5Guest("Red Hat Enterprise Linux 5 (32 bit)");
        linuxSystemType.setRhel4_64Guest("Red Hat Enterprise Linux 4 (64 bit)");
        linuxSystemType.setRhel4Guest("Red Hat Enterprise Linux 4 (32 bit)");
        linuxSystemType.setRhel3_64Guest("Red Hat Enterprise Linux 3 (64 bit)");
        linuxSystemType.setRhel3Guest("Red Hat Enterprise Linux 3 (32 bit)");
        linuxSystemType.setRhel2Guest("Red Hat Enterprise Linux 2.1");
        linuxSystemType.setSles12_64Guest("SUSE Linux Enterprise 12 (64 bit)");
        linuxSystemType.setSles11_64Guest("SUSE Linux Enterprise 11 (64 bit)");
        linuxSystemType.setSles11Guest("SUSE Linux Enterprise 11 (32 bit)");
        linuxSystemType.setSles10_64Guest("SUSE Linux Enterprise 10 (64 bit)");
        linuxSystemType.setSles10Guest("SUSE Linux Enterprise 10 (32 bit)");
        linuxSystemType.setSles64Guest("SUSE Linux Enterprise 8/9 (64 bit)");
        linuxSystemType.setSlesGuest("SUSE Linux Enterprise 8/9 (32 bit)");
        linuxSystemType.setDebian8_64Guest("Debian GNU/Linux 8 (64 bit)");
        linuxSystemType.setDebian8Guest("Debian GNU/Linux 8 (32 bit)");
        linuxSystemType.setDebian7_64Guest("Debian GNU/Linux 7 (64 bit)");

        linuxSystemType.setDebian7Guest("Debian GNU/Linux 7 (32 bit)");
        linuxSystemType.setDebian6_64Guest("Debian GNU/Linux 6 (64 bit)");
        linuxSystemType.setDebian6Guest("Debian GNU/Linux 6 (32 bit)");
        linuxSystemType.setDebian5_64Guest("Debian GNU/Linux 5 (64 bit)");
        linuxSystemType.setDebian5Guest("Debian GNU/Linux 5 (32 bit)");
        linuxSystemType.setDebian4_64Guest("Debian GNU/Linux 4 (64 bit)");
        linuxSystemType.setDebian4Guest("Debian GNU/Linux 4 (32 bit)");
        linuxSystemType.setOpensuse64Guest("SUSE openSUSE (64 bit)");
        linuxSystemType.setOpensuseGuest("SUSE openSUSE (32 bit)");
        linuxSystemType.setAsianux4_64Guest("Asianux 4 (64 bit)");

        linuxSystemType.setAsianux4Guest("Asianux 4 (32 bit)");
        linuxSystemType.setAsianux3_64Guest("Asianux 3 (64 bit)");
        linuxSystemType.setAsianux3Guest("Asianux 3 (32 bit)");
        linuxSystemType.setFedora64Guest("Red Hat Fedora (64 bit)");
        linuxSystemType.setFedoraGuest("Red Hat Fedora (32 bit)");
        linuxSystemType.setUbuntu64Guest("Ubuntu Linux (64 bit)");
        linuxSystemType.setUbuntuGuest("Ubuntu Linux (32 bit)");
        linuxSystemType.setCoreos64Guest("CoreOS Linux (64 bit)");
        linuxSystemType.setOther3xLinux64Guest("3.x Linux (64 bit)");
        linuxSystemType.setOther3xLinuxGuest("3.x Linux (32 bit)");

        linuxSystemType.setOther26xLinux64Guest("2.6.x Linux (64 bit)");
        linuxSystemType.setOther26xLinuxGuest("2.6.x Linux (32 bit)");
        linuxSystemType.setOther24xLinux64Guest("2.4.x Linux (64 bit)");
        linuxSystemType.setOther24xLinuxGuest("2.4.x Linux (32 bit)");
        linuxSystemType.setOtherLinux64Guest("Linux (64 bit)");
        linuxSystemType.setOtherLinuxGuest("Linux (32 bit)");
        vcenterLinuxType.setType("Linux");
        vcenterLinuxType.setSystemType(linuxSystemType);
        vcenterTypes.add(vcenterLinuxType);
    }

    private void setWindowsSystemTypes(List<VcenterType> vcenterTypes, VcenterType vcenterwindowsType,
        WindowsSystemType windowsType) {
        windowsType.setWindows8Server64Guest("Microsoft Windows Server 2012 (64 bit)");
        windowsType.setWindows7Server64Guest("Microsoft Windows Server 2008 R2 (64 bit)");
        windowsType.setWinLonghorn64Guest("Microsoft Windows Server 2008 (64 bit)");
        windowsType.setWinLonghornGuest("Microsoft Windows Server 2008 (32 bit)");
        windowsType.setWinNetEnterprise64Guest("Microsoft Windows Server 2003 (64 bit)");
        windowsType.setWinNetEnterpriseGuest("Microsoft Windows Server 2003 (32 bit)");
        windowsType.setWindows9_64Guest("Microsoft Windows 10 (64 bit)");
        windowsType.setWindows9Guest("Microsoft Windows 10 (32 bit)");
        windowsType.setWindows8_64Guest("Microsoft Windows 8 (64 bit)");
        windowsType.setWindows8Guest("Microsoft Windows 8 (32 bit)");
        windowsType.setWindows7_64Guest("Microsoft Windows 7 (64 bit)");

        windowsType.setWindows7Guest("Microsoft Windows 7 (32 bit)");
        windowsType.setWinVista64Guest("Microsoft Windows Vista (64 bit)");
        windowsType.setWinVistaGuest("Microsoft Windows Vista (32 bit)");
        windowsType.setWinXPPro64Guest("Microsoft Windows XP Professional (64 bit)");
        windowsType.setWinXPProGuest("Microsoft Windows XP Professional (32 bit)");
        windowsType.setWin2000AdvServGuest("Microsoft Windows 2000");
        windowsType.setWinNTGuest("Microsoft Windows NT");
        windowsType.setWin98Guest("Microsoft Windows 98");
        windowsType.setWin95Guest("Microsoft Windows 95");
        windowsType.setWin31Guest("Microsoft Windows 3.1");

        windowsType.setDosGuest("Microsoft MS-DOS");
        vcenterwindowsType.setType("Windows");
        vcenterwindowsType.setSystemType(windowsType);
        vcenterTypes.add(vcenterwindowsType);
    }

    private void setWinSystemOs(String version, WindowsSystemType windowsSystemType) {
        if ("6.5".equals(version)) {
            windowsSystemType.setWindows9Server64Guest("Microsoft Windows Server 2016 (64 bit)");
        }
    }

    private void setLinuxSystemOs(String version, LinuxSystemType linuxSystemType) {
        if ("6.0".equals(version)) {
            linuxSystemType.setCentos64Guest("CentOS 4/5/6/7 (64 bit)");
            linuxSystemType.setCentosGuest("CentOS 4/5/6 (32 bit)");
            linuxSystemType.setAsianux5_64Guest("Asianux 5 (64 bit)");
            linuxSystemType.setOracleLinux64Guest("Oracle Linux 4/5/6/7 (64 bit)");
            linuxSystemType.setOracleLinuxGuest("Oracle Linux 4/5/6 (32 bit)");
        } else if ("6.5".equals(version)) {
            linuxSystemType.setVmwarePhoton64Guest("VMware Photon OS (64 bit)");
            linuxSystemType.setDebian10Guest("Debian GNU/Linux 10 (64 bit)");
            linuxSystemType.setDebian10Guest("Debian GNU/Linux 10 (32 bit)");
            linuxSystemType.setDebian9_64Guest("Debian GNU/Linux 9 (64 bit)");
            linuxSystemType.setDebian9Guest("Debian GNU/Linux 9 (32 bit)");
            linuxSystemType.setCentos7_64Guest("CentOS 7 (64 bit)");
            linuxSystemType.setCentos6_64Guest("CentOS 6 (64 bit)");
            linuxSystemType.setCentos6Guest("CentOS 6 (32 bit)");
            linuxSystemType.setCentos64Guest("CentOS 4/5 (64 bit)");
            linuxSystemType.setCentosGuest("CentOS 4/5 (32 bit)");
            linuxSystemType.setAsianux7_64Guest("Asianux 7 (64 bit)");
            linuxSystemType.setOracleLinux7_64Guest("Oracle Linux 7 (64 bit)");
            linuxSystemType.setOracleLinux6_64Guest("Oracle Linux 6 (64 bit)");
            linuxSystemType.setOracleLinux6Guest("Oracle Linux 6 (32 bit)");
            linuxSystemType.setOracleLinux64Guest("Oracle Linux 4/5 (64 bit)");
            linuxSystemType.setOracleLinuxGuest("Oracle Linux 4/5 (32 bit)");
        }
    }

    private void setOtherSystemOs(String version, OtherSystemType otherSystemType) {
        if ("6.5".equals(version)) {
            otherSystemType.setDarwin16_64Guest("Apple Mac OS X 10.12 (64 bit)");
            otherSystemType.setDarwin15_64Guest("Apple Mac OS X 10.12 (64 bit)");
        }
    }

    private ManagedObjectReference setvmRef(String vmName, ServiceUtil svc) {
        ManagedObjectReference vmRef = null;
        if (svc != null && svc.connection != null) {
            vmRef = svc.getDecendentMoRef(null, "VirtualMachine", vmName);
        }
        return vmRef;
    }

}
