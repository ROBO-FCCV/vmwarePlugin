/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.PerformRequest;
import cc.plugin.vmware.model.vo.response.PerfDataVo;

import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;

import java.util.List;

/**
 * 功能描述
 *
 * @since 2019 -09-26
 */
public interface PerformService {
    /**
     * 获取性能数据
     *
     * @param vmwareId Vmware ID
     * @param performRequest 性能请求参数
     * @param isHost 判断是主机性能函数虚机性能
     * @return 性能数据 performance
     * @throws RuntimeFaultFaultMsg 异常
     * @throws InvalidPropertyFaultMsg 异常
     * @throws CustomException the custom exception
     */
    List<PerfDataVo> getPerformance(String vmwareId, PerformRequest performRequest, boolean isHost)
        throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg, CustomException;
}
