/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.vm.VmConfigInfo;
import cc.plugin.vmware.model.vo.request.vm.VmConfigTemplate;
import cc.plugin.vmware.model.vo.response.vm.TaskVmVo;

/**
 * 功能描述
 *
 * @since 2019 -09-23
 */
public interface VmCreateService {
    /**
     * 创建虚拟机
     *
     * @param vmwareId the vmware id
     * @param vmConfigInfo 虚拟机参数
     * @return the task vm vo
     * @throws CustomException the custom exception
     */
    TaskVmVo createVmOnly(String vmwareId, VmConfigInfo vmConfigInfo) throws CustomException;

    /**
     * Create vm by template task vm vo.
     *
     * @param vmwareId the vmware id
     * @param vmConfigInfo the vm config info
     * @return the task vm vo
     * @throws CustomException the custom exception
     */
    TaskVmVo createVmByTemplate(String vmwareId, VmConfigTemplate vmConfigInfo) throws CustomException;
}
