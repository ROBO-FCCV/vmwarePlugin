/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.vm;

import lombok.Getter;
import lombok.Setter;

/**
 * task status
 *
 * @since 2020-09-18
 */
@Setter
@Getter
public class TaskStatus {
    /**
     * task id
     */
    private String taskId;

    /**
     * status
     */
    private String status;

}
