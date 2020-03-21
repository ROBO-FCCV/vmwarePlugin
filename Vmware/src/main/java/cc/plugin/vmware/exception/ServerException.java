/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.exception;

/**
 * 功能描述 全局异常
 *
 * @since 2019 -07-31
 */
public class ServerException extends Exception {
    private static final long serialVersionUID = -138165102435136498L;

    /**
     * Instantiates a new Server exception.
     */
    public ServerException() {
    }

    /**
     * Instantiates a new Server exception.
     *
     * @param message the message
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Server exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Server exception.
     *
     * @param cause the cause
     */
    public ServerException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Server exception.
     *
     * @param message the message
     * @param cause the cause
     * @param enableSuppression the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
