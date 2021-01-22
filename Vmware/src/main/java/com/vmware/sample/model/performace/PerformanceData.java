/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.model.performace;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Performance data
 *
 * @since 2020-09-28
 */
@Getter
@Setter
public class PerformanceData {
    private String objectId;
    private List<Indicator> indicators;

    /**
     * Indicator
     *
     * @since 2020-09-29
     */
    @Getter
    @Setter
    public static class Indicator {
        private String metricName;

        private String unit;

        private BigDecimal value;
    }
}
