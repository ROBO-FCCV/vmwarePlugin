/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.file.UpSessionFileCreate;

import com.vmware.content.library.item.updatesession.FileTypes;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Library item up session file controller
 *
 * @since 2020-10-15
 */
@Validated
public interface LibraryItemUpSessionFileController {
    /**
     * Add up session file
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @param upSessionFileCreate up session file create spec
     * @return file info
     */
    @PostMapping("/{vmwareId}/library/item/up-session/file/{sessionId}/action/add")
    RestResult<FileTypes.Info> add(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId, @RequestBody @Valid UpSessionFileCreate upSessionFileCreate);

    /**
     * Get file info
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @param fileName file name
     * @return up session file info
     */
    @PostMapping("/{vmwareId}/library/item/up-session/file/{sessionId}/action/query")
    RestResult<FileTypes.Info> get(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId, @RequestBody @NotBlank String fileName);

    /**
     * Validate file
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @return validate result
     */
    @PostMapping("/{vmwareId}/library/item/up-session/file/{sessionId}/action/validate")
    RestResult<FileTypes.ValidationResult> validate(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId);

    /**
     * List file
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @return files
     */
    @GetMapping("/{vmwareId}/library/item/up-session/file/{sessionId}")
    RestResult<List<FileTypes.Info>> list(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId);
}
