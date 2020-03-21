/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.host;

import cc.plugin.vmware.model.vo.response.datastore.Datastore;
import cc.plugin.vmware.model.vo.response.datastore.IsoEnty;
import cc.plugin.vmware.model.vo.response.vm.VMVo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * The type Host.
 *
 * @since 2019 -10-15
 */
public class Host {
    @ApiModelProperty(value = "主机名称", example = "host-9", required = true)
    private String name;
    @ApiModelProperty(value = "主机IP", example = "192.0.2.0", required = true)
    private String ip;
    @ApiModelProperty(value = "主机标识", example = "host-9", required = true)
    private String moId;
    @ApiModelProperty(value = "主机状态", example = "Normal", required = true)
    private String esxStatus;
    @ApiModelProperty(value = "数据存储列表", required = true)
    private List<IsoEnty> dataStoreList;
    @ApiModelProperty(value = "虚拟机列表", required = true)
    private List<VMVo> vmList;
    @ApiModelProperty(value = "数据存储列表", required = true)
    private List<Datastore> datastoreLst;

    /**
     * Gets datastore lst.
     *
     * @return the datastore lst
     */
    public List<Datastore> getDatastoreLst() {
        return datastoreLst;
    }

    /**
     * Sets datastore lst.
     *
     * @param datastoreLst the datastore lst
     */
    public void setDatastoreLst(List<Datastore> datastoreLst) {
        this.datastoreLst = datastoreLst;
    }

    /**
     * Gets esx status.
     *
     * @return the esx status
     */
    public String getEsxStatus() {
        return esxStatus;
    }

    /**
     * Sets esx status.
     *
     * @param esxStatus the esx status
     */
    public void setEsxStatus(String esxStatus) {
        this.esxStatus = esxStatus;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets data store list.
     *
     * @return the data store list
     */
    public List<IsoEnty> getDataStoreList() {
        return dataStoreList;
    }

    /**
     * Sets data store list.
     *
     * @param dataStoreList the data store list
     */
    public void setDataStoreList(List<IsoEnty> dataStoreList) {
        this.dataStoreList = dataStoreList;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets mo id.
     *
     * @return the mo id
     */
    public String getMoId() {
        return moId;
    }

    /**
     * Sets mo id.
     *
     * @param moId the mo id
     */
    public void setMoId(String moId) {
        this.moId = moId;
    }

    /**
     * Gets vm list.
     *
     * @return the vm list
     */
    public List<VMVo> getVmList() {
        return vmList;
    }

    /**
     * Sets vm list.
     *
     * @param vmList the vm list
     */
    public void setVmList(List<VMVo> vmList) {
        this.vmList = vmList;
    }

}
