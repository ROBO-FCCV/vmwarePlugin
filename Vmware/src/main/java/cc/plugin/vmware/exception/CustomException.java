/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.exception;

/**
 * The type Custom exception.
 *
 * @since 2019 -09-19
 */
public class CustomException extends Exception {

    private static final long serialVersionUID = -5498436474008304513L;

    private String errorCode;

    /**
     * Instantiates a new Custom exception.
     *
     * @param errorCode the error code
     * @param errorMsg the error msg
     */
    public CustomException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
    }

    /**
     * Gets error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets error code.
     *
     * @param errorCode the error code
     * @return the error code
     */
    public CustomException setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}
