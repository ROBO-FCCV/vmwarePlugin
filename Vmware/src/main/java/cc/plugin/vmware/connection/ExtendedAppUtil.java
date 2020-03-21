/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.mo.LoginParamMo;
import cc.plugin.vmware.util.Cipher;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.YamlUtil;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Extended app util.
 *
 * @since 2019 -09-19
 */
@Component
public class ExtendedAppUtil extends AppUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExtendedAppUtil.class);
    private static Cache<String, ExtendedAppUtil> extendedAppUtilCache = CacheBuilder.newBuilder().build();
    private static Map<String, Integer> initMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> updateMap = new ConcurrentHashMap<>();

    @Autowired
    private Cipher cipher;

    /**
     * Instantiates a new Extended app util.
     */
    public ExtendedAppUtil() {
        super();
    }

    /**
     * Initialize extended app util.
     *
     * @param vmwareId the vmware id
     * @return the extended app util
     * @throws ApplicationException the application exception
     * @throws CustomException the custom exception
     */
    @SuppressWarnings("unchecked")
    public ExtendedAppUtil initialize(String vmwareId) throws ApplicationException, CustomException {
        logger.info("Vmware initialization starts");
        ExtendedAppUtil cb = new ExtendedAppUtil();
        Map<String, Object> vmwareMap = YamlUtil.getYamlMap("vmware.yml");
        if (MapUtils.isEmpty(vmwareMap)) {
            logger.error("Vmware map yaml is empty");
            throw new CustomException(ErrorCode.VMWARE_YML_EMPTY_CODE, ErrorCode.VMWARE_YML_EMPTY_MSG);
        }
        String ip = null;
        String username = null;
        String originalPassword = null;
        List<String> vmwareIds = new ArrayList<>();
        for (Map.Entry<String, Object> entry : vmwareMap.entrySet()) {
            Map<String, String> vmwareInfo = (Map<String, String>) entry.getValue();
            vmwareIds.add(vmwareInfo.get("vmwareId"));
        }
        if (!vmwareIds.contains(vmwareId)) {
            logger.warn("The vmwareId {} does not exist in vmware.yml", vmwareId);
            return null;
        }
        for (Map.Entry<String, Object> entry : vmwareMap.entrySet()) {
            Map<String, String> vmwareInfo = (Map<String, String>) entry.getValue();
            if (!StringUtils.equals(vmwareInfo.get("vmwareId"), vmwareId)) {
                continue;
            }
            ip = vmwareInfo.get("ip");
            username = vmwareInfo.get("username");
            originalPassword = vmwareInfo.get("password");
            if (!CommonUtil.isIpv4Address(ip) || StringUtils.isEmpty(username) || StringUtils.isEmpty(
                originalPassword)) {
                logger.error("Vmware {} info is empty.", ip);
                throw new CustomException(ErrorCode.VMWARE_INFO_ILLEGAL_CODE, ErrorCode.VMWARE_INFO_ILLEGAL_MSG);
            }
            break;
        }
        String password = cipher.decrypt(originalPassword);
        initializeExtendedAppUtil(vmwareId, ip, username, password, cb);
        return cb;
    }

    /**
     * Gets extended app util.
     *
     * @param vmwareId the vmware id
     * @return the extended app util
     * @throws CustomException the custom exception
     */
    public ExtendedAppUtil getExtendedAppUtil(String vmwareId) throws CustomException {
        logger.info("getExtendedAppUtil starts");
        ExtendedAppUtil extendedAppUtil = extendedAppUtilCache.getIfPresent(vmwareId);
        if (extendedAppUtil != null) {
            return updateOrDeleteInstance(vmwareId, extendedAppUtil);
        }
        int localVersion = 0;
        initMap.put(vmwareId, localVersion);
        synchronized (vmwareId.intern()) {
            if (localVersion != initMap.get(vmwareId)) {
                logger.warn("The vmwareId {} has been initialized", vmwareId);
                extendedAppUtil = extendedAppUtilCache.getIfPresent(vmwareId);
            } else {
                extendedAppUtil = initialize(vmwareId);
                initMap.put(vmwareId, initMap.get(vmwareId) + 1);
            }
        }
        if (extendedAppUtil == null) {
            throw new CustomException(ErrorCode.VMWAREID_NOT_EXISTED_CODE, ErrorCode.VMWAREID_NOT_EXISTED_MSG);
        }
        logger.info("getExtendedAppUtil ends");
        return extendedAppUtil;
    }

    private void deleteInstance(String vmwareId) {
        extendedAppUtilCache.invalidate(vmwareId);
    }

    private ExtendedAppUtil updateOrDeleteInstance(String vmwareId, ExtendedAppUtil extendedAppUtil)
        throws CustomException {
        Map<String, Object> vmwareMap = YamlUtil.getYamlMap("vmware.yml");
        if (MapUtils.isEmpty(vmwareMap)) {
            logger.error("Vmware map yaml is empty");
            throw new CustomException(ErrorCode.VMWARE_YML_EMPTY_CODE, ErrorCode.VMWARE_YML_EMPTY_MSG);
        }
        List<String> vmwareIds = new ArrayList<>();
        for (Map.Entry<String, Object> entry : vmwareMap.entrySet()) {
            Map<String, String> vmwareInfo = (Map<String, String>) entry.getValue();
            vmwareIds.add(vmwareInfo.get("vmwareId"));
        }
        if (!vmwareIds.contains(vmwareId)) {
            // 文件中不存在该vmwareId，删除已初始化的实例
            logger.warn("The vmware {} info has been deleted in vmware.yml", vmwareId);
            deleteInstance(vmwareId);
            throw new CustomException(ErrorCode.VMWARE_INFO_HAS_BEEN_DELETED_ERROR_CODE,
                ErrorCode.VMWARE_INFO_HAS_BEEN_DELETED_ERROR_MSG);
        }
        return updateInstance(vmwareId, extendedAppUtil, vmwareMap);
    }

    private ExtendedAppUtil updateInstance(String vmwareId, ExtendedAppUtil extendedAppUtil,
        Map<String, Object> vmwareMap) throws CustomException {
        String ip = null;
        String username = null;
        String originalPassword = null;
        for (Map.Entry<String, Object> entry : vmwareMap.entrySet()) {
            Map<String, String> vmwareInfo = (Map<String, String>) entry.getValue();
            if (!StringUtils.equals(vmwareInfo.get("vmwareId"), vmwareId)) {
                continue;
            }
            ip = vmwareInfo.get("ip");
            username = vmwareInfo.get("username");
            originalPassword = vmwareInfo.get("password");
            if (!CommonUtil.isIpv4Address(ip) || StringUtils.isEmpty(username) || StringUtils.isEmpty(
                originalPassword)) {
                logger.error("Vmware {} info is empty.", ip);
                throw new CustomException(ErrorCode.VMWARE_INFO_ILLEGAL_CODE, ErrorCode.VMWARE_INFO_ILLEGAL_MSG);
            }
            break;
        }
        String password = cipher.decrypt(originalPassword);
        // 比较内存和文件的IP，用户名，密码是否存在改动
        String instanceIp = extendedAppUtil.getIp();
        String instanceUserName = extendedAppUtil.getUsername();
        String instancePassword = extendedAppUtil.getPassword();
        if (StringUtils.equals(instanceIp, ip) && StringUtils.equals(instanceUserName, username) && StringUtils.equals(
            instancePassword, password)) {
            // 未改动，直接返回
            logger.info("Get vmware instance successfully. The vmwareId {} has been initialized", vmwareId);
            return extendedAppUtil;
        }
        // 不一致，以文件中信息为准，重新初始化实例
        return reinitializeInstance(vmwareId, ip, username, password);
    }

    private ExtendedAppUtil reinitializeInstance(String vmwareId, String ip, String username, String password)
        throws CustomException {
        logger.info("vmware {} info has been updated, which needs to be reinitialized", vmwareId);
        ExtendedAppUtil instance = new ExtendedAppUtil();
        int localVersion = 0;
        updateMap.put(vmwareId, localVersion);
        synchronized (vmwareId.intern()) {
            if (localVersion != updateMap.get(vmwareId)) {
                logger.warn("The vmwareId {} has been reinitialized", vmwareId);
                instance = extendedAppUtilCache.getIfPresent(vmwareId);
            } else {
                logger.info("Vmware {} reinitialization starts", vmwareId);
                initializeExtendedAppUtil(vmwareId, ip, username, password, instance);
                updateMap.put(vmwareId, updateMap.get(vmwareId) + 1);
                logger.info("Vmware {} reinitialization ends", vmwareId);
            }
        }
        return instance;
    }

    private void initializeExtendedAppUtil(String vmwareId, String ip, String username, String password,
        ExtendedAppUtil instance) throws CustomException {
        instance.parseInput(
            new LoginParamMo().setIp(ip).setUsername(username).setPassword(password).setUrl("https://" + ip + "/sdk"));
        try {
            instance.connect();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CONNECT_VCENTER_ERROR_CODE, ErrorCode.CONNECT_VCENTER_ERROR_MSG);
        }
        extendedAppUtilCache.put(vmwareId, instance);
        logger.info("Vmware {} initialization ends", vmwareId);
    }
}