/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.to.HostTo;
import cc.plugin.vmware.model.vo.request.DeployOvfRequest;
import cc.plugin.vmware.model.vo.request.ImportOvfRequest;
import cc.plugin.vmware.model.vo.request.host.HostRequest;
import cc.plugin.vmware.model.vo.response.OvfVo;
import cc.plugin.vmware.model.vo.response.host.EsxInfo;
import cc.plugin.vmware.model.vo.response.host.HostInfo;
import cc.plugin.vmware.model.vo.response.host.ImportResourceVo;
import cc.plugin.vmware.model.vo.response.storage.HostBusAdapterVo;
import cc.plugin.vmware.service.HostService;

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

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * 操作host实体类
 *
 * @since 2019 -09-09
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class HostController {

    private static final Logger logger = LoggerFactory.getLogger(HostController.class);

    /**
     * The Host service.
     */
    @Autowired
    HostService hostService;

    /**
     * 查询主机存储总线上的适配器
     *
     * @param vmwareId Vmware ID
     * @param hostId 主机ID
     * @return 主机存储总线适配器 host bus adapters
     */
    @ApiOperation("查询主机存储总线上的适配器")
    @GetMapping("/v1/{vmwareId}/hosts/{hostId}/bus-adapters")
    public RestResult<HostBusAdapterVo> getHostBusAdapters(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("hostId") @ApiParam(value = "主机ID", example = "host-115", required = true) String hostId) {
        RestResult<HostBusAdapterVo> result = new RestResult<>();
        try {
            HostBusAdapterVo hostBusAdapters = hostService.getHostBusAdapters(vmwareId, hostId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getHostBusAdapters successfully");
            result.setData(hostBusAdapters);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getHostBusAdapters failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 获取导入资源
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the import resource
     */
    @ApiOperation("获取导入资源")
    @GetMapping("/v1/{vmwareId}/hosts/{hostId}/resources")
    public RestResult<ImportResourceVo> getImportResource(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("hostId") @ApiParam(value = "主机ID", example = "host-261", required = true) String hostId) {
        RestResult<ImportResourceVo> result = new RestResult<>();
        try {
            ImportResourceVo importResourceVo = hostService.getImportResource(vmwareId, hostId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getImportResource successfully");
            result.setData(importResourceVo);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getImportResource failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 导入Ovf
     *
     * @param vmwareId the vmware id
     * @param ovfRequest 导入Ovf入参
     * @return RestResult<String>  任务ID
     */
    @ApiOperation("导入ovf模板")
    @PostMapping("/v1/{vmwareId}/hosts/action/importOvf")
    public RestResult<OvfVo> importOvf(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody @ApiParam(value = "导入ovf模板请求参数模板", required = true) @Valid ImportOvfRequest ovfRequest) {
        RestResult<OvfVo> result = new RestResult<>();
        try {
            OvfVo data = hostService.importOvf(ovfRequest, vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("vmware importOvf successfully");
            result.setData(data);
        } catch (Exception e) {
            logger.error("importOvf failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Deploy ovf rest result.
     *
     * @param vmwareId the vmware id
     * @param ovfRequest the ovf request
     * @return the rest result
     */
    @ApiOperation("部署ovf模板")
    @PostMapping("/v1/{vmwareId}/hosts/action/deployOvf")
    public RestResult<OvfVo> deployOvf(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody @ApiParam(value = "部署ovf请求参数模板", required = true) @Valid DeployOvfRequest ovfRequest) {
        RestResult<OvfVo> result = new RestResult<>();
        try {
            OvfVo data = hostService.deployOvf(ovfRequest, vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("vmware deployOvf successfully");
            result.setData(data);
        } catch (Exception e) {
            logger.error("deployOvf failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets importing ovf status.
     *
     * @param vmwareId the vmware id
     * @param sessionId the session id
     * @return the importing ovf status
     */
    @ApiOperation("查询导入模板状态")
    @GetMapping("/v1/{vmwareId}/ovf/status")
    public RestResult<String> getImportingOvfStatus(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("sessionId") @ApiParam(value = "vCenter client sessionId", required = true) String sessionId) {
        RestResult<String> result = new RestResult<>();
        try {
            String data = hostService.getImportingOvfStatus(vmwareId, sessionId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("get ovf status successfully");
            result.setData(data);
        } catch (Exception e) {
            logger.error("get ovf status failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 刷新存储适配器信息
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the rest result
     */
    @ApiOperation("刷新存储适配器信息")
    @PostMapping("/v1/{vmwareId}/hosts/{hostId}/action/rescanAllHba")
    public RestResult<Boolean> rescanAllHba(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("hostId") @ApiParam(value = "主机ID", example = "host-9", required = true) String hostId) {
        RestResult<Boolean> result = new RestResult<>();
        try {
            boolean rescanResult = hostService.rescanAllHba(vmwareId, hostId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware rescanAllHba successfully");
            result.setData(rescanResult);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            result.setData(false);
        } catch (Exception e) {
            logger.error("Vmware rescanAllHba failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
            result.setData(false);
        }
        return result;
    }

    /**
     * Gets esx info lst.
     *
     * @param vmwareId the vmware id
     * @param hostRequest the host request
     * @return the esx info lst
     */
    @ApiOperation("根据主机名称查询ESX信息")
    @PostMapping("/v1/{vmwareId}/hosts/action/getEsxInfo")
    public RestResult<List<EsxInfo>> getEsxInfoLst(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody @ApiParam(value = "主机IP列表，数据中心名称") @Valid HostRequest hostRequest) {
        RestResult<List<EsxInfo>> result = new RestResult<>();
        try {
            List<EsxInfo> esxInfos = hostService.queryEsxInfoList(vmwareId, hostRequest);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware get Esx Info Lst successfully");
            result.setData(esxInfos);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            result.setData(null);
        } catch (Exception e) {
            logger.error("Vmware get Esx Info Lst failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
            result.setData(null);
        }
        return result;
    }

    /**
     * Gets serial number.
     *
     * @param vmwareId the vmware id
     * @param hostName the host name
     * @return the serial number
     */
    @ApiOperation("查询主机序列号")
    @GetMapping("/v1/{vmwareId}/hosts/serial-number")
    public RestResult<String> getSerialNumber(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("hostName") @ApiParam(value = "主机名称", example = "192.0.2.0", required = true)
            String hostName) {
        RestResult<String> result = new RestResult<>();
        try {
            String serialNumber = hostService.getSerialNumber(vmwareId, hostName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getSerialNumber successfully");
            result.setData(serialNumber);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            result.setData(null);
        } catch (Exception e) {
            logger.error("Vmware getSerialNumber failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
            result.setData(null);
        }
        return result;
    }

    /**
     * Gets host basic info.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the host basic info
     */
    @ApiOperation("查询主机信息")
    @GetMapping("/v1/{vmwareId}/hosts/{hostId}")
    public RestResult<HostTo> getHostBasicInfo(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("hostId") @ApiParam(value = "主机ID", example = "host-261", required = true) String hostId) {
        RestResult<HostTo> result = new RestResult<>();
        try {
            HostTo hostTo = hostService.getHostBasicInfo(vmwareId, hostId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getHostBasicInfo successfully");
            result.setData(hostTo);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getHostBasicInfo failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

}
