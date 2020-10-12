/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.connection;

import cc.plugin.vmware.constant.Constant;
import cc.plugin.vmware.constant.ErrorCode;
import cc.plugin.vmware.exception.CustomException;
import cc.plugin.vmware.model.mo.LoginParamMo;
import cc.plugin.vmware.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The type App util.
 *
 * @since 2019 -10-15
 */
public class AppUtil {
    private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);

    private LoginParamMo loginParams;
    /**
     * The Connection.
     */
    protected ServiceConnection serviceConnection;
    /**
     * The Svc util.
     */
    protected ServiceUtil serviceUtil;

    private AtomicInteger connectVersion = new AtomicInteger(0);

    private ReadWriteLock connectLock = new ReentrantReadWriteLock();

    private Lock writeLock = connectLock.writeLock();

    /**
     * Instantiates a new App util.
     */
    public AppUtil() {
        setup();
    }

    private void setup() {
        serviceUtil = new ServiceUtil();
        serviceConnection = ServiceConnection.createServiceConnection("ServiceInstance");
    }

    /**
     * Init connection.
     */
    public void initConnection() {
        getServiceUtil().init(this, serviceConnection);
    }

    /**
     * Parse input.
     *
     * @param loginParamMo the login param mo
     */
    public void parseInput(LoginParamMo loginParamMo) {
        loginParams = loginParamMo;
    }

    private void reconnect(String url, String username, String password) throws CustomException {
        // IP检查
        String ip = this.getIp();
        if (!CommonUtil.ipCanBeConnectedDiffIpVersion(ip, Constant.IPV4)) {
            logger.error("The IP is unreachable");
            throw new CustomException(ErrorCode.CONNECT_VCENTER_ERROR_CODE, ErrorCode.CONNECT_VCENTER_ERROR_MSG);
        }
        int localVersion = connectVersion.get();
        writeLock.lock();
        try {
            if (localVersion != connectVersion.get()) {
                logger.info("Another thread has already finished connection operation");
                return;
            }
            serviceConnection.getConnect(url, username, password);
        } catch (Exception e) {
            logger.error("Connection exception", e);
            throw new CustomException(ErrorCode.CONNECT_VCENTER_ERROR_CODE, ErrorCode.CONNECT_VCENTER_ERROR_MSG);
        } finally {
            connectVersion.incrementAndGet();
            writeLock.unlock();
        }
    }

    /**
     * Connect.
     *
     * @throws CustomException the custom exception
     */
    public void connect() throws CustomException {
        logger.info("connect ... start");
        String url = this.getServiceUrl();
        String username = this.getUsername();
        String password = this.getPassword();
        if (isConnectActive()) {
            logger.info("Connection is active.");
        } else {
            logger.info("Connection is not active... connection start");
            reconnect(url, username, password);
        }
        initConnection();
        logger.info("connect ... end");
    }

    private boolean isConnectActive() {
        return serviceConnection.checkConnection(serviceConnection);
    }

    /**
     * Gets service util.
     *
     * @return the service util
     */
    public ServiceUtil getServiceUtil() {
        return serviceUtil;
    }

    /**
     * Gets service url.
     *
     * @return the service url
     */
    public String getServiceUrl() {
        return loginParams.getUrl();
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return loginParams.getUsername();
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return loginParams.getIp();
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return loginParams.getPassword();
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public ServiceConnection getConnection() {
        return serviceConnection;
    }
}
