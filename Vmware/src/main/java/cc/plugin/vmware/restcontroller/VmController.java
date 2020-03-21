/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.ApplicationException;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.CommonConstants;
import cc.plugin.vmware.model.vo.request.host.HostRequest;
import cc.plugin.vmware.model.vo.request.vm.RenameVmRequest;
import cc.plugin.vmware.model.vo.request.vm.SnapshotRequest;
import cc.plugin.vmware.model.vo.request.vm.VmConfigInfo;
import cc.plugin.vmware.model.vo.request.vm.VmConfigTemplate;
import cc.plugin.vmware.model.vo.request.vm.VmNameRequest;
import cc.plugin.vmware.model.vo.response.vm.TaskVmVo;
import cc.plugin.vmware.model.vo.response.vm.VMVo;
import cc.plugin.vmware.model.vo.response.vm.VcenterType;
import cc.plugin.vmware.model.vo.response.vm.VmByHostIpRes;
import cc.plugin.vmware.model.vo.response.vm.VmInfo;
import cc.plugin.vmware.model.vo.response.vm.VmOverStatus;
import cc.plugin.vmware.model.vo.response.vm.VmStatus;
import cc.plugin.vmware.model.vo.response.vm.VmStatusVo;
import cc.plugin.vmware.model.vo.response.vm.VncVo;
import cc.plugin.vmware.service.impl.VmByTemplateImpl;
import cc.plugin.vmware.service.impl.VmNotIsoServiceImpl;
import cc.plugin.vmware.service.impl.VmOnlyServiceImpl;
import cc.plugin.vmware.service.impl.VmServiceImpl;
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
 * 虚拟机查询类
 *
 * @since 2019 -09-10
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class VmController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VmController.class);

    @Autowired
    private VmOnlyServiceImpl vmOnlyServiceImpl;

    @Autowired
    private VmNotIsoServiceImpl vmNotIsoServiceImpl;

    @Autowired
    private VmByTemplateImpl vmByTemplateImpl;

    @Autowired
    private VmServiceImpl vmServiceImpl;

    /**
     * 查询vm列表和/getVmByDatacenterHost同一套代码
     *
     * @param vmwareId header
     * @param hostRequest hostIP列表，数据中心名称
     * @return the rest result
     */
    @ApiOperation("根据主机名称查询虚拟机列表")
    @PostMapping("/v1/{vmwareId}/vms/action/getVmsByHostNames")
    public RestResult<VmByHostIpRes> queryVmDetailByHost(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @ApiParam(value = "主机IP列表，数据中心名称")
        @Valid HostRequest hostRequest) {
        RestResult<VmByHostIpRes> result = new RestResult<>();
        try {
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("query Vm Detail from Host success");
            result.setData(vmServiceImpl.queryVmListByHost(vmwareId, hostRequest));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("queryVmDetailByHost exception :", e);
        }
        return result;
    }

    /**
     * 查询虚拟机状态
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return result rest result
     */
    @ApiOperation("查询虚拟机状态")
    @GetMapping("/v1/{vmwareId}/vms/{vmId}/status")
    public RestResult<VmStatusVo> queryVmStatus(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<VmStatusVo> result = new RestResult<>();
        try {
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("query Host success");
            result.setData(vmServiceImpl.getVmStatusByVmId(vmwareId, vmId));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("queryVmStatus exception :", e);
        }
        return result;
    }

    /**
     * 根据ID查询虚拟机信息
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return RestResult<VmVo> vm by id
     */
    @ApiOperation("根据ID查询虚拟机信息")
    @GetMapping("/v1/{vmwareId}/vms/{vmId}")
    public RestResult<VmInfo> getVmById(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<VmInfo> result = new RestResult<>();
        try {
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("query vm detail success");
            result.setData(vmServiceImpl.queryVmDetailByVmId(vmwareId, vmId));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("getVmById exception :", e);
        }
        return result;
    }

    /**
     * 查询vnc信息
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return RestResult<VncVo> rest result
     */
    @ApiOperation("查询vnc信息")
    @GetMapping("/v1/{vmwareId}/vms/{vmId}/vnc")
    public RestResult<VncVo> queryVncInfo(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<VncVo> result = new RestResult<>();
        try {
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("query Host success");
            result.setData(vmServiceImpl.queryVncInfo(vmwareId, vmId));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("queryVncInfo exception :", e);
        }
        return result;
    }

    /**
     * 根据名称查询虚拟机ID
     *
     * @param vmwareId the vmware id
     * @param vmName the vm name
     * @return RestResult<String> vm id by name
     */
    @ApiOperation("根据名称查询虚拟机ID")
    @GetMapping("/v1/{vmwareId}/vms/id")
    public RestResult<String> getVmIdByName(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("vmName")
        @ApiParam(value = "虚拟机名称", example = "vm-1470", required = true)
            String vmName) {
        RestResult<String> result = new RestResult<>();
        try {
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("query vmId success");
            result.setData(vmServiceImpl.getVmIdByName(vmwareId, vmName));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("getVmIdByName exception :", e);
        }

        return result;
    }

    /**
     * 开启VM
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return 上电结果 rest result
     */
    @ApiOperation("虚拟机上电")
    @PostMapping("/v1/{vmwareId}/vms/{vmId}/action/powerOn")
    public RestResult<String> powerOnVm(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<String> result = new RestResult<>();
        try {
            result.setData(vmServiceImpl.pownOnVm(vmwareId, vmId));
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("power On Vm success");
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("powerOnVm exception :", e);
        }

        return result;
    }

    /**
     * 关闭VM
     *
     * @param vmwareId the vmware id
     * @param vmId 参数
     * @return 下电结果 rest result
     */
    @ApiOperation("虚拟机下电")
    @PostMapping("/v1/{vmwareId}/vms/{vmId}/action/powerOff")
    public RestResult<String> powerOffVm(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<String> result = new RestResult<>();
        try {
            result.setData(vmServiceImpl.pownOffVm(vmwareId, vmId));
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("power Off Vm success");
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("powerOffVm exception :", e);
        }

        return result;
    }

    /**
     * 不使用镜像创建虚拟机
     *
     * @param vmwareId the vmware id
     * @param vmConfigInfo the vm config info
     * @return RestResult<TaskVmVo> rest result
     */
    @ApiOperation("创建虚拟机")
    @PostMapping("/v1/{vmwareId}/vms")
    public RestResult<TaskVmVo> createVmNotIso(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @ApiParam(value = "创建虚拟机入参", required = true)
        @Valid VmConfigInfo vmConfigInfo) {
        RestResult<TaskVmVo> result = new RestResult<>();
        try {
            result.setData(vmNotIsoServiceImpl.createVmOnly(vmwareId, vmConfigInfo));
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("create VM success");
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("createVmNotIso fail", e);
        }

        return result;
    }

    /**
     * 根据模板创建虚拟机
     *
     * @param vmwareId the vmware id
     * @param vmConfigTemplate 虚拟机信息
     * @return the rest result
     */
    @ApiOperation("克隆虚拟机")
    @PostMapping("/v1/{vmwareId}/vms/action/clone")
    public RestResult<TaskVmVo> createVmByTemplate(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @ApiParam(value = "创建虚拟机入参", required = true)
        @Valid VmConfigTemplate vmConfigTemplate) {
        RestResult<TaskVmVo> result = new RestResult<>();
        TaskVmVo taskVmVo = new TaskVmVo();
        try {
            taskVmVo = vmByTemplateImpl.createVmByTemplate(vmwareId, vmConfigTemplate);
        } catch (CustomException connectionException) {
            result.setCode(connectionException.getErrorCode());
            result.setMsg(connectionException.getMessage());
        } catch (Exception exception) {
            LOGGER.error("create Vm from template fail", exception);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(exception.getMessage());
            return result;
        }

        result.setData(taskVmVo);
        result.setCode(CommonConstants.SUCCESS);
        result.setMsg("create Vm from template success");
        return result;
    }

    /**
     * 删除vm
     *
     * @param vmwareId the vmware id
     * @param renameVmRequest the rename vm request
     * @return the rest result
     */
    @ApiOperation("删除虚拟机")
    @PostMapping("/v1/{vmwareId}/vms/action/delete")
    public RestResult<String> deleteVm(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @ApiParam(value = "虚拟机信息", required = true)
        @Valid RenameVmRequest renameVmRequest) {
        RestResult<String> result = new RestResult<>();
        String message = "";
        try {
            message = vmServiceImpl.deleteVm(vmwareId, renameVmRequest.getVmId(), renameVmRequest.getVmName());
        } catch (CustomException e) {
            result.setData(e.getMessage());
            result.setCode(e.getErrorCode());
            result.setMsg("delete Vm failed");
            return result;
        } catch (Exception exception) {
            LOGGER.error("delete Vm fail ", exception);
            result.setData(exception.getMessage());
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg("delete Vm failed");
            return result;
        }
        result.setData(message);
        result.setCode(CommonConstants.SUCCESS);
        result.setMsg("delete Vm success");
        return result;
    }

    /**
     * 重置此虚拟机的电源。 如果当前状态是powerOn，则此方法首先执行powerOff（hard）。 一旦电源状态为powerOff，则此方法执行powerOn（选项）
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the rest result
     */
    @ApiOperation("重置此虚拟机的电源")
    @PostMapping("/v1/{vmwareId}/vms/{vmId}/action/reset")
    public RestResult<String> resetVm(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<String> result = new RestResult();
        String message = "";
        try {
            message = vmServiceImpl.resetVm(vmwareId, vmId);
        } catch (CustomException e) {
            result.setData(e.getMessage());
            result.setCode(e.getErrorCode());
            result.setMsg("reset Vm failed");
            return result;
        } catch (Exception exception) {
            LOGGER.error("reset Vm fail ", exception);
            result.setData(exception.getMessage());
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg("reset Vm failed");
            return result;
        }
        result.setData(message);
        result.setCode(CommonConstants.SUCCESS);
        result.setMsg("reset Vm success");
        return result;
    }

    /**
     * 查询VmwareTools状态
     *
     * @param vmwareId vmwareId
     * @param vmId 虚拟机ID
     * @return RestResult<Boolean> vmware tools status
     */
    @ApiOperation("查询VmwareTools状态")
    @GetMapping("/v1/{vmwareId}/vms/{vmId}/vmware-tools-status")
    public RestResult<String> getVmwareToolsStatus(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<String> result = new RestResult<>();
        String message = "";
        try {
            message = vmServiceImpl.getVmwareToolsStatus(vmwareId, vmId);
            if (!message.equals("TOOLS_NOT_INSTALLED")) {
                result.setCode(CommonConstants.SUCCESS);
                result.setMsg("get VmwareTools Status success");
            } else {
                result.setCode(ErrorCode.FAILED_CODE);
                result.setMsg("get VmwareTools Status failed");
            }
            result.setData(message);
        } catch (CustomException e) {
            result.setData(e.getMessage());
            result.setCode(e.getErrorCode());
            result.setMsg("get VmwareTools Status failed");
            return result;
        } catch (Exception exception) {
            LOGGER.error("get VmwareTools Status failed .exception: ", exception);
            result.setData(exception.getMessage());
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg("get VmwareTools Status failed");
            return result;
        }
        return result;
    }

    /**
     * Mark as template rest result.
     *
     * @param vmwareId the vmware id
     * @param vmId the vm id
     * @return the rest result
     */
    @ApiOperation("虚拟机转换为模板")
    @PostMapping("/v1/{vmwareId}/vms/{vmId}/action/markAsTemplate")
    public RestResult<Void> markAsTemplate(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<Void> result = new RestResult<>();
        try {
            vmServiceImpl.markAsTemplate(vmwareId, vmId);
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("markAsTemplate success");
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            return result;
        } catch (Exception exception) {
            LOGGER.error("markAsTemplate .exception:", exception);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg("markAsTemplate failed");
            return result;
        }
        return result;
    }

    /**
     * 虚拟机挂载VmwareTools工具
     *
     * @param vmwareId vmwareId
     * @param vmId 虚拟机ID
     * @return RestResult<Boolean> rest result
     */
    @ApiOperation("虚拟机挂载VmwareTools工具")
    @PostMapping("/v1/{vmwareId}/vms/{vmId}/mountToolsInstaller")
    public RestResult<String> mountVMToolsInstaller(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("vmId")
        @ApiParam(value = "虚拟机ID", example = "vm-1470", required = true)
            String vmId) {
        RestResult<String> result = new RestResult<>();
        try {
            vmServiceImpl.mountVMToolsInstaller(vmwareId, vmId);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            return result;
        } catch (Exception e) {
            LOGGER.error("mount VMToolsInstaller failed. exception:", e);
            result.setData(e.getMessage());
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg("mount VMToolsInstaller failed.");
            return result;
        }
        result.setCode(CommonConstants.SUCCESS);
        result.setMsg("mount VMToolsInstaller success");
        return result;
    }

    /**
     * Query vm overall status rest result.
     *
     * @param vmwareId the vmware id
     * @return the rest result
     */
    @ApiOperation("查询全部虚拟机状态")
    @GetMapping("/v1/{vmwareId}/vms/overall-status")
    public RestResult<List<VmOverStatus>> queryVmOverallStatus(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<VmOverStatus>> result = new RestResult<>();
        List<VmOverStatus> vmOverStatus;
        try {
            vmOverStatus = vmServiceImpl.getAllOverallStatus(vmwareId);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            return result;
        } catch (Exception exception) {
            LOGGER.error("query Vm Overall Status failed .exception:", exception);
            result.setData(null);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(exception.getMessage());
            return result;
        }
        result.setData(vmOverStatus);
        result.setCode(CommonConstants.SUCCESS);
        result.setMsg("query Vm Overall Status success");
        return result;
    }

    /**
     * Query system type rest result.
     *
     * @param vmwareId the vmware id
     * @return the rest result
     */
    @ApiOperation("查询系统类型")
    @GetMapping("/v1/{vmwareId}/vms/system-types")
    public RestResult<List<VcenterType>> querySystemType(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<VcenterType>> result = new RestResult<>();
        List<VcenterType> vcenterTypes;
        try {
            vcenterTypes = vmServiceImpl.getSystemType(vmwareId);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            return result;
        } catch (Exception exception) {
            LOGGER.error("querySystemType failed .exception:", exception);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(exception.getMessage());
            return result;
        }
        result.setData(vcenterTypes);
        result.setCode(CommonConstants.SUCCESS);
        result.setMsg("querySystemType success");
        return result;
    }

    /**
     * 开启VM
     *
     * @param vmwareId the vmware id
     * @param vmNameRequest the vm name request
     * @return 上电结果 rest result
     */
    @ApiOperation("根据名称虚拟机上电")
    @PostMapping("/v1/{vmwareId}/vms/action/powerOnByName")
    public RestResult<VmStatus> powerOn(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @Valid VmNameRequest vmNameRequest) {
        RestResult<VmStatus> result = new RestResult<>();
        VmStatus status;
        try {
            String vmName = vmNameRequest.getVmName();
            status = vmServiceImpl.powerOn(vmwareId, vmName);
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("power On Vm success");
            result.setData(status);
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 关闭VM
     *
     * @param vmwareId the vmware id
     * @param vmNameRequest the vm name request
     * @return 下电结果 rest result
     */
    @ApiOperation("根据名称虚拟机下电")
    @PostMapping("/v1/{vmwareId}/vms/action/powerOffByName")
    public RestResult<VmStatus> powerOff(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @Valid VmNameRequest vmNameRequest) {
        RestResult<VmStatus> result = new RestResult<>();
        VmStatus status;
        try {
            String vmName = vmNameRequest.getVmName();
            status = vmServiceImpl.powerOff(vmwareId, vmName);
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("power Off Vm success");
            result.setData(status);
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 创建快照
     *
     * @param vmwareId the vmware id
     * @param snapshotRequest the snapshot request
     * @return 创建快照结果 rest result
     */
    @ApiOperation("创建快照")
    @PostMapping("/v1/{vmwareId}/vms/action/createSnapshot")
    public RestResult<String> createSnapshot(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @Valid SnapshotRequest snapshotRequest) {
        RestResult<String> result = new RestResult<>();
        String name;
        try {
            name = vmServiceImpl.createSnapshot(vmwareId, snapshotRequest);
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("create snapshot success");
            result.setData(name);
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (ApplicationException exception) {
            LOGGER.error("ApplicationException is ", exception);
            result.setCode(exception.getCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            LOGGER.error("", e);
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * Gets host vms.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @return the host vms
     */
    @ApiOperation("查询主机的虚拟机列表")
    @GetMapping("/v1/{vmwareId}/hosts/{hostId}/vms")
    public RestResult<List<VMVo>> getHostVms(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("hostId")
        @ApiParam(value = "主机ID", example = "host-10", required = true)
            String hostId) {
        RestResult<List<VMVo>> result = new RestResult<>();
        try {
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("query Host success");
            result.setData(vmServiceImpl.getHostVms(vmwareId, hostId));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("queryVmStatus exception :", e);
        }
        return result;
    }

    /**
     * Gets vms.
     *
     * @param vmwareId the vmware id
     * @return the vms
     */
    @ApiOperation("查询虚拟机列表")
    @GetMapping("/v1/{vmwareId}/vms")
    public RestResult<List<VMVo>> getVms(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<VMVo>> result = new RestResult<>();
        try {
            result.setCode(CommonConstants.SUCCESS);
            result.setMsg("query Host success");
            result.setData(vmServiceImpl.getVms(vmwareId));
        } catch (CustomException exception) {
            result.setCode(exception.getErrorCode());
            result.setMsg(exception.getMessage());
        } catch (Exception e) {
            result.setCode(ErrorCode.FAILED_CODE);
            result.setMsg(e.getMessage());
            LOGGER.error("queryVmStatus exception : ", e);
        }
        return result;
    }
}