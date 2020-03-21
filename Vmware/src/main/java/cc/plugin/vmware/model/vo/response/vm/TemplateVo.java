/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.vm;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Vm查询返回结果
 *
 * @since 2019 -09-10
 */
public class TemplateVo {
    @ApiModelProperty(value = "模板名称", required = true)
    private List<String> templateNames;

    /**
     * Gets template names.
     *
     * @return the template names
     */
    public List<String> getTemplateNames() {
        return templateNames;
    }

    /**
     * Sets template names.
     *
     * @param templateNames the template names
     * @return the template names
     */
    public TemplateVo setTemplateNames(List<String> templateNames) {
        this.templateNames = templateNames;
        return this;
    }
}
