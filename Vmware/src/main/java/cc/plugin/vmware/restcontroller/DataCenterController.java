/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.to.ClusterAndHostTo;
import cc.plugin.vmware.model.vo.response.VcenterEnvironment;
import cc.plugin.vmware.model.vo.response.datacenter.DataCenter;
import cc.plugin.vmware.service.DataCenterService;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.constraints.Pattern;

/**
 * datacenter操作类
 *
 * @since 2019 -09-09
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class DataCenterController {

    private static final Logger logger = LoggerFactory.getLogger(DataCenterController.class);

    /**
     * The Data center service.
     */
    @Autowired
    DataCenterService dataCenterService;

    /**
     * 查询vCenter cpu、内存、datastore
     *
     * @param vmwareId the vmware id
     * @return 环境信息 vcenter basic info
     */
    @ApiOperation("查询vCenter cpu, memory, datastore")
    @GetMapping("/v1/{vmwareId}/resources")
    public RestResult<VcenterEnvironment> getVcenterBasicInfo(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<VcenterEnvironment> result = new RestResult<>();
        try {
            VcenterEnvironment vcenterEnvironment = dataCenterService.getVcenterBasicInfo(vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getVcenterBasicInfo successfully");
            result.setData(vcenterEnvironment);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getVcenterBasicInfo failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 查询Datacenter基本信息
     *
     * @param vmwareId the vmware id
     * @param isActiveHost the is active host
     * @return the data center
     */
    @ApiOperation("查询Datacenter基本信息")
    @GetMapping("/v1/{vmwareId}/datacenters")
    public RestResult<List<DataCenter>> getDataCenter(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("isActiveHost") @ApiParam(value = "主机状态是否正常", example = "true", required = true)
            boolean isActiveHost) {
        RestResult<List<DataCenter>> result = new RestResult<>();
        try {
            List<DataCenter> dataCenters = dataCenterService.getDataCenterBasicInfo(vmwareId, isActiveHost);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getDataCenter successfully");
            result.setData(dataCenters);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getDataCenter failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 查询vcenter下的集群列表，主机列表
     *
     * @param vmwareId the vmware id
     * @return 集群列表 ，主机列表
     */
    @ApiOperation("查询vcenter下的集群列表，主机列表")
    @GetMapping("/v1/{vmwareId}/clusters-hosts")
    public RestResult<List<DataCenter>> getClustersAndHosts(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<DataCenter>> result = new RestResult<>();
        try {
            List<DataCenter> dataCenters = dataCenterService.getClustersAndHosts(vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getClustersAndHosts successfully");
            result.setData(dataCenters);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getClustersAndHosts failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets hosts.
     *
     * @param vmwareId the vmware id
     * @return the hosts
     */
    @ApiOperation("查询vCenter下的主机列表")
    @GetMapping("/v1/{vmwareId}/hosts")
    public RestResult<List<ClusterAndHostTo>> getHosts(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<ClusterAndHostTo>> result = new RestResult<>();
        try {
            List<ClusterAndHostTo> dataCenters = dataCenterService.getHosts(vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getHosts successfully");
            result.setData(dataCenters);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getHosts failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}
