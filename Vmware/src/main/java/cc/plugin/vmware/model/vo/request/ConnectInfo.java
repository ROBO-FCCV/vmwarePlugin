/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.model.vo.request;

import cc.plugin.vmware.connection.ExtendedAppUtil;
import cc.plugin.vmware.connection.ServiceUtil;

import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

/**
 * 功能描述
 *
 * @since 2019 -09-26
 */
public class ConnectInfo {
    private ExtendedAppUtil ecb;
    private ServiceContent serviceContent;
    private ServiceUtil svc;
    private VimPortType vimPortType;

    /**
     * Instantiates a new Connect info.
     *
     * @param ecb the ecb
     * @param serviceContent the service content
     * @param svc the svc
     * @param vimPortType the vim port type
     */
    public ConnectInfo(ExtendedAppUtil ecb, ServiceContent serviceContent, ServiceUtil svc, VimPortType vimPortType) {
        this.ecb = ecb;
        this.serviceContent = serviceContent;
        this.svc = svc;
        this.vimPortType = vimPortType;
    }

    /**
     * Gets ecb.
     *
     * @return the ecb
     */
    public ExtendedAppUtil getEcb() {
        return ecb;
    }

    /**
     * Sets ecb.
     *
     * @param ecb the ecb
     */
    public void setEcb(ExtendedAppUtil ecb) {
        this.ecb = ecb;
    }

    /**
     * Gets service content.
     *
     * @return the service content
     */
    public ServiceContent getServiceContent() {
        return serviceContent;
    }

    /**
     * Sets service content.
     *
     * @param serviceContent the service content
     */
    public void setServiceContent(ServiceContent serviceContent) {
        this.serviceContent = serviceContent;
    }

    /**
     * Gets svc.
     *
     * @return the svc
     */
    public ServiceUtil getSvc() {
        return svc;
    }

    /**
     * Sets svc.
     *
     * @param svc the svc
     */
    public void setSvc(ServiceUtil svc) {
        this.svc = svc;
    }

    /**
     * Gets vim port type.
     *
     * @return the vim port type
     */
    public VimPortType getVimPortType() {
        return vimPortType;
    }

    /**
     * Sets vim port type.
     *
     * @param vimPortType the vim port type
     */
    public void setVimPortType(VimPortType vimPortType) {
        this.vimPortType = vimPortType;
    }
}
