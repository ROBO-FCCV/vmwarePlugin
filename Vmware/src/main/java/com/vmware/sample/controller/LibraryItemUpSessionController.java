/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.upsession.UpSessionCreate;

import com.vmware.content.library.item.UpdateSessionModel;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Library item up session controller
 *
 * @since 2020-10-15
 */
@Validated
public interface LibraryItemUpSessionController {
    /**
     * Create up session
     *
     * @param vmwareId vmware id
     * @param updateSession up session create spec
     * @return up session id
     */
    @PostMapping("/{vmwareId}/library/item/up-session")
    RestResult<String> create(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid UpSessionCreate updateSession);

    /**
     * Complete up session
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @return result
     */
    @PostMapping("/{vmwareId}/library/item/up-session/{sessionId}/action/complete")
    RestResult<Void> complete(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId);

    /**
     * Delete up session
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @return result
     */
    @DeleteMapping("/{vmwareId}/library/item/up-session/{sessionId}")
    RestResult<Void> delete(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId);

    /**
     * Cancel up session
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @return result
     */
    @PostMapping("/{vmwareId}/library/item/up-session/{sessionId}/action/cancel")
    RestResult<Void> cancel(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId);

    /**
     * Fail up session
     *
     * @param vmwareId vmware id
     * @param sessionId up session id
     * @param failedMsg failed msg
     * @return result
     */
    @PostMapping("/{vmwareId}/library/item/up-session/{sessionId}/action/fail")
    RestResult<Void> fail(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId, String failedMsg);

    /**
     * Get up session
     *
     * @param vmwareId vmware id
     * @param sessionId session id
     * @return up session
     */
    @GetMapping("/{vmwareId}/library/item/up-session/{sessionId}")
    RestResult<UpdateSessionModel> get(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String sessionId);
}
