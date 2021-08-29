/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

import com.vmware.sample.enums.RestCodeEnum;
import com.vmware.sample.exception.PluginException;
import com.vmware.sample.model.VMware;
import com.vmware.sample.util.IpAddressCheck;
import com.vmware.sample.util.SensitiveExceptionUtils;

import com.vmware.cis.Session;
import com.vmware.vapi.bindings.Service;
import com.vmware.vapi.bindings.StubConfiguration;
import com.vmware.vapi.bindings.StubFactory;
import com.vmware.vapi.cis.authn.ProtocolFactory;
import com.vmware.vapi.cis.authn.SecurityContextFactory;
import com.vmware.vapi.core.ApiProvider;
import com.vmware.vapi.core.ExecutionContext;
import com.vmware.vapi.protocol.HttpConfiguration;
import com.vmware.vapi.protocol.ProtocolConnection;
import com.vmware.vapi.security.SessionSecurityContext;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Vmware API implement
 *
 * @since 2020-09-14
 */
@Slf4j
@Getter
@Setter
public class VMwareAPI extends AbstractVMware {
    private static final int RETRY_TIMES = 3;
    private StubFactory stubFactory;
    private Session session;
    private VMware vMware;
    private StubConfiguration stubConfiguration;

    public VMwareAPI(VMware vMware) {
        this.vMware = vMware;
    }

    @Override
    public synchronized boolean login() {
        try {
            boolean reachable = IpAddressCheck.isReachable(vMware.getIp());
            if (!reachable) {
                throw new PluginException(RestCodeEnum.CONNECTION_EXCEPTION);
            }
            HttpConfiguration httpConfiguration = new HttpConfiguration.Builder().setSslConfiguration(
                new HttpConfiguration.SslConfiguration.Builder().disableCertificateValidation()
                    .disableHostnameVerification()
                    .getConfig()).getConfig();
            this.stubFactory = createApiStubFactory(httpConfiguration);
            // Create a security context with a user name and password
            ExecutionContext.SecurityContext securityContext = SecurityContextFactory.createUserPassSecurityContext(
                vMware.getUsername(), vMware.getPassword());
            // Creating Stub Configurations Using Security Contexts
            StubConfiguration stubConfig = new StubConfiguration(securityContext);
            // Creating a Session Using Stub Configuration
            Session stubSession = this.stubFactory.createStub(Session.class, stubConfig);
            // 新建会话
            char[] sessionId = stubSession.create();
            SessionSecurityContext sessionSecurityContext = new SessionSecurityContext(sessionId);
            // Add the security session to the stub configuration.
            stubConfig.setSecurityContext(sessionSecurityContext);
            // Use the authenticated stub configuration to set the session of the current object.
            this.session = this.stubFactory.createStub(Session.class, stubConfig);
            this.stubConfiguration = stubConfig;
            return true;
        } catch (Exception e) {
            log.error("The VMware {} login failed.please check configuration.", vMware.getIp(),
                SensitiveExceptionUtils.hideSensitiveInfo(e));
            return false;
        }
    }

    @Override
    public void logout() {
        if (this.session != null) {
            this.session.delete();
        }
    }

    @Override
    public boolean checkSession() {
        try {
            if (stubFactory == null || session == null) {
                return login();
            }
            session.get();
            return true;
        } catch (Exception e) {
            log.error("Session check error,maybe the server was not reach.",
                SensitiveExceptionUtils.hideSensitiveInfo(e));
            return login();
        }
    }

    /**
     * Get service
     *
     * @param clazz class
     * @param <T> T
     * @return instance of service
     */
    public <T extends Service> T getStubConfiguration(Class<? extends T> clazz) {
        return stubFactory.createStub(clazz, stubConfiguration);
    }

    private StubFactory createApiStubFactory(HttpConfiguration sslConfiguration) {
        ProtocolFactory pf = new ProtocolFactory();
        URI uri = UriComponentsBuilder.newInstance()
            .host(vMware.getIp())
            .port(vMware.getPort())
            .path("api")
            .scheme("https")
            .build()
            .toUri();
        // create session
        ProtocolConnection connection = pf.getHttpConnection(uri.toString(), null, sslConfiguration);
        // Initializing the stub factory using the API connection
        ApiProvider provider = connection.getApiProvider();
        return new StubFactory(provider);
    }
}
