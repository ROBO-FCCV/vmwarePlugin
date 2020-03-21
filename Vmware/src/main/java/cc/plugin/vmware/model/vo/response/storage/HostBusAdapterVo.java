/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.response.storage;

import java.util.List;

/**
 * 主机总线适配器Vo
 *
 * @since 2019 -09-16
 */
public class HostBusAdapterVo {
    private List<HostSerialAttachedHba> hostSerialAttachedHbas;
    private List<HostFibreChannelHba> hostFibreChannelHbas;
    private List<HostBlockHba> hostBlockHbas;
    private List<HostInternetScsiHba> hostInternetScsiHbas;

    /**
     * Instantiates a new Host bus adapter vo.
     */
    public HostBusAdapterVo() {
    }

    /**
     * Gets host serial attached hbas.
     *
     * @return the host serial attached hbas
     */
    public List<HostSerialAttachedHba> getHostSerialAttachedHbas() {
        return hostSerialAttachedHbas;
    }

    /**
     * Sets host serial attached hbas.
     *
     * @param hostSerialAttachedHbas the host serial attached hbas
     * @return the host serial attached hbas
     */
    public HostBusAdapterVo setHostSerialAttachedHbas(List<HostSerialAttachedHba> hostSerialAttachedHbas) {
        this.hostSerialAttachedHbas = hostSerialAttachedHbas;
        return this;
    }

    /**
     * Gets host fibre channel hbas.
     *
     * @return the host fibre channel hbas
     */
    public List<HostFibreChannelHba> getHostFibreChannelHbas() {
        return hostFibreChannelHbas;
    }

    /**
     * Sets host fibre channel hbas.
     *
     * @param hostFibreChannelHbas the host fibre channel hbas
     * @return the host fibre channel hbas
     */
    public HostBusAdapterVo setHostFibreChannelHbas(List<HostFibreChannelHba> hostFibreChannelHbas) {
        this.hostFibreChannelHbas = hostFibreChannelHbas;
        return this;
    }

    /**
     * Gets host block hbas.
     *
     * @return the host block hbas
     */
    public List<HostBlockHba> getHostBlockHbas() {
        return hostBlockHbas;
    }

    /**
     * Sets host block hbas.
     *
     * @param hostBlockHbas the host block hbas
     * @return the host block hbas
     */
    public HostBusAdapterVo setHostBlockHbas(List<HostBlockHba> hostBlockHbas) {
        this.hostBlockHbas = hostBlockHbas;
        return this;
    }

    /**
     * Gets host internet scsi hbas.
     *
     * @return the host internet scsi hbas
     */
    public List<HostInternetScsiHba> getHostInternetScsiHbas() {
        return hostInternetScsiHbas;
    }

    /**
     * Sets host internet scsi hbas.
     *
     * @param hostInternetScsiHbas the host internet scsi hbas
     * @return the host internet scsi hbas
     */
    public HostBusAdapterVo setHostInternetScsiHbas(List<HostInternetScsiHba> hostInternetScsiHbas) {
        this.hostInternetScsiHbas = hostInternetScsiHbas;
        return this;
    }
}
