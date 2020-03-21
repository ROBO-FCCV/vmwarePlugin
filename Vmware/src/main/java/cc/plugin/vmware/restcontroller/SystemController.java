/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.request.PasswordVo;
import cc.plugin.vmware.model.vo.request.VmwareInfo;
import cc.plugin.vmware.model.vo.response.ValidationResponse;
import cc.plugin.vmware.model.vo.response.VmwareInstance;
import cc.plugin.vmware.token.TokenCache;
import cc.plugin.vmware.util.CommonUtil;
import cc.plugin.vmware.util.Encoder;
import cc.plugin.vmware.util.JWTUtil;
import cc.plugin.vmware.util.WebUtil;

import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录
 *
 * @since 2019 -09-09
 */
@RequestMapping(value = "", produces = {"application/json;charset=UTF-8"})
@RestController
@Validated
public class SystemController {

    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    /**
     * The Token cache.
     */
    @Autowired
    TokenCache tokenCache;

    /**
     * The Common util.
     */
    @Autowired
    CommonUtil commonUtil;

    /**
     * 登录
     *
     * @param request the request
     * @param response the response
     * @return the rest result
     */
    @ApiOperation("登录")
    @PostMapping(value = "/login")
    public RestResult<String> login(HttpServletRequest request, HttpServletResponse response) {
        RestResult<String> result = new RestResult<>();
        try {
            String username = request.getHeader("username");
            String token = JWTUtil.generateJwtToken(username);
            if (StringUtils.isEmpty(token)) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
                result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
            } else {
                // token放到内存中
                tokenCache.dealWithToken(response, WebUtil.getClientIp(request), token);
                result.setCode(ErrorCode.SUCCESS_CODE);
                result.setMsg(ErrorCode.SUCCESS_MSG);
                result.setData(token);
            }
        } catch (CustomException e) {
            if (StringUtils.equals(e.getErrorCode(), ErrorCode.MAXIMUM_CONNECTION_CODE)) {
                logger.warn("Valid tokens have exceeded maximum");
                String existingToken = tokenCache.getValidToken(WebUtil.getClientIp(request));
                result.setCode(ErrorCode.SUCCESS_CODE);
                result.setMsg(ErrorCode.SUCCESS_MSG);
                result.setData(existingToken);
            } else {
                result.setCode(e.getErrorCode());
                result.setMsg(e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Login failed", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets vmware instances.
     *
     * @return the vmware instances
     */
    @ApiOperation("获取Vmware所有实例")
    @GetMapping(value = "/v1/instance")
    public RestResult<List<VmwareInstance>> getVmwareInstances() {
        RestResult<List<VmwareInstance>> result = new RestResult<>();
        try {
            List<VmwareInstance> instances = VmwareInstance.getVmwareInstances();
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getVmwareInstances successfully");
            result.setData(instances);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getVmwareInstances failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Validate vmware info rest result.
     *
     * @param vmwareInfo the vmware info
     * @return the rest result
     */
    @ApiOperation("校验Vmware信息合法性")
    @PostMapping(value = "/v1/validateVmwareInfo")
    public RestResult<ValidationResponse> validateVmwareInfo(@RequestBody
        VmwareInfo vmwareInfo) {
        RestResult<ValidationResponse> result = new RestResult<>();
        try {
            ValidationResponse instances = commonUtil.validateVmwareInfo(vmwareInfo);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware validateVmwareInfo successfully");
            result.setData(instances);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
            result.setData(new ValidationResponse().setValidationResult(false));
        } catch (Exception e) {
            logger.error("Vmware validateVmwareInfo failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
            result.setData(new ValidationResponse().setValidationResult(false));
        }
        return result;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    @ApiOperation("查询版本号")
    @GetMapping(value = "/version")
    public RestResult<String> getVersion() {
        RestResult<String> result = new RestResult<>();
        try {
            String version = commonUtil.getVersion();
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getVersion successfully");
            result.setData(version);
        } catch (Exception e) {
            logger.error("Vmware getVersion failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Encrypt rest result.
     *
     * @param passwordVo the password vo
     * @return the rest result
     */
    @ApiOperation("加密")
    @PostMapping(value = "/encode")
    public RestResult<String> encrypt(@RequestBody
        PasswordVo passwordVo) {
        RestResult<String> result = new RestResult<>();
        try {
            Encoder encoder = new Encoder();
            String password = encoder.encode(passwordVo.getPassword());
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Encode successfully");
            result.setData(password);
        } catch (Exception e) {
            logger.error("Encode failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}
