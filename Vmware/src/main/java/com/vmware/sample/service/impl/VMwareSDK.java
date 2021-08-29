/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.VMware;
import com.vmware.sample.util.IpAddressCheck;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import javax.xml.ws.BindingProvider;

/**
 * VMware SDK implement
 *
 * @since 2020-09-14
 */
@Slf4j
@Getter
@Setter
public class VMwareSDK extends AbstractVMware {
    private static final ManagedObjectReference SVC_INST_REF = new ManagedObjectReference();
    private VMware vMware;
    private VimService vimService;
    private VimPortType vimPort;
    private ServiceContent serviceContent;
    private UserSession currentSession;

    static {
        SVC_INST_REF.setType("ServiceInstance");
        SVC_INST_REF.setValue("ServiceInstance");
    }

    public VMwareSDK(VMware vMware) {
        this.vMware = vMware;
    }

    @Override
    public synchronized boolean login() {
        boolean reachable = IpAddressCheck.isReachable(vMware.getIp());
        if (!reachable) {
            throw new PluginException(RestCodeEnum.CONNECTION_EXCEPTION);
        }
        URI uri = UriComponentsBuilder.newInstance()
            .host(vMware.getIp())
            .port(vMware.getPort())
            .path("sdk")
            .scheme("https")
            .build()
            .toUri();
        this.vimService = new VimService();
        this.vimPort = vimService.getVimPort();
        Map<String, Object> ctxt = null;
        if (vimPort instanceof BindingProvider) {
            ctxt = ((BindingProvider) vimPort).getRequestContext();
            ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, uri.toString());
            ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        }
        try {
            serviceContent = vimPort.retrieveServiceContent(SVC_INST_REF);
            currentSession = this.vimPort.login(serviceContent.getSessionManager(), vMware.getUsername(),
                String.valueOf(vMware.getPassword()), null);
        } catch (Exception exception) {
            log.error("Login failed with error", SensitiveExceptionUtils.hideSensitiveInfo(exception));
            return false;
        }
        return true;
    }

    @Override
    public void logout() {
        try {
            this.vimPort.logout(serviceContent.getSessionManager());
        } catch (Exception e) {
            log.error("Connect Failed.");
        }
    }

    @Override
    public boolean checkSession() {
        try {
            if (serviceContent == null || currentSession == null) {
                return login();
            }
            boolean sessionIsActive = vimPort.sessionIsActive(serviceContent.getSessionManager(),
                currentSession.getKey(), vMware.getUsername());
            if (sessionIsActive) {
                return true;
            } else {
                log.warn("Session empire.refer to login.");
                return login();
            }
        } catch (Exception e) {
            log.error("Session check error,maybe the server was not reach.",
                SensitiveExceptionUtils.hideSensitiveInfo(e));
            return login();
        }
    }
}
