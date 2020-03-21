/*
 * Copyright (c). 2020-2020. All rights reserved.
 */

package cc.plugin.vmware.vsphere;

import com.vmware.vcloud.suite.samples.common.ServiceManager;

/**
 * The type Vim service manager.
 *
 * @since 2019 -09-19
 */
public final class VimServiceManager {

    /**
     * The Manager.
     */
    static ServiceManager manager;

    private VimServiceManager() {
    }

    /**
     * Dis connect.
     */
    public static void disConnect() {
        if (manager != null) {
            manager.disconnect();
        }
    }
}
