/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import cc.plugin.vmware.exception.ApplicationException;

import com.vmware.common.ssl.TrustAllHostNameVerifier;
import com.vmware.common.ssl.TrustAllTrustManager;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * The type Service connection.
 *
 * @since 2019 -10-15
 */
public class ServiceConnection extends DefaultBasicConnection {

    /**
     * The constant ConnectionState_Connected.
     */
    public final static int CONNECTION_STATE_CONNECTED = 0;
    /**
     * The constant ConnectionState_Disconnected.
     */
    public final static int CONNECTION_STATE_DISCONNECTED = 1;
    private static final Logger logger = LoggerFactory.getLogger(ServiceConnection.class);
    /**
     * The Svc state.
     */
    protected int serviceConnectionState;

    /**
     * Instantiates a new Service connection.
     *
     * @param svcRefVal the svc ref val
     */
    public ServiceConnection(String svcRefVal) {
        super();
        serviceConnectionState = CONNECTION_STATE_DISCONNECTED;
    }

    /**
     * Create service connection service connection.
     *
     * @param inst the inst
     * @return the service connection
     */
    public static ServiceConnection createServiceConnection(String inst) {
        return new ServiceConnection(inst);
    }

    /**
     * Gets connect.
     *
     * @param urlStr the url str
     * @param username the username
     * @param password the password
     */
    public void getConnect(String urlStr, String username, String password) {
        setUrl(urlStr);
        setUsername(username);
        setPassword(password);
        try {
            new TrustAllTrustManager().register();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new ApplicationException("vmware register fail ", e);
        }
        new TrustAllHostNameVerifier().register();
        this.connect();
        serviceConnectionState = CONNECTION_STATE_CONNECTED;
    }

    /**
     * Check vmware connection.
     *
     * @param urlStr the url str
     * @param username the username
     * @param password the password
     */
    public void checkVmwareConnection(String urlStr, String username, String password) {
        setUrl(urlStr);
        setUsername(username);
        setPassword(password);
        try {
            new TrustAllTrustManager().register();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new ApplicationException("vmware register fail ", e);
        }
        new TrustAllHostNameVerifier().register();
        this.checkVmwareInfo();
        serviceConnectionState = CONNECTION_STATE_CONNECTED;
    }

    /**
     * Check connection boolean.
     *
     * @param serviceConnection the service connection
     * @return the boolean
     */
    public boolean checkConnection(ServiceConnection serviceConnection) {
        boolean connectionStatus;
        ServiceContent serviceContent;
        try {
            serviceContent = serviceConnection.getServiceContent();
        } catch (Exception e) {
            logger.info("get service content exception:", e);
            return false;
        }
        if (serviceContent == null) {
            return false;
        }
        ManagedObjectReference sessionManager;
        try {
            sessionManager = serviceContent.getSessionManager();
        } catch (Exception e) {
            logger.info("get session manager exception:", e);
            return false;
        }
        connectionStatus = getConnectionStatus(sessionManager, serviceConnection);
        return connectionStatus;
    }

    private boolean getConnectionStatus(ManagedObjectReference sessionManager, ServiceConnection serviceConnection) {
        boolean connectionStatus;
        String sessionId;
        String username;
        try {
            sessionId = serviceConnection.getUserSession().getKey();
        } catch (Exception e) {
            logger.info("get user session exception:", e);
            return false;
        }
        try {
            username = serviceConnection.getUsername();
        } catch (Exception e) {
            logger.info("get username exception:", e);
            return false;
        }
        try {
            connectionStatus = serviceConnection.getVimPort().sessionIsActive(sessionManager, sessionId, username);
        } catch (Exception e) {
            logger.info("get connection status exception:", e);
            return false;
        }
        return connectionStatus;
    }

    /**
     * isConnected
     *
     * @return boolean
     */
    public boolean isConnected() {
        return serviceConnectionState == CONNECTION_STATE_CONNECTED;
    }

    /**
     * Gets prop col.
     *
     * @return the prop col
     */
    public ManagedObjectReference getPropCol() {
        return getServiceContent().getPropertyCollector();
    }

    /**
     * Service state int.
     *
     * @return the int
     */
    public int serviceState() {
        return serviceConnectionState;
    }

    /**
     * Gets root folder.
     *
     * @return the root folder
     */
    public ManagedObjectReference getRootFolder() {
        return getServiceContent().getRootFolder();
    }

    /**
     * Gets service instance ref.
     *
     * @return the service instance ref
     */
    public ManagedObjectReference getServiceInstanceRef() {
        return getServiceInstanceReference();
    }
}
