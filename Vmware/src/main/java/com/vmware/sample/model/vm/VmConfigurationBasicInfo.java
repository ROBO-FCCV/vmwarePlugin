/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.Length;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Create virtual machine basic configuration information
 *
 * @since 2020-10-15
 */
@Getter
@Setter
public class VmConfigurationBasicInfo {
    /**
     * dataCenterId
     */
    private String dataCenterId;

    /**
     * Virtual machine name
     */
    @NotBlank
    @Length(max = 80)
    private String vmName;

    /**
     * host id
     */
    private String hostId;

    /**
     * cluster id
     */
    private String clusterId;

    /**
     * datastore Id
     */
    @NotBlank
    private String datastoreId;

    /**
     * create folder
     */
    private String vmFolder;

    /**
     * cpu information
     */
    @Valid
    private CpuInfo cpuInfo;

    /**
     * memory size
     */
    @Min(1)
    private Long memorySize;

    /**
     * RDM
     */
    private List<RDMInfo> rdms;
}
