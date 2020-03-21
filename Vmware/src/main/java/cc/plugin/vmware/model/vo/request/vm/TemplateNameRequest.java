/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;

/**
 * 查询设置的虚拟机实体类
 *
 * @since 2019 -09-16
 */
public class TemplateNameRequest {
    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称", example = "vm-new", required = true)
    @NotEmpty
    private String templateName;

    /**
     * Gets template name.
     *
     * @return the template name
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets template name.
     *
     * @param templateName the template name
     * @return the template name
     */
    public TemplateNameRequest setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }
}
