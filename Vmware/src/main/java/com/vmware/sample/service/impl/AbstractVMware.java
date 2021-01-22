/*
 * Copyright (c). 2021-2021. All rights reserved.
 */

package com.vmware.sample.service.impl;

/**
 * Abstract VMware
 *
 * @since 2020-09-14
 */
public abstract class AbstractVMware {
    /**
     * login
     *
     * @return Whether the login is successful
     */
    public abstract boolean login();

    /**
     * logout
     */
    public abstract void logout();

    /**
     * check session
     *
     * @return check session result
     */
    public abstract boolean checkSession();
}
