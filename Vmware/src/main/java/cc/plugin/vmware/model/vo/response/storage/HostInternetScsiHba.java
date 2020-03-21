/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

/**
 * 主机总线适配器
 *
 * @since 2019 -09-16
 */
public class HostInternetScsiHba extends HostBusAdapter {
    private String iScsiName;

    /**
     * Instantiates a new Host internet scsi hba.
     */
    public HostInternetScsiHba() {
    }

    /**
     * Gets scsi name.
     *
     * @return the scsi name
     */
    public String getiScsiName() {
        return iScsiName;
    }

    /**
     * Sets scsi name.
     *
     * @param iScsiName the scsi name
     * @return the scsi name
     */
    public HostInternetScsiHba setiScsiName(String iScsiName) {
        this.iScsiName = iScsiName;
        return this;
    }
}
