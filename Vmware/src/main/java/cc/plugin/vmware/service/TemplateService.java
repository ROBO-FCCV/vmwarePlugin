/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.service;

import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.vo.response.vm.TemplateVo;

/**
 * 功能描述
 *
 * @since 2019 -09-21
 */
public interface TemplateService {
    /**
     * Gets templates.
     *
     * @param vmwareId the vmware id
     * @return the templates
     * @throws CustomException the custom exception
     */
    TemplateVo getTemplates(String vmwareId) throws CustomException;

    /**
     * Is template existing boolean.
     *
     * @param vmwareId the vmware id
     * @param vmName the vm name
     * @return the boolean
     * @throws CustomException the custom exception
     */
    boolean isTemplateExisting(String vmwareId, String vmName) throws CustomException;
}
