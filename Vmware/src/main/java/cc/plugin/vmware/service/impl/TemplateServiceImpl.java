/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.vm.TemplateVo;
import cc.plugin.vmware.service.TemplateService;
import cc.plugin.vmware.util.CommonUtil;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VirtualMachineConfigInfo;

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
public class TemplateServiceImpl implements TemplateService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    @Override
    public TemplateVo getTemplates(String vmwareId) throws CustomException {
        TemplateVo result = new TemplateVo();
        List<String> templateNames = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        svc = ecb.getServiceUtil();
        if (serviceConnection == null) {

            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        svc = checkSvc(ecb, svc);
        content = checkContent(content, serviceConnection);
        if (content == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ManagedObjectReference rootFolder = content.getRootFolder();
        if (svc == null || svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        @SuppressWarnings("unchecked") List<ManagedObjectReference> datacenterList = svc.getDecendentMoRefs(rootFolder, "Datacenter");
        setVmFolderLst(templateNames, svc, datacenterList);
        result.setTemplateNames(templateNames);
        return result;
    }

    @Override
    public boolean isTemplateExisting(String vmwareId, String vmName) throws CustomException {
        ManagedObjectReference vmRef;
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        if (svc.connection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        vmRef = svc.getDecendentMoRef(null, "VirtualMachine", vmName);
        return vmRef != null;
    }

    private ServiceUtil checkSvc(ExtendedAppUtil ecb, ServiceUtil svc) {
        if (svc == null) {
            logger.info("svc is empty...");
            svc = ecb.getServiceUtil();
        }
        return svc;
    }

    private ServiceContent checkContent(ServiceContent content, ServiceConnection S1) {
        if (content == null) {
            logger.info("content is null");
            content = S1.getServiceContent();
            return content;
        }
        return content;
    }

    private void setVmFolderLst(List<String> templetNameLst, ServiceUtil svc,
        List<ManagedObjectReference> datacenterList) {
        ManagedObjectReference vmFolder;
        ManagedObjectReference[] vmFolderLst;
        for (ManagedObjectReference obj : datacenterList) {
            vmFolder = (ManagedObjectReference) svc.getDynamicProperty(obj, "vmFolder");
            @SuppressWarnings("unchecked") List<ManagedObjectReference> list = (List<ManagedObjectReference>) svc.getDynamicProperty(vmFolder,
                "childEntity");
            if (list != null) {
                vmFolderLst = list.toArray(new ManagedObjectReference[] {});
            } else {
                vmFolderLst = new ArrayList<ManagedObjectReference>().toArray(new ManagedObjectReference[] {});
            }
            iteratorFolderLst(vmFolderLst, svc, templetNameLst);
        }
    }

    private void iteratorFolderLst(ManagedObjectReference[] vmFolderLst, ServiceUtil svc, List<String> templetNameLst) {
        for (ManagedObjectReference vm : vmFolderLst) {
            handleFolders(svc, templetNameLst, vm);
        }
    }

    private void handleFolders(ServiceUtil svc, List<String> templetNameLst, ManagedObjectReference vm) {
        VirtualMachineConfigInfo vmInfo;
        if (vm.getType().equals("Folder")) {
            // 虚拟机列表
            @SuppressWarnings("unchecked") List<ManagedObjectReference> vmList = (List<ManagedObjectReference>) svc.getDynamicProperty(vm,
                "childEntity");
            if (vmList == null) {
                return;
            }
            for (ManagedObjectReference vmTem : vmList) {
                Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vmTem);
                if (vmConfigInfo == null) {
                    continue;
                } else {
                    vmInfo = (VirtualMachineConfigInfo) vmConfigInfo;
                }
                handlerVmInfo(vmInfo, templetNameLst);
            }
        } else {
            Object vmConfigInfo = CommonUtil.getVmConfigInfo(svc, vm);
            if (vmConfigInfo == null) {
                return;
            } else {
                vmInfo = (VirtualMachineConfigInfo) vmConfigInfo;
            }
            handlerVmInfo(vmInfo, templetNameLst);
        }
    }

    private void handlerVmInfo(VirtualMachineConfigInfo vmInfo, List<String> templetNameLst) {
        if (vmInfo != null && vmInfo.isTemplate()) {
            String templetName = vmInfo.getName();
            templetNameLst.add(templetName);
        }
    }
}
