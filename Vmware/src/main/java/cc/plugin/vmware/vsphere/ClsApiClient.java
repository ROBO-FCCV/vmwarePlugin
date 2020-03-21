/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import com.vmware.content.Configuration;
import com.vmware.content.Library;
import com.vmware.content.LocalLibrary;
import com.vmware.content.Type;
import com.vmware.content.library.Item;
import com.vmware.content.library.item.Storage;
import com.vmware.content.library.item.UpdateSession;
import com.vmware.vapi.bindings.Service;
import com.vmware.vcenter.ovf.LibraryItem;
import com.vmware.vcloud.suite.samples.common.LookupServiceHelper;
import com.vmware.vcloud.suite.samples.common.PlatformServiceController;
import com.vmware.vcloud.suite.samples.common.SSOConnection;
import com.vmware.vcloud.suite.samples.vapi.endpoint.VapiServiceEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The type Cls api client.
 *
 * @since 2019 -10-15
 */
public class ClsApiClient {
    private String userName;
    private String password;
    private String serviceUrl;
    private VapiServiceEndpoint serviceEndPoint;
    private static final Logger logger = LoggerFactory.getLogger(ClsApiClient.class);

    /**
     * Instantiates a new Cls api client.
     *
     * @param hostname the hostname
     * @param userName the user name
     * @param password the password
     */
    public ClsApiClient(String hostname, String userName, String password) {
        this.serviceUrl = "https://" + getHost(hostname) + "/lookupservice/sdk";
        this.password = password;
        this.userName = userName;
    }

    /**
     * Login.
     */
    public void login() {
        try {
            PlatformServiceController platformServiceController = new PlatformServiceController(serviceUrl);
            try {
                platformServiceController.login(userName, password);
            } catch (Exception e) {
                throw new RuntimeException("Unable to login: ", e);
            }

            LookupServiceHelper lookupService = platformServiceController.getLsServiceHelper();
            String nodeId;
            try {
                nodeId = lookupService.getDefaultMgmtNode();
                assert lookupService.getMgmtNodeInstanceName(nodeId) != null;
            } catch (Exception e) {
                throw new RuntimeException("Cannot get default management node: ", e);
            }

            String vApiUrl = lookupService.findVapiUrl(nodeId);
            logger.info("URL :{}", vApiUrl);

            SSOConnection ssoConn = platformServiceController.getSsoConnection();
            serviceEndPoint = new VapiServiceEndpoint(vApiUrl);
            serviceEndPoint.login(ssoConn.getSamlBearerToken());
        } catch (Exception e) {
            logger.error("Login failed: ", e);
            throw new RuntimeException("Login failed: ", e);
        }
    }

    /**
     * Logout.
     */
    public void logout() {
        try {
        } catch (Exception e) {
            logger.error("logout failed", e);
        }

    }

    private <T extends Service> T getService(Class<T> serviceClass) {
        return (T) serviceEndPoint.getService(serviceClass);
    }

    /**
     * Library library.
     *
     * @return the library
     */
    public Library library() {
        return getService(Library.class);
    }

    /**
     * Type type.
     *
     * @return the type
     */
    public Type type() {
        return getService(Type.class);
    }

    /**
     * Item item.
     *
     * @return the item
     */
    public Item item() {
        return getService(Item.class);
    }

    /**
     * Update session update session.
     *
     * @return the update session
     */
    public UpdateSession updateSession() {
        return getService(UpdateSession.class);
    }

    /**
     * Update session file com . vmware . content . library . item . updatesession . file.
     *
     * @return the com . vmware . content . library . item . updatesession . file
     */
    public com.vmware.content.library.item.updatesession.File updateSessionFile() {
        return getService(com.vmware.content.library.item.updatesession.File.class);
    }

    /**
     * Ovf library item library item.
     *
     * @return the library item
     */
    public LibraryItem ovfLibraryItem() {
        return getService(LibraryItem.class);
    }

    /**
     * Configuration configuration.
     *
     * @return the configuration
     */
    public Configuration configuration() {
        return getService(Configuration.class);
    }

    /**
     * Storage storage.
     *
     * @return the storage
     */
    public Storage storage() {
        return getService(Storage.class);
    }

    /**
     * Local library local library.
     *
     * @return the local library
     */
    public LocalLibrary localLibrary() {
        return getService(LocalLibrary.class);
    }

    private static String getHost(String ip) {
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
            return address.getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to lookup on ", e);
        }
    }
}
