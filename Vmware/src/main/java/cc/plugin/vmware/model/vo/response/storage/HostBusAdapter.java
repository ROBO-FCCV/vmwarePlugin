/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

import io.swagger.annotations.ApiModelProperty;

/**
 * 主机总线适配器
 *
 * @since 2019 -09-16
 */
public class HostBusAdapter {

    @ApiModelProperty(value = "总线编号", example = "0", required = true)
    private int bus;
    @ApiModelProperty(value = "总线名称", example = "vmhba0", required = true)
    private String device;
    @ApiModelProperty(value = "驱动名称", example = "vmw_ahci", required = true)
    private String driver;
    @ApiModelProperty(value = "链接标识", example = "key-vim.host.BlockHba-vmhba0", required = true)
    private String key;
    @ApiModelProperty(value = "总线型号", example = "Patsburg 6 Port SATA AHCI Controller", required = true)
    private String model;
    @ApiModelProperty(value = "PCI ID", example = "0000:00:1f.2", required = true)
    private String pci;
    @ApiModelProperty(value = "适配器状态：online,offline,unbound,unknown", example = "online", required = true)
    private String status;

    /**
     * Gets bus.
     *
     * @return the bus
     */
    public int getBus() {
        return bus;
    }

    /**
     * Sets bus.
     *
     * @param bus the bus
     */
    public void setBus(int bus) {
        this.bus = bus;
    }

    /**
     * Gets device.
     *
     * @return the device
     */
    public String getDevice() {
        return device;
    }

    /**
     * Sets device.
     *
     * @param device the device
     */
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * Gets driver.
     *
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets driver.
     *
     * @param driver the driver
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets model.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets model.
     *
     * @param model the model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets pci.
     *
     * @return the pci
     */
    public String getPci() {
        return pci;
    }

    /**
     * Sets pci.
     *
     * @param pci the pci
     */
    public void setPci(String pci) {
        this.pci = pci;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
