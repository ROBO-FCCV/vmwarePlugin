/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.response.storage.HostDisk;
import cc.plugin.vmware.service.StorageService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.constraints.Pattern;

/**
 * 操作host实体类
 *
 * @since 2019 -09-09
 */
@RequestMapping(value = "", produces = {"application/json;charset=UTF-8"})
@RestController
@Validated
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);

    /**
     * The Storage service.
     */
    @Autowired
    StorageService storageService;

    /**
     * 查询硬盘列表
     *
     * @param vmwareId the vmware id
     * @param datacenterName 数据中心名称
     * @param hostName 主机名称
     * @return the disks
     */
    @ApiOperation("查询硬盘列表")
    @GetMapping(value = "/v1/{vmwareId}/disks")
    public RestResult<List<HostDisk>> getDisks(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("datacenterName") @ApiParam(value = "数据中心名称", example = "datastore", required = true)
            String datacenterName,
        @RequestParam("hostName") @ApiParam(value = "主机名称", example = "192.0.2.0", required = true) String hostName) {
        RestResult<List<HostDisk>> result = new RestResult<>();
        try {
            List<HostDisk> hostDisks = storageService.getDisks(vmwareId, datacenterName, hostName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getDisks successfully");
            result.setData(hostDisks);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getDisks failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets storage wwn.
     *
     * @param vmwareId the vmware id
     * @param hostName the host name
     * @return the storage wwn
     */
    @ApiOperation("查询存储唯一标识")
    @GetMapping(value = "/v1/{vmwareId}/storage/wwn")
    public RestResult<List<String>> getStorageWwn(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("hostName") @ApiParam(value = "主机名称", example = "192.0.2.0", required = true) String hostName) {
        RestResult<List<String>> result = new RestResult<>();
        try {
            List<String> hostDisks = storageService.getStorageWwn(vmwareId, hostName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getStorageWwn successfully");
            result.setData(hostDisks);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getStorageWwn failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}
