/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import com.github.dozermapper.core.Mapper;
import com.vmware.vim25.HostConfigFaultFaultMsg;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostScsiDisk;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NotFoundFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ScsiLun;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceConnection;
import cc.plugin.vmware.connection.ServiceUtil;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.storage.HostDisk;
import cc.plugin.vmware.service.StorageService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
@Service
public class StorageServiceImpl implements StorageService {
    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    /**
     * The Extended app util.
     */
    @Autowired
    ExtendedAppUtil extendedAppUtil;

    /**
     * The Dozer mapper.
     */
    @Autowired
    Mapper dozerMapper;

    @Override
    public List<HostDisk> getDisks(String vmwareId, String datacenterName, String hostName) throws CustomException {
        List<HostDisk> hostDisks = new ArrayList<>();
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ServiceConnection serviceConnection;
        ServiceUtil svc;
        ecb.connect();
        serviceConnection = ecb.getConnection();
        if (serviceConnection == null) {
            throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
        }
        ServiceContent content = serviceConnection.getServiceContent();
        VimPortType service = serviceConnection.getVimPort();
        svc = ecb.getServiceUtil();
        try {
            ManagedObjectReference datacenterRef = service.findByInventoryPath(content.getSearchIndex(),
                datacenterName);
            if (svc.connection == null) {
                throw new CustomException(ErrorCode.CONNECTION_EXCEPTION_CODE, ErrorCode.CONNECTION_EXCEPTION_MSG);
            }
            ManagedObjectReference hostSystem = svc.getDecendentMoRef(datacenterRef, "HostSystem", hostName);
            HostConfigManager hostConfigManager = (HostConfigManager) svc.getDynamicProperty(hostSystem,
                "configManager");
            ManagedObjectReference hostDatastoreSystem = hostConfigManager.getDatastoreSystem();
            List<HostScsiDisk> hostScsiDisks = service.queryAvailableDisksForVmfs(hostDatastoreSystem, null);
            hostDisks = hostScsiDisks
                .stream()
                .map(disk -> dozerMapper.map(disk, HostDisk.class))
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("query error...", e);
        } return hostDisks;
    }

    @Override
    public List<String> getStorageWwn(String vmwareId, String hostName) throws CustomException {
        List<String> wwnList = null;
        ExtendedAppUtil ecb = extendedAppUtil.getExtendedAppUtil(vmwareId);
        ecb.connect();
        ServiceUtil svc = ecb.getServiceUtil();
        if (svc != null && svc.connection != null) {
            ManagedObjectReference hostRef = svc.getDecendentMoRef(null, "HostSystem", hostName);
            wwnList = getWwn(svc, hostRef);
        }

        return wwnList;
    }

    private List<String> getWwn(ServiceUtil svc, ManagedObjectReference hostRef) {
        logger.info("_getHostStorageLunListOfDevice start.");
        List<String> wwnList = new ArrayList<String>();

        Map<String, ScsiLun> scsiLunsMap = getLunMap(svc, hostRef);

        if (null != scsiLunsMap) {
            for (String wwn : scsiLunsMap.keySet()) {
                logger.debug("wwn : {}", wwn);
                wwnList.add(wwn);
            }
        }
        return wwnList;
    }

    private Map<String, ScsiLun> getLunMap(ServiceUtil svc, ManagedObjectReference hostRef) {
        Map<String, ScsiLun> lunMap = new HashMap<>();
        HostConfigInfo hostConfig = (HostConfigInfo) svc.getDynamicProperty(hostRef, "config");

        List<ScsiLun> luns = hostConfig.getStorageDevice().getScsiLun();

        if (null == luns) {
            return null;
        }

        for (ScsiLun scsiLun : luns) {
            String wwn = getWWNFromLunCanonicalName(scsiLun.getCanonicalName().trim());
            if (!"disk".equals(scsiLun.getDeviceType())) {
                continue;
            }
            lunMap.put(wwn, scsiLun);
        }
        return lunMap;
    }

    /**
     * Gets wwn from lun canonical name.
     *
     * @param diskName the disk name
     * @return the wwn from lun canonical name
     */
    public static String getWWNFromLunCanonicalName(String diskName) {
        logger.debug("diskName is {}", diskName);
        if (StringUtils.isBlank(diskName)) {
            return "";
        }
        String diskNameHeader = "naa.";
        if (Pattern.matches("^" + diskNameHeader + "[\\w]*$", diskName.trim())) {
            return diskName.substring(diskName.indexOf(diskNameHeader) + diskNameHeader.length());
        } else {
            return "";
        }
    }
}
