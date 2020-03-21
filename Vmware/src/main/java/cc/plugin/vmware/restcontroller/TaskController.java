/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.service.TaskService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;

/**
 * task操作接口
 *
 * @since 2019 -09-16
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    /**
     * The Task service.
     */
    @Autowired
    TaskService taskService;

    /**
     * 查询任务状态
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return vmlist task status
     */
    @ApiOperation("查询任务状态")
    @GetMapping("/v1/{vmwareId}/task/status")
    public RestResult<String> getTaskStatus(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("vmId") @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true) String vmId) {
        RestResult<String> result = new RestResult<>();
        try {
            String status = taskService.getTaskStatus(vmwareId, vmId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getTaskStatus successfully");
            result.setData(status);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getTaskStatus failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}
