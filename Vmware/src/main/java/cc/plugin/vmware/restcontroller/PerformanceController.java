/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.CommonConstants;
import cc.plugin.vmware.model.vo.request.PerformRequest;
import cc.plugin.vmware.model.vo.request.host.VcenterEsxi;
import cc.plugin.vmware.model.vo.request.vm.VcenterVm;
import cc.plugin.vmware.model.vo.response.PerfDataVo;
import cc.plugin.vmware.service.PerformService;

import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.constraints.Pattern;

/**
 * 获取vmware主机及虚拟机性能数据
 *
 * @since 2019 -09-19
 */
@RestController
@RequestMapping(value = "", produces = {"application/json;charset=UTF-8"})
@Validated
class PerformanceController {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);

    @Autowired
    private PerformService performService;

    /**
     * 获取虚拟机性能数据
     *
     * @param vmwareId the vmware id
     * @param performRequest the perform request
     * @return the vm performance
     */
    @ApiOperation("获取虚拟机性能数据")
    @RequestMapping(value = "/v1/{vmwareId}/performance/action/getVmPerformance", method = RequestMethod.POST,
        produces = {"application/json;charset=UTF-8"})
    public RestResult<List<PerfDataVo>> getVmPerformance(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody @ApiParam(value = "请求主机列表,每个主机需要带虚机列表,需要的性能指标", required = true) PerformRequest performRequest) {
        if (!checkParam(performRequest.getVcenterEsxis(), false)) {
            return new RestResult(ErrorCode.PARAMETER_ERROR_CODE,
                ErrorCode.PARAMETER_ERROR_MSG + "vm id or name is null or empty.");
        }
        return getPerformance(vmwareId, performRequest, false, "get vms performance success");
    }

    /**
     * 获取主机性能数据
     *
     * @param vmwareId the vmware id
     * @param performRequest the perform request
     * @return the host performance
     */
    @ApiOperation("获取主机性能数据")
    @RequestMapping(value = "/v1/{vmwareId}/action/getHostPerformance", method = RequestMethod.POST,
        produces = {"application/json;charset=UTF-8"})
    public RestResult<List<PerfDataVo>> getHostPerformance(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody @ApiParam(value = "请求主机列表,需要的性能指标", required = true) PerformRequest performRequest) {
        if (!checkParam(performRequest.getVcenterEsxis(), true)) {
            return new RestResult(ErrorCode.PARAMETER_ERROR_CODE,
                ErrorCode.PARAMETER_ERROR_MSG + "vcenterEsxis id or hostName is null or empty.");
        }
        return getPerformance(vmwareId, performRequest, true, "get hosts performance success");
    }

    private boolean checkParam(List<VcenterEsxi> vcenterEsxis, boolean isHost) {
        for (VcenterEsxi esxi : vcenterEsxis) {
            if (StringUtils.isEmpty(esxi.getId()) || StringUtils.isEmpty(esxi.getHostName())) {
                logger.error("Parameter error,vcenterEsxis id or hostName is null or empty.");
                return false;
            }
            if (!isHost && !checkParam(esxi.getVmList())) {
                return false;
            }
        }
        return true;
    }

    private boolean checkParam(List<VcenterVm> vms) {
        for (VcenterVm vm : vms) {
            if (StringUtils.isEmpty(vm.getId()) || StringUtils.isEmpty(vm.getVmName())) {
                logger.error("Parameter error,vm id or name is null or empty.");
                return false;
            }
        }
        return true;
    }

    private RestResult getPerformance(String vmwareId, PerformRequest performRequest, boolean isHost, String msg) {
        RestResult<List<PerfDataVo>> result = new RestResult<>();
        try {
            List<PerfDataVo> perfDataVos = performService.getPerformance(vmwareId, performRequest, isHost);
            result.setData(perfDataVos);
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg(msg);
        } catch (CustomException e) {
            logger.error("Connection fail", e);
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (RuntimeFaultFaultMsg e) {
            logger.error("RuntimeFaultFaultMsg is ", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        } catch (InvalidPropertyFaultMsg e) {
            logger.error("InvalidPropertyFaultMsg is ", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }

        return result;
    }
}
