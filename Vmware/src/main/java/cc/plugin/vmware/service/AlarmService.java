/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.VcenterAlarmInfo;

import java.util.List;

/**
 * vcenter告警相关接口
 *
 * @since 2019 -09-23
 */
public interface AlarmService {

    /**
     * Gets alarm current.
     *
     * @param vmwareId the vmware id
     * @return the alarm current
     * @throws CustomException the custom exception
     */
    List<VcenterAlarmInfo> getAlarmCurrent(String vmwareId) throws CustomException;
}
