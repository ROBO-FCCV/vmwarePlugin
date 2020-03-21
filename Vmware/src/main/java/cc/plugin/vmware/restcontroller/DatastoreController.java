/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.restcontroller;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.common.RestResult;
import cc.plugin.vmware.model.vo.request.datastore.DatastoreInfo;
import cc.plugin.vmware.model.vo.response.datastore.Datastore;
import cc.plugin.vmware.model.vo.response.datastore.DatastoreResponse;
import cc.plugin.vmware.model.vo.response.datastore.DatastoreVo;
import cc.plugin.vmware.service.DatastoreService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 功能描述
 *
 * @since 2019 -09-10
 */
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@Validated
public class DatastoreController {
    private static final Logger logger = LoggerFactory.getLogger(DatastoreController.class);

    /**
     * The Datastore service.
     */
    @Autowired
    DatastoreService datastoreService;

    /**
     * 创建datastore
     *
     * @param vmwareId the vmware id
     * @param datastoreInfo datastore实体
     * @return 是否创建成功 rest result
     */
    @ApiOperation("创建数据存储")
    @PostMapping("/v1/{vmwareId}/datastores")
    public RestResult<String> createDatastore(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestBody
        @ApiParam(value = "创建数据存储入参")
        @Valid DatastoreInfo datastoreInfo) {
        RestResult<String> result = new RestResult<>();
        try {
            String createdResult = datastoreService.createDatastore(vmwareId, datastoreInfo);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware createDatastore successfully");
            result.setData(createdResult);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware createDatastore failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 删除数据存储
     *
     * @param vmwareId the vmware id
     * @param datastoreId 数据存储ID
     * @return 删除结果 rest result
     */
    @ApiOperation("删除数据存储")
    @DeleteMapping("/v1/{vmwareId}/datastores/{datastoreId}")
    public RestResult<String> deleteDatastore(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("datastoreId")
        @ApiParam(value = "Datastore ID", example = "datastore-348", required = true)
            String datastoreId) {
        RestResult<String> result = new RestResult<>();
        try {
            String status = datastoreService.deleteDatastore(vmwareId, datastoreId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware deleteDatastore successfully");
            result.setData(status);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware deleteDatastore failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * 查询共享存储
     *
     * @param vmwareId the vmware id
     * @return 共享存储信息列表 vmware shared storage
     */
    @ApiOperation("查询共享存储")
    @GetMapping("/v1/{vmwareId}/shared-datastores")
    public RestResult<List<DatastoreVo>> getVmwareSharedStorage(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId) {
        RestResult<List<DatastoreVo>> result = new RestResult<>();
        try {
            List<DatastoreVo> datastores = datastoreService.getVmwareSharedStorage(vmwareId);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getVmwareSharedStorage successfully");
            result.setData(datastores);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getVmwareSharedStorage failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets datastores by datacenter or host name.
     *
     * @param vmwareId the vmware id
     * @param datacenterName the datacenter name
     * @param hostName the host name
     * @return the datastores by datacenter or host name
     */
    @ApiOperation("根据数据中心与主机名称获取数据存储列表")
    @GetMapping("/v1/{vmwareId}/datastores")
    public RestResult<List<DatastoreResponse>> getDatastoresByDatacenterOrHostName(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @RequestParam("datacenterName")
        @ApiParam(value = "数据中心名称", example = "datastore", required = true)
            String datacenterName,
        @RequestParam("hostName")
        @ApiParam(value = "主机名称", example = "192.0.2.0", required = true)
            String hostName) {
        RestResult<List<DatastoreResponse>> result = new RestResult<>();
        try {
            List<DatastoreResponse> datastores = datastoreService.getDatastoresByDatacenterOrHostName(vmwareId,
                datacenterName, hostName);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getDatastoresByDatacenterOrHostName successfully");
            result.setData(datastores);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getDatastoresByDatacenterOrHostName failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }

    /**
     * Gets datastores by filter type.
     *
     * @param vmwareId the vmware id
     * @param hostId the host id
     * @param filterType the filter type
     * @return the datastores by filter type
     */
    @ApiOperation("查询数据存储列表")
    @GetMapping("/v1/{vmwareId}/hosts/{hostId}/datastores")
    public RestResult<List<Datastore>> getDatastoresByFilterType(
        @PathVariable("vmwareId")
        @ApiParam(value = "Vmware ID", example = "f156b5da25024452b3d7ba8bd1022698", required = true)
        @Pattern(regexp = Constant.ID_REGEXP) String vmwareId,
        @PathVariable("hostId")
        @ApiParam(value = "主机 ID", example = "host-9", required = true)
            String hostId,
        @RequestParam("filterType")
        @ApiParam(value = "数据中心名称", example = "0集群,1主机", required = true)
            String filterType) {
        RestResult<List<Datastore>> result = new RestResult<>();
        try {
            List<Datastore> datastores = datastoreService.getDatastoreByFilterType(vmwareId, hostId, filterType);
            result.setCode(ErrorCode.SUCCESS_CODE);
            result.setMsg("Vmware getDatastoresByDatacenterOrHostName successfully");
            result.setData(datastores);
        } catch (CustomException e) {
            result.setCode(e.getErrorCode());
            result.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("Vmware getDatastoresByDatacenterOrHostName failed", e);
            result.setCode(ErrorCode.SYSTEM_ERROR_CODE);
            result.setMsg(ErrorCode.SYSTEM_ERROR_MSG);
        }
        return result;
    }
}
