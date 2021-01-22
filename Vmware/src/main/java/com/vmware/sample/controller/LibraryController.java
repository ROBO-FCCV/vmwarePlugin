/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.controller;

import com.vmware.sample.consts.Constants;
import com.vmware.sample.model.RestResult;
import com.vmware.sample.model.library.LibraryFindSpec;
import com.vmware.sample.model.library.LocalLibraryModel;

import com.vmware.content.LibraryModel;

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
 * Library controller
 *
 * @since 2020-10-13
 */
@Validated
public interface LibraryController {
    /**
     * Find library
     *
     * @param vmwareId vmware id
     * @param libraryFindSpec library find spec
     * @return library ids
     */
    @PostMapping("/{vmwareId}/library/action/query")
    RestResult<List<String>> find(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid LibraryFindSpec libraryFindSpec);

    /**
     * Get library
     *
     * @param vmwareId vmware id
     * @param libraryId library id
     * @return local library
     */
    @GetMapping("/{vmwareId}/local-library/{libraryId}")
    RestResult<LibraryModel> get(@PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @PathVariable @NotBlank String libraryId);

    @PostMapping("/{vmwareId}/local-library")
    RestResult<String> createLocalLibrary(
        @PathVariable @NotBlank @Pattern(regexp = Constants.ID_REGEXP) String vmwareId,
        @RequestBody @Valid LocalLibraryModel localLibraryModel);
}
