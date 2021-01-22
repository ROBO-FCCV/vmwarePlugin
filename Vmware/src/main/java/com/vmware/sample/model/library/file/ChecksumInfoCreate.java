/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library.file;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Check sum info create spec
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class ChecksumInfoCreate {
    @Pattern(regexp = "SHA1|MD5|SHA256|SHA512")
    private String algorithm = "SHA1";
    @NotBlank
    private String checksum;
}
