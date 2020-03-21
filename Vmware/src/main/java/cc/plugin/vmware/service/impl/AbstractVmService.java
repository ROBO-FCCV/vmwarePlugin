/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service.impl;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.request.vm.VmConfigInfo;
import cc.plugin.vmware.model.vo.request.vm.VmConfigTemplate;
import cc.plugin.vmware.model.vo.response.vm.TaskVmVo;
import cc.plugin.vmware.service.VmCreateService;

/**
 * 功能描述
 *
 * @since 2019 -09-27
 */
abstract class AbstractVmService implements VmCreateService {

    @Override
    public TaskVmVo createVmOnly(String vmwareId, VmConfigInfo vmConfigInfo) throws CustomException {
        return null;
    }

    @Override
    public TaskVmVo createVmByTemplate(String vmwareId, VmConfigTemplate vmConfigTemplate)
        throws CustomException {
        return null;
    }

}
