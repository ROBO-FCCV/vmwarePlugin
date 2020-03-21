/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.request.vm.TemplateNameRequest;
import cc.plugin.vmware.model.vo.response.vm.TemplateVo;
import cc.plugin.vmware.service.TemplateService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * 虚拟机查询类
 *
 * @since 2019 -09-10
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class TemplateController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    /**
     * The Template service.
     */
    @Autowired
    TemplateService templateService;

    /**
     * 查询所有模板
     *
     * @param vmwareId the vmware id
     * @return 所有模板信息 templates
     */
    @ApiOperation("查询所有模板")
    @GetMapping("/v1/{vmwareId}/templates")
    public RestResult<TemplateVo> getTemplates(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<TemplateVo> result = new RestResult<>();
        try {
            TemplateVo templates = templateService.getTemplates(vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getTemplates successfully");
            result.setData(templates);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getTemplates failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 根据模板名称查询模板是否存在
     *
     * @param vmwareId the vmware id
     * @param templateName 模板名称
     * @return 模板是否存在 rest result
     */
    @ApiOperation("根据模板名称查询模板是否存在")
    @GetMapping("/v1/{vmwareId}/templates/template-availability")
    public RestResult<Boolean> isTemplateExisting(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam
        @ApiParam(value = "模板名称", required = true) String templateName) {
        RestResult<Boolean> result = new RestResult<>();
        try {
            boolean templateExisting = templateService.isTemplateExisting(vmwareId, templateName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware isTemplateExisting successfully");
            result.setData(templateExisting);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            result.setData(false);
        } catch (Exception e) {
            logger.error("Vmware isTemplateExisting failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
            result.setData(false);
        }
        return result;
    }
}