/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.to.ClusterInfoTo;
import cc.plugin.vmware.model.vo.response.cluster.ClusterResourceVo;
import cc.plugin.vmware.model.vo.response.vm.VMVo;
import cc.plugin.vmware.service.ClusterService;
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
public class ClusterController {
    private static final Logger logger = LoggerFactory.getLogger(ClusterController.class);

    /**
     * The Cluster service.
     */
    @Autowired
    ClusterService clusterService;

    /**
     * Gets cluster info.
     *
     * @param vmwareId the vmware id
     * @param clusterId the cluster id
     * @return the cluster info
     */
    @ApiOperation("查询集群信息")
    @GetMapping("/v1/{vmwareId}/clusters/{clusterId}")
    public RestResult<ClusterInfoTo> getClusterInfo(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("clusterId")
        @ApiParam(value = "集群ID", example = "domain-c112", required = true)
            String clusterId) {
        RestResult<ClusterInfoTo> result = new RestResult<>();
        try {
            ClusterInfoTo clusterInfoTo = clusterService.getClusterInfo(vmwareId, clusterId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getClusterInfo successfully");
            result.setData(clusterInfoTo);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getClusterResource failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    @ApiOperation("查询集群下的虚拟机列表")
    @GetMapping("/v1/{vmwareId}/clusters/{clusterId}/vms")
    public RestResult<List<VMVo>> getClusterVms(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("clusterId")
        @ApiParam(value = "集群ID", example = "domain-c112", required = true)
            String clusterId) {
        RestResult<List<VMVo>> result = new RestResult<>();
        try {
            List<VMVo> vms = clusterService.getClusterVms(vmwareId, clusterId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getClusterVms successfully");
            result.setData(vms);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getClusterVms failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}
