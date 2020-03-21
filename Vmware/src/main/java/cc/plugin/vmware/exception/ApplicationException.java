/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.exception;

/**
 * The type Application exception.
 *
 * @since 2019 -09-19
 */
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 异常code
     */
    private String code;

    /**
     * 异常meassge
     */
    private String message;

    /**
     * Instantiates a new Application exception.
     *
     * @param msg the msg
     */
    public ApplicationException(String msg) {
        super(msg);
        this.message = msg;
    }

    /**
     * Instantiates a new Application exception.
     *
     * @param code the code
     * @param msg the msg
     */
    public ApplicationException(String code, String msg) {
        super(msg);
        this.code = code;
        this.message = msg;
    }

    /**
     * Instantiates a new Application exception.
     *
     * @param msg the msg
     * @param e the e
     */
    public ApplicationException(String msg, Exception e) {
        super(msg, e);
        this.message = msg;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
