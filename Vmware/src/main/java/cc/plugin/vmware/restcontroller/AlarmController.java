/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.CommonConstants;
import cc.plugin.vmware.model.vo.response.VcenterAlarmInfo;
import cc.plugin.vmware.service.AlarmService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.constraints.Pattern;

/**
 * 对vmware告警进行操作
 *
 * @since 2019 -09-19
 */
@RestController
@RequestMapping(value = "", produces = {"application/json;charset=UTF-8"})
@Validated
class AlarmController {
    @Autowired
    private AlarmService alarmService;

    /**
     * 获取vmware当前告警
     *
     * @param vmwareId the vmware id
     * @return the alarm list
     */
    @ApiOperation("查询vmware当前告警")
    @RequestMapping(value = "/v1/{vmwareId}/alarms", method = RequestMethod.GET)
    public RestResult<List<VcenterAlarmInfo>> getAlarmList(@PathVariable("vmwareId")
    @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
    @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<VcenterAlarmInfo>> result = null;
        try {
            List<VcenterAlarmInfo> alarmInfos = alarmService.getAlarmCurrent(vmwareId);
            result = new RestResult();
            result.setData(alarmInfos);
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("get alarm list end");
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
