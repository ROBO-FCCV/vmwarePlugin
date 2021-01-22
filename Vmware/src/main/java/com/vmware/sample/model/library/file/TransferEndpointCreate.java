/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.library.file;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;

import javax.validation.constraints.NotNull;

/**
 * Transfer end point create
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class TransferEndpointCreate {
    @NotNull
    private URI uri;
    private String sslCertificateThumbprint;
}
