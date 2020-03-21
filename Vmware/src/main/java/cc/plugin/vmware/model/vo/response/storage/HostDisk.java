/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 硬盘
 *
 * @since 2019 -09-16
 */
public class HostDisk {

    @ApiModelProperty(value = "是否为SSD:true,false", example = "true", required = true)
    private boolean ssd;
    @ApiModelProperty(value = "显示名称", example = "Local AVAGO Disk (naa.6ac751de91b7b00024cc07e60bfe84e6)",
        required = true)
    private String displayName;
    @ApiModelProperty(value = "是否为本地硬盘:true,false", example = "true", required = true)
    private boolean localDisk;
    @ApiModelProperty(value = "是否支持vStorage:true,false", example = "vStorageUnsupported", required = true)
    private String vStorageSupport;
    @ApiModelProperty(value = "是否为SSD:true,false", example = "/vmfs/devices/disks/naa.6ac751de91b7b00024cc07e60bfe84e6",
        required = true)
    private String deviceName;
    @ApiModelProperty(value = "UUID", example = "02000000006ac751de91b7b00024cc07e60bfe84e6415641474f20",
        required = true)
    private String uuid;
    @ApiModelProperty(value = "容量", required = true)
    private HostDiskDimensionsLba capacity;
    @ApiModelProperty(value = "硬盘路径", example = "/vmfs/devices/disks/naa.6ac751de91b7b00024cc07e60bfe84e6",
        required = true)
    private String devicePath;
    @ApiModelProperty(value = "设备厂商", example = "AVAGO", required = true)
    private String vendor;
    @ApiModelProperty(value = "设备型号", example = "AVAGO", required = true)
    private String model;
    @ApiModelProperty(value = "连接标识符",
        example = "key-vim.host" + ".ScsiDisk-02000000006ac751de91b7b00024cc07e60bfe84e6415641474f20", required = true)
    private String key;
    @ApiModelProperty(value = "硬盘绝对名称", example = "naa.6ac751de91b7b00024cc07e60bfe84e6", required = true)
    private String canonicalName;
    @ApiModelProperty(value = "设备类型", example = "disk", required = true)
    private String deviceType;
    @ApiModelProperty(value = "LUN类型", example = "disk", required = true)
    private String lunType;
    @ApiModelProperty(value = "序列号", example = "unavailable", required = true)
    private String serialNumber;
    @ApiModelProperty(value = "SCSI硬盘类型", example = "emulated512", required = true)
    private String scsiDiskType;
    @ApiModelProperty(value = "SCSI等级", example = "5", required = true)
    private int scsiLevel;
    @ApiModelProperty(value = "LUN描述符", required = true)
    private List<Descriptor> descriptor;

    /**
     * Is ssd boolean.
     *
     * @return the boolean
     */
    public boolean isSsd() {
        return ssd;
    }

    /**
     * Sets ssd.
     *
     * @param ssd the ssd
     * @return the ssd
     */
    public HostDisk setSsd(boolean ssd) {
        this.ssd = ssd;
        return this;
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets display name.
     *
     * @param displayName the display name
     * @return the display name
     */
    public HostDisk setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Is local disk boolean.
     *
     * @return the boolean
     */
    public boolean isLocalDisk() {
        return localDisk;
    }

    /**
     * Sets local disk.
     *
     * @param localDisk the local disk
     * @return the local disk
     */
    public HostDisk setLocalDisk(boolean localDisk) {
        this.localDisk = localDisk;
        return this;
    }

    /**
     * Gets storage support.
     *
     * @return the storage support
     */
    public String getvStorageSupport() {
        return vStorageSupport;
    }

    /**
     * Sets storage support.
     *
     * @param vStorageSupport the v storage support
     * @return the storage support
     */
    public HostDisk setvStorageSupport(String vStorageSupport) {
        this.vStorageSupport = vStorageSupport;
        return this;
    }

    /**
     * Gets device name.
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets device name.
     *
     * @param deviceName the device name
     * @return the device name
     */
    public HostDisk setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets uuid.
     *
     * @param uuid the uuid
     * @return the uuid
     */
    public HostDisk setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * Gets capacity.
     *
     * @return the capacity
     */
    public HostDiskDimensionsLba getCapacity() {
        return capacity;
    }

    /**
     * Sets capacity.
     *
     * @param capacity the capacity
     * @return the capacity
     */
    public HostDisk setCapacity(HostDiskDimensionsLba capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Gets device path.
     *
     * @return the device path
     */
    public String getDevicePath() {
        return devicePath;
    }

    /**
     * Sets device path.
     *
     * @param devicePath the device path
     * @return the device path
     */
    public HostDisk setDevicePath(String devicePath) {
        this.devicePath = devicePath;
        return this;
    }

    /**
     * Gets vendor.
     *
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets vendor.
     *
     * @param vendor the vendor
     * @return the vendor
     */
    public HostDisk setVendor(String vendor) {
        this.vendor = vendor;
        return this;
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
     * @return the model
     */
    public HostDisk setModel(String model) {
        this.model = model;
        return this;
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
     * @return the key
     */
    public HostDisk setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Gets canonical name.
     *
     * @return the canonical name
     */
    public String getCanonicalName() {
        return canonicalName;
    }

    /**
     * Sets canonical name.
     *
     * @param canonicalName the canonical name
     * @return the canonical name
     */
    public HostDisk setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
        return this;
    }

    /**
     * Gets device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets device type.
     *
     * @param deviceType the device type
     * @return the device type
     */
    public HostDisk setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    /**
     * Gets lun type.
     *
     * @return the lun type
     */
    public String getLunType() {
        return lunType;
    }

    /**
     * Sets lun type.
     *
     * @param lunType the lun type
     * @return the lun type
     */
    public HostDisk setLunType(String lunType) {
        this.lunType = lunType;
        return this;
    }

    /**
     * Gets serial number.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets serial number.
     *
     * @param serialNumber the serial number
     * @return the serial number
     */
    public HostDisk setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    /**
     * Gets scsi disk type.
     *
     * @return the scsi disk type
     */
    public String getScsiDiskType() {
        return scsiDiskType;
    }

    /**
     * Sets scsi disk type.
     *
     * @param scsiDiskType the scsi disk type
     * @return the scsi disk type
     */
    public HostDisk setScsiDiskType(String scsiDiskType) {
        this.scsiDiskType = scsiDiskType;
        return this;
    }

    /**
     * Gets scsi level.
     *
     * @return the scsi level
     */
    public int getScsiLevel() {
        return scsiLevel;
    }

    /**
     * Sets scsi level.
     *
     * @param scsiLevel the scsi level
     * @return the scsi level
     */
    public HostDisk setScsiLevel(int scsiLevel) {
        this.scsiLevel = scsiLevel;
        return this;
    }

    /**
     * Gets descriptor.
     *
     * @return the descriptor
     */
    public List<Descriptor> getDescriptor() {
        return descriptor;
    }

    /**
     * Sets descriptor.
     *
     * @param descriptor the descriptor
     * @return the descriptor
     */
    public HostDisk setDescriptor(List<Descriptor> descriptor) {
        this.descriptor = descriptor;
        return this;
    }
}
