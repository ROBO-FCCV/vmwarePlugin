/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.response.NetworkVo;
import cc.plugin.vmware.service.NetworkService;

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

import java.util.List;

import javax.validation.constraints.Pattern;

/**
 * 网络查询类
 *
 * @since 2019 -09-10
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class NetworkController {

    private static final Logger logger = LoggerFactory.getLogger(NetworkController.class);

    /**
     * The Network service.
     */
    @Autowired
    NetworkService networkService;

    /**
     * 查询网络信息
     *
     * @param vmwareId the vmware id
     * @param datacenterName 数据中心名称
     * @param hostName 主机名称
     * @return 网络信息 networks
     */
    @ApiOperation("查询网络信息")
    @GetMapping("/v1/{vmwareId}/networks")
    public RestResult<List<NetworkVo>> getNetworks(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("datacenterName") @ApiParam(value = "数据中心名称", example = "Datacenter", required = true)
            String datacenterName,
        @RequestParam("hostName") @ApiParam(value = "主机名", example = "192.0.2.0", required = true) String hostName) {
        RestResult<List<NetworkVo>> result = new RestResult<>();
        try {
            List<NetworkVo> networks = networkService.getNetworks(vmwareId, datacenterName, hostName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getNetworks successfully");
            result.setData(networks);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getNetworks failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets esxi networks.
     *
     * @param vmwareId the vmware id
     * @param hostName the host name
     * @return the esxi networks
     */
    @ApiOperation("查询ESXi网络信息")
    @GetMapping("/v1/{vmwareId}/esxi-networks")
    public RestResult<List<String>> getEsxiNetworks(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("hostName") @ApiParam(value = "主机名", example = "192.0.2.0", required = true) String hostName) {
        RestResult<List<String>> result = new RestResult<>();
        try {
            List<String> networks = networkService.getEsxiNetworks(vmwareId, hostName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getEsxiNetworks successfully");
            result.setData(networks);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getEsxiNetworks failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}