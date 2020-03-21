/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request;

import cc.plugin.vmware.model.vo.request.host.VcenterEsxi;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @since 2019 -09-16
 */
public class PerformRequest {
    @ApiModelProperty(value = "指标id", example = "cpu_usage：cpu_usage", required = true)
    private Map<String,String> metricIds;

    @ApiModelProperty(value = "主机列表", example = "vcenterEsxis", required = true)
    private List<VcenterEsxi> vcenterEsxis;

    /**
     * Gets metric ids.
     *
     * @return the metric ids
     */
    public Map<String, String> getMetricIds() {
        return metricIds;
    }

    /**
     * Sets metric ids.
     *
     * @param metricIds the metric ids
     */
    public void setMetricIds(Map<String, String> metricIds) {
        this.metricIds = metricIds;
    }

    /**
     * Gets vcenter esxis.
     *
     * @return the vcenter esxis
     */
    public List<VcenterEsxi> getVcenterEsxis() {
        return vcenterEsxis;
    }

    /**
     * Sets vcenter esxis.
     *
     * @param vcenterEsxis the vcenter esxis
     */
    public void setVcenterEsxis(List<VcenterEsxi> vcenterEsxis) {
        this.vcenterEsxis = vcenterEsxis;
    }
}
