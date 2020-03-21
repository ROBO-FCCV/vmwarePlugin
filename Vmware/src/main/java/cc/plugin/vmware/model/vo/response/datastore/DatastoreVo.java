/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.datastore;

import io.swagger.annotations.ApiModelProperty;

/**
 * Vm查询返回结果
 *
 * @since 2019 -09-10
 */
public class DatastoreVo {
    @ApiModelProperty(value = "数据存储连通状态:true,false", example = "true", required = true)
    private boolean accessible;
    @ApiModelProperty(value = "总容量(Byte)", example = "3991903666176", required = true)
    private long capacity;
    @ApiModelProperty(value = "数据存储基本信息", required = true)
    private DatastoreCommonVo datastore;
    @ApiModelProperty(value = "空闲容量(Byte)", example = "710635683840", required = true)
    private long freeSpace;
    @ApiModelProperty(value = "维护模式状态:normal,inMaintenance,enteringMaintenance", example = "normal", required = true)
    private String maintenanceMode;
    @ApiModelProperty(value = "多主机是否可访问:true,false", example = "false", required = true)
    private boolean multipleHostAccess;
    @ApiModelProperty(value = "数据存储名称", example = "datastore1 (4)", required = true)
    private String name;
    @ApiModelProperty(value = "文件系统类型:VMFS,NFS,CIFS,VFAT", example = "VMFS", required = true)
    private String type;
    @ApiModelProperty(value = "未提交大小(byte)", example = "6933392639333", required = true)
    private long uncommitted;
    @ApiModelProperty(value = "数据存储url", example = "ds:///vmfs/volumes/5bdff9e7-a9a6359d-8b29-3c78436c1061/",
        required = true)
    private String url;

    /**
     * Is accessible boolean.
     *
     * @return the boolean
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * Sets accessible.
     *
     * @param accessible the accessible
     * @return the accessible
     */
    public DatastoreVo setAccessible(boolean accessible) {
        this.accessible = accessible;
        return this;
    }

    /**
     * Gets capacity.
     *
     * @return the capacity
     */
    public long getCapacity() {
        return capacity;
    }

    /**
     * Sets capacity.
     *
     * @param capacity the capacity
     * @return the capacity
     */
    public DatastoreVo setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Gets datastore.
     *
     * @return the datastore
     */
    public DatastoreCommonVo getDatastore() {
        return datastore;
    }

    /**
     * Sets datastore.
     *
     * @param datastore the datastore
     * @return the datastore
     */
    public DatastoreVo setDatastore(DatastoreCommonVo datastore) {
        this.datastore = datastore;
        return this;
    }

    /**
     * Gets free space.
     *
     * @return the free space
     */
    public long getFreeSpace() {
        return freeSpace;
    }

    /**
     * Sets free space.
     *
     * @param freeSpace the free space
     * @return the free space
     */
    public DatastoreVo setFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
        return this;
    }

    /**
     * Gets maintenance mode.
     *
     * @return the maintenance mode
     */
    public String getMaintenanceMode() {
        return maintenanceMode;
    }

    /**
     * Sets maintenance mode.
     *
     * @param maintenanceMode the maintenance mode
     * @return the maintenance mode
     */
    public DatastoreVo setMaintenanceMode(String maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        return this;
    }

    /**
     * Is multiple host access boolean.
     *
     * @return the boolean
     */
    public boolean isMultipleHostAccess() {
        return multipleHostAccess;
    }

    /**
     * Sets multiple host access.
     *
     * @param multipleHostAccess the multiple host access
     * @return the multiple host access
     */
    public DatastoreVo setMultipleHostAccess(boolean multipleHostAccess) {
        this.multipleHostAccess = multipleHostAccess;
        return this;
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
     * @return the name
     */
    public DatastoreVo setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @return the type
     */
    public DatastoreVo setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Gets uncommitted.
     *
     * @return the uncommitted
     */
    public long getUncommitted() {
        return uncommitted;
    }

    /**
     * Sets uncommitted.
     *
     * @param uncommitted the uncommitted
     * @return the uncommitted
     */
    public DatastoreVo setUncommitted(long uncommitted) {
        this.uncommitted = uncommitted;
        return this;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     * @return the url
     */
    public DatastoreVo setUrl(String url) {
        this.url = url;
        return this;
    }
}
