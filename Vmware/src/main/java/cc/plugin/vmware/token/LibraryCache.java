/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.token;

import cc.plugin.vmware.vsphere.ClsApiClient;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 功能描述
 *
 * @since 2019 -09-25
 */
@Component
public class LibraryCache {
    private static final Logger logger = LoggerFactory.getLogger(LibraryCache.class);

    private static Cache<String, ClsApiClient> clients = CacheBuilder.newBuilder().build();

    /**
     * Sets client.
     *
     * @param vmwareId the vmware id
     * @param ip the ip
     * @param username the username
     * @param password the password
     * @return the client
     */
    public static ClsApiClient setClient(String vmwareId, String ip, String username, String password) {
        ClsApiClient clsApiClient = null;
        try {
            clsApiClient = clients.get(vmwareId, () -> {
                ClsApiClient client = new ClsApiClient(ip, username, password);
                client.login();
                logger.info("Logged in to Content Library API successfully.");
                return client;
            });
        } catch (Exception e) {
            logger.error("Get ClsApiClient failed, vmwareId: {}", vmwareId);
            logger.error("Get ClsApiClient failed:", e);
        }
        logger.info("Get client from cache successfully.");
        return clsApiClient;
    }

    /**
     * Gets client.
     *
     * @param vmwareId the vmware id
     * @return the client
     */
    public static ClsApiClient getClient(String vmwareId) {
        logger.info("Get client from cache successfully.");
        return clients.getIfPresent(vmwareId);
    }

}
