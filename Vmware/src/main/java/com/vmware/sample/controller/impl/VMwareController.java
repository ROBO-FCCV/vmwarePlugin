/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller.impl;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.VMware;
import com.vmware.sample.service.VMwareService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * VMware controller
 *
 * @since 2020-09-16
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VMwareController {
    private final VMwareService vMwareService;

    /**
     * Add vmware
     *
     * @param vMware vmware
     * @return result
     */
    @PostMapping(value = "/vmware", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResult<String> add(@NotNull @RequestBody VMware vMware) {
        return RestResult.success(vMwareService.add(vMware));
    }

    /**
     * List vmware
     *
     * @return result
     */
    @GetMapping("/vmware")
    public RestResult<List<VMware>> list() {
        return RestResult.success(vMwareService.list());
    }

    /**
     * Get vmware
     *
     * @param vmwareId vmware id
     * @return result
     */
    @GetMapping("/vmware/{vmwareId}")
    public RestResult<VMware> get(@NotBlank @PathVariable @Pattern(regexp = Constants.ID_REGEXP) String vmwareId) {
        return RestResult.success(vMwareService.get(vmwareId));
    }

    /**
     * Delete vmware
     *
     * @param vmwareId vmware id
     * @return result
     */
    @DeleteMapping("/vmware/{vmwareId}")
    public RestResult<String> del(@NotBlank @Pattern(regexp = Constants.ID_REGEXP) @PathVariable String vmwareId) {
        return RestResult.success(vMwareService.del(vmwareId));
    }

    /**
     * Modify vmware
     *
     * @param vmwareId vmware id
     * @param vMware vmware
     * @return result
     */
    @PutMapping(value = "/vmware/{vmwareId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestResult<String> modify(@NotBlank @Pattern(regexp = Constants.ID_REGEXP) @PathVariable String vmwareId,
        @NotNull @Valid @RequestBody VMware vMware) {
        return RestResult.success(vMwareService.modify(vmwareId, vMware));
    }
}
