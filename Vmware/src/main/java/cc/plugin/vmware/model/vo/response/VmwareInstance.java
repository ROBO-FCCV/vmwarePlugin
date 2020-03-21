/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response;

import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.util.YamlUtil;

import io.swagger.annotations.ApiModelProperty;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @since 2019 -09-10
 */
public class VmwareInstance {
    private static final Logger logger = LoggerFactory.getLogger(VmwareInstance.class);

    @ApiModelProperty(value = "Vmware ID", example = "abb60594c6804d59bc15ad6f63f9e5d7", required = true)
    private String vmwareId;

    @ApiModelProperty(value = "虚拟化平台IP", example = "127.0.0.1", required = true)
    private String ip;

    /**
     * Gets vmware instances.
     *
     * @return the vmware instances
     * @throws CustomException the custom exception
     */
    public static List<VmwareInstance> getVmwareInstances() throws CustomException {
        List<VmwareInstance> instances = new ArrayList<>();
        Map<String, Object> vmwareMap;
        try {
            vmwareMap = YamlUtil.getYamlMap("vmware.yml");
        } catch (Exception e) {
            logger.error("get vmware.yml failed", e);
            throw new CustomException(ErrorCode.GET_VMWARE_YML_FAILED_CODE, ErrorCode.GET_VMWARE_YML_FAILED_MSG);
        }
        if (MapUtils.isEmpty(vmwareMap)) {
            logger.error("Vmware map yaml is empty");
            return instances;
        }
        for (Map.Entry<String, Object> entry : vmwareMap.entrySet()) {
            @SuppressWarnings("unchecked") Map<String, String> vmwareInfo = (Map<String, String>) entry.getValue();
            String ip = vmwareInfo.get("ip");
            String vmwareId = vmwareInfo.get("vmwareId");
            if (StringUtils.isEmpty(vmwareId)) {
                logger.error("Vmware {} has not been initialized.", ip);
                continue;
            }
            instances.add(new VmwareInstance().setIp(ip).setVmwareId(vmwareId));
        }
        return instances;
    }

    /**
     * Gets vmware id.
     *
     * @return the vmware id
     */
    public String getVmwareId() {
        return vmwareId;
    }

    /**
     * Sets vmware id.
     *
     * @param vmwareId the vmware id
     * @return the vmware id
     */
    public VmwareInstance setVmwareId(String vmwareId) {
        this.vmwareId = vmwareId;
        return this;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     * @return the ip
     */
    public VmwareInstance setIp(String ip) {
        this.ip = ip;
        return this;
    }
}
