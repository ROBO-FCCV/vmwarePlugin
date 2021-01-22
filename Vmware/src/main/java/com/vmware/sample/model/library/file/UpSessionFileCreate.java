/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library.file;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Up session file create spec
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class UpSessionFileCreate {
    @NotBlank
    @Length(max = 80)
    private String name;
    @NotBlank
    @Pattern(regexp = "NONE|PUSH|PULL")
    private String sourceType;
    @Valid
    private TransferEndpointCreate sourceEndpoint;
    @Valid
    private ChecksumInfoCreate checksumInfo;
}
